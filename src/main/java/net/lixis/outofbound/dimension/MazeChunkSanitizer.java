package net.lixis.outofbound.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class MazeChunkSanitizer {

	private static final BlockState REPLACEMENT = Blocks.STONE.defaultBlockState();

	private MazeChunkSanitizer() {
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (!(event.getLevel() instanceof ServerLevel level)) {
			return;
		}
		if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
			return;
		}
		if (!(event.getChunk() instanceof LevelChunk chunk)) {
			return;
		}
		sanitize(chunk);
	}

	private static void sanitize(LevelChunk chunk) {
		LevelChunkSection[] sections = chunk.getSections();
		boolean dirty = false;
		for (LevelChunkSection section : sections) {
			if (section == null || section.hasOnlyAir()) {
				continue;
			}
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						BlockState state = section.getBlockState(x, y, z);
						if (!needsScrub(state)) {
							continue;
						}
						section.setBlockState(x, y, z, REPLACEMENT, false);
						dirty = true;
					}
				}
			}
		}

		List<BlockPos> orphanTes = new ArrayList<>();
		for (BlockPos pos : chunk.getBlockEntities().keySet()) {
			if (!chunk.getBlockState(pos).hasBlockEntity()) {
				orphanTes.add(pos.immutable());
			}
		}
		for (BlockPos pos : orphanTes) {
			chunk.removeBlockEntity(pos);
			dirty = true;
		}

		if (dirty) {
			chunk.setUnsaved(true);
		}
	}

	static boolean needsScrub(BlockState state) {
		if (state.isAir()) {
			return false;
		}
		var block = state.getBlock();
		return block instanceof MovingPistonBlock
				|| block instanceof PistonHeadBlock
				|| block == Blocks.MOVING_PISTON
				|| block == Blocks.PISTON_HEAD;
	}
}
