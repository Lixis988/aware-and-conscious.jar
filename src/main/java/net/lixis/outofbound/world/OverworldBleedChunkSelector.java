package net.lixis.outofbound.world;

import net.lixis.outofbound.dimension.MazeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class OverworldBleedChunkSelector {

	private OverworldBleedChunkSelector() {
	}

	public static long packChunk(int chunkX, int chunkZ) {
		return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
	}

	public static ChunkPos unpackChunk(long key) {
		return new ChunkPos((int) (key >> 32), (int) key);
	}

	public static ChunkPos selectLoadedChunk(long worldSeed, int slotIndex, BlockPos spawnPos, int excludeSpawnRadius,
			Set<Long> loadedKeys, Set<Long> corruptedKeys) {
		if (loadedKeys.isEmpty()) {
			return null;
		}

		int spawnChunkX = spawnPos.getX() >> 4;
		int spawnChunkZ = spawnPos.getZ() >> 4;
		List<Long> candidates = new ArrayList<>();
		for (long key : loadedKeys) {
			if (corruptedKeys.contains(key)) {
				continue;
			}
			ChunkPos pos = unpackChunk(key);
			int distance = Math.max(Math.abs(pos.x - spawnChunkX), Math.abs(pos.z - spawnChunkZ));
			if (distance <= excludeSpawnRadius) {
				continue;
			}
			candidates.add(key);
		}

		if (candidates.isEmpty()) {
			return null;
		}

		int index = Math.floorMod(mixSeed(worldSeed, slotIndex), candidates.size());
		return unpackChunk(candidates.get(index));
	}

	public static long dimensionIndexForChunk(long worldSeed, int chunkX, int chunkZ) {
		long hash = worldSeed ^ ((long) chunkX * 34187312853L) ^ ((long) chunkZ * 1327217885L);
		hash *= 2862933555777941757L;
		hash ^= hash >>> 32;
		return 1L + Math.floorMod(hash, 999_999L);
	}

	public static boolean shouldGenerateAsMaze(long worldSeed, int chunkX, int chunkZ, double chance) {
		if (chance <= 0.0D) {
			return false;
		}
		if (!isOnSelectionGrid(chunkX, chunkZ, MazeConfig.bleedNewChunkGridSpacing)) {
			return false;
		}
		if (chance >= 1.0D) {
			return true;
		}
		long hash = worldSeed ^ ((long) chunkX * 912238459L) ^ ((long) chunkZ * 784558337L) ^ 0x4D415A4547454E4CL;
		hash *= 2862933555777941757L;
		hash ^= hash >>> 32;
		return Math.floorMod(hash, 10_000L) < (long) (chance * 10_000.0D);
	}

	public static boolean isOnSelectionGrid(int chunkX, int chunkZ, int spacing) {
		if (spacing <= 1) {
			return true;
		}
		return Math.floorMod(chunkX, spacing) == 0 && Math.floorMod(chunkZ, spacing) == 0;
	}

	private static int mixSeed(long worldSeed, int slotIndex) {
		long hash = worldSeed ^ ((long) slotIndex * 0x9E3779B97F4A7C15L);
		hash *= 2862933555777941757L;
		hash ^= hash >>> 32;
		return (int) hash;
	}
}
