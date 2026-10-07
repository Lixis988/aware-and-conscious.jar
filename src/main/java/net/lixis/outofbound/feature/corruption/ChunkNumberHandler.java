package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.feature.corruption.util.ChunkNumberUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;
import java.util.Map;

public final class ChunkNumberHandler {

	private static final int RESTORE_TICKS = 200;

	public record Result(int chunkX, int chunkZ, int fromDigit, int toDigit, int blockStatesChanged,
			int blockEntitiesChanged, int numericFieldsChanged) {
	}

	private ChunkNumberHandler() {
	}

	public static Result corruptLoadedChunk(ServerPlayer player, int fromDigit, int toDigit,
			@Nullable ChunkPos requestedChunk) {
		ServerLevel level = player.serverLevel();
		ChunkPos chunkPos = resolveChunk(player, requestedChunk);
		if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
			return new Result(chunkPos.x, chunkPos.z, fromDigit, toDigit, 0, 0, 0);
		}

		ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);
		int blockStatesChanged = 0;
		int blockEntitiesChanged = 0;
		int numericFieldsChanged = 0;

		int minY = level.getMinBuildHeight();
		int maxY = level.getMaxBuildHeight();
		int baseX = chunkPos.getMinBlockX();
		int baseZ = chunkPos.getMinBlockZ();

		for (int localX = 0; localX < 16; localX++) {
			for (int localZ = 0; localZ < 16; localZ++) {
				for (int y = minY; y < maxY; y++) {
					BlockPos pos = new BlockPos(baseX + localX, y, baseZ + localZ);
					BlockState state = chunk.getBlockState(pos);
					if (state.isAir()) {
						continue;
					}

					BlockState updated = applyDigitReplacement(state, fromDigit, toDigit);
					if (updated != state) {
						CorruptionSnapshots.scheduleBlockRestore(level.dimension(), pos.immutable(), state, updated, RESTORE_TICKS);
						level.setBlockAndUpdate(pos, updated);
						blockStatesChanged++;
						numericFieldsChanged++;
					}
				}
			}
		}

		if (chunk instanceof LevelChunk levelChunk) {
			for (BlockEntity blockEntity : levelChunk.getBlockEntities().values()) {
				BlockPos pos = blockEntity.getBlockPos();
				net.minecraft.nbt.CompoundTag original = blockEntity.saveWithFullMetadata();
				net.minecraft.nbt.CompoundTag working = original.copy();
				int tagChanges = ChunkNumberUtil.replaceAllNumericTags(working, fromDigit, toDigit);
				if (tagChanges <= 0) {
					continue;
				}

				CorruptionSnapshots.scheduleBlockEntityRestore(level.dimension(), pos.immutable(), original, RESTORE_TICKS);
				blockEntity.load(working);
				blockEntity.setChanged();
				level.sendBlockUpdated(pos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
				blockEntitiesChanged++;
				numericFieldsChanged += tagChanges;
			}
		}

		return new Result(chunkPos.x, chunkPos.z, fromDigit, toDigit, blockStatesChanged, blockEntitiesChanged,
				numericFieldsChanged);
	}

	private static ChunkPos resolveChunk(ServerPlayer player, @Nullable ChunkPos requestedChunk) {
		if (requestedChunk != null) {
			return requestedChunk;
		}
		return player.chunkPosition();
	}

	private static BlockState applyDigitReplacement(BlockState state, int fromDigit, int toDigit) {
		BlockState updated = state;
		for (Property<?> property : state.getProperties()) {
			if (!(property instanceof IntegerProperty intProperty)) {
				continue;
			}
			int current = updated.getValue(intProperty);
			int replaced = ChunkNumberUtil.replaceAllDigits(current, fromDigit, toDigit);
			replaced = ChunkNumberUtil.nearestValidPropertyValue(replaced, intProperty, current);
			if (replaced != current) {
				updated = updated.setValue(intProperty, replaced);
			}
		}
		return updated;
	}
}
