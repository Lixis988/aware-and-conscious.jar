package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.entity.BlackSquareSpawnManager;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ThreadLocalRandom;

public final class MazeTeleportUtil {

	private static final long MIN_MAZE_INDEX = 1L;
	private static final long MAX_MAZE_INDEX = 1_000_000L;

	private MazeTeleportUtil() {
	}

	public static void teleportToRandomMaze(ServerPlayer player, long avoidIndex) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		long index = pickRandomMazeIndex(random, avoidIndex);
		teleportToMaze(player, index);
	}

	public static void teleportToMaze(ServerPlayer player, long index) {
		MinecraftServer server = player.server;
		ServerLevel maze = DimensionManager.getOrCreateMazeLevel(server, index);
		if (maze == null) {
			OutofboundMod.LOGGER.error("[outofbound] Failed to create maze dimension {}", index);
			return;
		}

		WorldInternalConfig.markTeleportedToMaze(server, index);
		WorldInternalConfig.incrementNaturalMazeEntries(server);
		MazePortalPlacer.ensurePortal(maze);
		MazeSafeSpawn.teleportPlayerToMaze(player, maze);
		player.getPersistentData().putLong("outofbound_maze_index", index);
		BlackSquareSpawnManager.trySpawnForPlayer(maze, player);
	}

	public static long pickRandomMazeIndex(ThreadLocalRandom random, long avoidIndex) {
		long index = random.nextLong(MIN_MAZE_INDEX, MAX_MAZE_INDEX);
		if (avoidIndex < MIN_MAZE_INDEX) {
			return index;
		}
		int attempts = 0;
		while (index == avoidIndex && attempts < 8) {
			index = random.nextLong(MIN_MAZE_INDEX, MAX_MAZE_INDEX);
			attempts++;
		}
		return index;
	}
}
