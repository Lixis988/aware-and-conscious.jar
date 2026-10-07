package net.lixis.outofbound;

import net.lixis.outofbound.dimension.DimensionManager;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazeSafeSpawn;
import net.lixis.outofbound.entity.BlackSquareSpawnManager;
import net.lixis.outofbound.world.PostEyeWeirdnessHandler;
import net.lixis.outofbound.world.FinaleState;
import net.lixis.outofbound.world.WorldGameStage;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class WorldProgressionServerHandler {

	@SuppressWarnings("unused")
	private static final String OWNERSHIP = Authorship.NOTICE;

	static final String RETURN_DEADLINE_TAG = "outofbound_maze_return_deadline";

	private WorldProgressionServerHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (server.getTickCount() == 20) {
			WorldInternalConfig.migrateIfNeeded(server);
		}

		if (server.getTickCount() % 20 != 0) {
			return;
		}

		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}

		FinaleState finaleState = WorldInternalConfig.getFinaleState(server);
		if (finaleState == FinaleState.ACTIVE) {
			return;
		}

		WorldGameStage stage = WorldInternalConfig.getGameStage(server);
		if (stage != WorldGameStage.SINKING && stage != WorldGameStage.IN_MAZE) {
			return;
		}

		if (server.getTickCount() % 100 == 0) {
			WorldProgressServerHandler.syncAllPlayers(server);
		}

		enforceOverworldReturns(server);
	}

	public static void handleOverworldReturn(ServerPlayer player) {
		handleOverworldReturn(player, true);
	}

	public static void handleOverworldReturn(ServerPlayer player, boolean refreshDeadline) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}

		WorldInternalConfig.migrateIfNeeded(server);
		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}
		if (MazeDimensions.isMazeDimension(player.level().dimension().location())) {
			return;
		}

		if (WorldInternalConfig.getNaturalMazeEntries(server) >= 2
				&& WorldInternalConfig.getFinaleState(server) == FinaleState.NONE) {
			WorldInternalConfig.setFinaleState(server, FinaleState.ACTIVE);
			WorldInternalConfig.setFinalePlayer(server, player);
			PostEyeWeirdnessHandler.onEyeEncounter(server);
			WorldProgressServerHandler.syncToPlayer(player);
			return;
		}

		if (WorldInternalConfig.getFinaleState(server) == FinaleState.ACTIVE) {
			WorldProgressServerHandler.syncToPlayer(player);
			return;
		}

		if (refreshDeadline) {
			markReturnDeadline(player);
		} else if (player.getPersistentData().getLong(RETURN_DEADLINE_TAG) <= 0L) {
			markReturnDeadline(player);
		}

		WorldGameStage stage = WorldInternalConfig.getGameStage(server);
		if (stage == WorldGameStage.SINKING || stage == WorldGameStage.IN_MAZE) {
			tryReturnPlayerToMaze(player);
		}
		WorldProgressServerHandler.syncToPlayer(player);
	}

	static void markReturnDeadline(ServerPlayer player) {
		long gameTime = player.server.overworld().getGameTime();
		player.getPersistentData().putLong(RETURN_DEADLINE_TAG, gameTime + WorldInternalConfig.SINK_DURATION_TICKS);
	}

	public static void clearReturnDeadline(ServerPlayer player) {
		player.getPersistentData().remove(RETURN_DEADLINE_TAG);
	}

	static void enforceOverworldReturns(MinecraftServer server) {
		FinaleState finaleState = WorldInternalConfig.getFinaleState(server);
		if (finaleState == FinaleState.ACTIVE) {
			return;
		}

		long gameTime = server.overworld().getGameTime();
		WorldGameStage stage = WorldInternalConfig.getGameStage(server);
		long sinkUntil = WorldInternalConfig.getSinkUntilTick(server);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!shouldProcessOverworldReturn(player)) {
				continue;
			}

			if (stage == WorldGameStage.IN_MAZE) {
				if (shouldForceMazeReturn(player, gameTime)) {
					if (returnPlayerToMaze(player)) {
						clearReturnDeadline(player);
					}
				}
				continue;
			}

			if (stage == WorldGameStage.SINKING && isReturnOverdue(player, gameTime, sinkUntil)) {
				tryReturnPlayerToMaze(player);
			}
		}

		if (stage == WorldGameStage.SINKING && gameTime >= sinkUntil) {
			teleportWorldToRandomMaze(server);
		}
	}

	static void tryReturnPlayerToMaze(ServerPlayer player) {
		if (!shouldProcessOverworldReturn(player)) {
			return;
		}

		MinecraftServer server = player.getServer();
		if (WorldInternalConfig.getFinaleState(server) == FinaleState.ACTIVE) {
			return;
		}
		WorldGameStage stage = WorldInternalConfig.getGameStage(server);
		long gameTime = server.overworld().getGameTime();
		long sinkUntil = WorldInternalConfig.getSinkUntilTick(server);

		if (stage == WorldGameStage.IN_MAZE) {
			if (shouldForceMazeReturn(player, gameTime)) {
				if (returnPlayerToMaze(player)) {
					clearReturnDeadline(player);
				}
			}
			return;
		}

		if (stage == WorldGameStage.SINKING && isReturnOverdue(player, gameTime, sinkUntil)) {
			if (gameTime >= sinkUntil) {
				teleportWorldToRandomMaze(server);
			} else if (returnPlayerToMaze(player)) {
				clearReturnDeadline(player);
			}
		}
	}

	private static boolean shouldProcessOverworldReturn(ServerPlayer player) {
		return !MazeDimensions.isMazeDimension(player.level().dimension().location())
				&& player.isAlive()
				&& !player.isSpectator();
	}

	private static boolean shouldForceMazeReturn(ServerPlayer player, long gameTime) {
		long personalDeadline = player.getPersistentData().getLong(RETURN_DEADLINE_TAG);
		if (personalDeadline <= 0L) {
			return true;
		}
		return gameTime >= personalDeadline;
	}

	private static boolean isReturnOverdue(ServerPlayer player, long gameTime, long sinkUntil) {
		long personalDeadline = player.getPersistentData().getLong(RETURN_DEADLINE_TAG);
		return gameTime >= sinkUntil || (personalDeadline > 0L && gameTime >= personalDeadline);
	}

	private static boolean returnPlayerToMaze(ServerPlayer player) {
		MinecraftServer server = player.getServer();
		long index = WorldInternalConfig.getMazeIndex(server);
		if (index <= 0L) {
			return teleportSingleOverworldPlayer(player);
		}

		ServerLevel maze = DimensionManager.getOrCreateMazeLevel(server, index);
		if (maze == null) {
			return false;
		}

		net.lixis.outofbound.dimension.MazePortalPlacer.ensurePortal(maze);
		teleportIntoMaze(player, maze, index);
		BlackSquareSpawnManager.trySpawnForPlayer(maze, player);
		return !MazeDimensions.isMazeDimension(player.level().dimension().location());
	}

	private static boolean teleportSingleOverworldPlayer(ServerPlayer player) {
		MinecraftServer server = player.getServer();
		long index = ThreadLocalRandom.current().nextLong(1L, 1_000_000L);
		ServerLevel maze = DimensionManager.getOrCreateMazeLevel(server, index);
		if (maze == null) {
			OutofboundMod.LOGGER.error("[outofbound] Failed to create maze dimension {} for {}", index, player.getGameProfile().getName());
			return false;
		}

		WorldInternalConfig.markTeleportedToMaze(server, index);
		net.lixis.outofbound.dimension.MazePortalPlacer.ensurePortal(maze);
		teleportIntoMaze(player, maze, index);
		BlackSquareSpawnManager.trySpawnForPlayer(maze, player);
		WorldProgressServerHandler.syncAllPlayers(server);
		return !MazeDimensions.isMazeDimension(player.level().dimension().location());
	}

	private static void teleportWorldToRandomMaze(MinecraftServer server) {
		long index = ThreadLocalRandom.current().nextLong(1L, 1_000_000L);
		ServerLevel maze = DimensionManager.getOrCreateMazeLevel(server, index);
		if (maze == null) {
			OutofboundMod.LOGGER.error("[outofbound] Failed to create maze dimension {}", index);
			return;
		}

		WorldInternalConfig.markTeleportedToMaze(server, index);
		net.lixis.outofbound.dimension.MazePortalPlacer.ensurePortal(maze);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!shouldProcessOverworldReturn(player)) {
				continue;
			}
			teleportIntoMaze(player, maze, index);
			clearReturnDeadline(player);
		}

		WorldProgressServerHandler.syncAllPlayers(server);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.level() == maze) {
				BlackSquareSpawnManager.trySpawnForPlayer(maze, player);
			}
		}
	}

	private static void teleportIntoMaze(ServerPlayer player, ServerLevel maze, long index) {
		MazeSafeSpawn.teleportPlayer(player, maze);
		player.getPersistentData().putLong("outofbound_maze_index", index);
		WorldInternalConfig.incrementNaturalMazeEntries(player.server);
	}
}
