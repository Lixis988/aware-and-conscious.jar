package net.lixis.outofbound.world;

import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.entity.SkyFigureEntity;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class SkyFigureSpawner {

	private static final int PLAYER_CHUNK_RADIUS = 8;
	private static final int MIN_AIR_COLUMN = 8;
	private static final int DEFERRED_BLOCK_THRESHOLD = 100;

	private SkyFigureSpawner() {
	}

	public static void trySpawn(ServerLevel overworld) {
		MinecraftServer server = overworld.getServer();
		if (server == null || !WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}
		if (!MazeConfig.enableSkyFigures) {
			return;
		}

		SkyFigureData data = SkyFigureData.get(overworld);
		int activeEntities = countActiveEntities(overworld);
		if (data.activeBlockFigureCount() + activeEntities >= MazeConfig.skyFigureMaxActive) {
			return;
		}

		OverworldBleedData bleedData = OverworldBleedData.get(overworld);
		Set<Long> loadedKeys = bleedData.loadedChunkKeys();
		if (loadedKeys.isEmpty()) {
			return;
		}

		List<Long> candidates = buildCandidates(overworld, loadedKeys);
		if (candidates.isEmpty()) {
			return;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		Collections.shuffle(candidates, random);
		BlockPos anchor = null;
		for (long key : candidates) {
			ChunkPos chunkPos = OverworldBleedChunkSelector.unpackChunk(key);
			if (!overworld.hasChunk(chunkPos.x, chunkPos.z)) {
				continue;
			}
			anchor = pickSkyAnchor(overworld, chunkPos, random);
			if (anchor != null) {
				break;
			}
		}
		if (anchor == null) {
			return;
		}

		SkyFigureType type = SkyFigureType.random(random);
		int variantSeed = random.nextInt();
		long spawnTick = overworld.getGameTime();
		long expiresAt = spawnTick + MazeConfig.skyFigureLifespanTicks;

		if (type.isBlockBased()) {
			spawnBlockFigure(overworld, data, type, anchor, variantSeed, expiresAt);
		} else {
			spawnEntityFigure(overworld, type, anchor, variantSeed, spawnTick);
		}
	}

	private static List<Long> buildCandidates(ServerLevel overworld, Set<Long> loadedKeys) {
		List<ServerPlayer> players = overworld.players();
		List<Long> nearPlayers = new ArrayList<>();
		List<Long> fallback = new ArrayList<>();

		for (long key : loadedKeys) {
			ChunkPos chunkPos = OverworldBleedChunkSelector.unpackChunk(key);
			if (!overworld.hasChunk(chunkPos.x, chunkPos.z)) {
				continue;
			}
			fallback.add(key);
			if (players.isEmpty()) {
				continue;
			}
			for (ServerPlayer player : players) {
				ChunkPos playerChunk = player.chunkPosition();
				int distance = Math.max(Math.abs(chunkPos.x - playerChunk.x), Math.abs(chunkPos.z - playerChunk.z));
				if (distance <= PLAYER_CHUNK_RADIUS) {
					nearPlayers.add(key);
					break;
				}
			}
		}

		return nearPlayers.isEmpty() ? fallback : nearPlayers;
	}

	private static BlockPos pickSkyAnchor(ServerLevel level, ChunkPos chunkPos, ThreadLocalRandom random) {
		int x = chunkPos.getMinBlockX() + 8;
		int z = chunkPos.getMinBlockZ() + 8;
		int terrainY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		int minOffset = MazeConfig.skyFigureMinHeightAboveTerrain;
		int maxOffset = Math.max(minOffset, MazeConfig.skyFigureMaxHeightAboveTerrain);
		int offset = minOffset + random.nextInt(maxOffset - minOffset + 1);
		int y = terrainY + offset;
		BlockPos anchor = new BlockPos(x, y, z);
		if (!hasAirColumn(level, anchor)) {
			return null;
		}
		return anchor;
	}

	private static boolean hasAirColumn(ServerLevel level, BlockPos anchor) {
		for (int dy = 0; dy < MIN_AIR_COLUMN; dy++) {
			BlockPos pos = anchor.above(dy);
			if (!level.isLoaded(pos)) {
				return false;
			}
			BlockState state = level.getBlockState(pos);
			FluidState fluid = level.getFluidState(pos);
			if (!state.isAir() && !fluid.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static void spawnBlockFigure(ServerLevel level, SkyFigureData data, SkyFigureType type, BlockPos anchor,
			int variantSeed, long expiresAt) {
		List<BlockPos> positions = SkyFigureGenerators.generate(type, anchor, variantSeed);
		if (positions.isEmpty()) {
			return;
		}
		Runnable placement = () -> placeBlockFigure(level, data, positions, expiresAt);
		if (positions.size() > DEFERRED_BLOCK_THRESHOLD) {
			DeferredChunkWorkQueue.enqueue(placement);
		} else {
			placement.run();
		}
	}

	private static void placeBlockFigure(ServerLevel level, SkyFigureData data, List<BlockPos> positions, long expiresAt) {
		List<SkyFigureData.BlockSnapshot> snapshots = new ArrayList<>();
		for (BlockPos pos : positions) {
			if (!level.isLoaded(pos)) {
				continue;
			}
			BlockState original = level.getBlockState(pos);
			if (!original.canBeReplaced()) {
				continue;
			}
			level.setBlock(pos, net.minecraft.world.level.block.Blocks.BLACK_CONCRETE.defaultBlockState(), 3);
			snapshots.add(new SkyFigureData.BlockSnapshot(pos, original));
		}
		if (!snapshots.isEmpty()) {
			data.registerBlockFigure(snapshots, expiresAt);
		}
	}

	private static void spawnEntityFigure(ServerLevel level, SkyFigureType type, BlockPos anchor, int variantSeed,
			long spawnTick) {
		SkyFigureEntity entity = OutofboundExtraEntities.SKY_FIGURE.get().create(level);
		if (entity == null) {
			return;
		}
		int size = 3 + Math.floorMod(variantSeed, 4);
		entity.moveTo(anchor.getX() + 0.5D, anchor.getY() + 0.5D, anchor.getZ() + 0.5D, 0.0F, 0.0F);
		entity.configure(type.ordinal(), size, variantSeed, spawnTick);
		entity.getPersistentData().putBoolean(SkyFigureEntity.MANAGED_TAG, true);
		level.addFreshEntity(entity);
	}

	private static int countActiveEntities(ServerLevel level) {
		if (level.dimension() != Level.OVERWORLD) {
			return 0;
		}
		return level.getEntitiesOfClass(SkyFigureEntity.class,
				new AABB(-3.0E7, -2048.0D, -3.0E7, 3.0E7, 2048.0D, 3.0E7),
				entity -> entity.getPersistentData().getBoolean(SkyFigureEntity.MANAGED_TAG) && !entity.isRemoved()).size();
	}
}
