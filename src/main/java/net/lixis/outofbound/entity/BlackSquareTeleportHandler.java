package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.DimensionManager;
import net.lixis.outofbound.dimension.MazePortalPlacer;
import net.lixis.outofbound.dimension.MazeSafeSpawn;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ThreadLocalRandom;

public final class BlackSquareTeleportHandler {

	private BlackSquareTeleportHandler() {
	}

	public static void teleportAllPlayers(MinecraftServer server) {
		long newIndex = ThreadLocalRandom.current().nextLong(1L, 1_000_000L);
		ServerLevel maze = DimensionManager.getOrCreateMazeLevel(server, newIndex);
		if (maze == null) {
			OutofboundMod.LOGGER.error("[outofbound] Black square failed to create maze dimension {}", newIndex);
			return;
		}

		WorldInternalConfig.markTeleportedToMaze(server, newIndex);
		WorldInternalConfig.incrementNaturalMazeEntries(server);
		MazePortalPlacer.ensurePortal(maze);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			MazeSafeSpawn.teleportPlayer(player, maze);
			player.getPersistentData().putLong("outofbound_maze_index", newIndex);
		}
	}
}
