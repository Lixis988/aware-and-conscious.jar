package net.lixis.outofbound.block;

import net.lixis.outofbound.WorldProgressionServerHandler;
import net.lixis.outofbound.dimension.DimensionManager;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazeOverworldTeleport;
import net.lixis.outofbound.dimension.MazePortalPlacer;
import net.lixis.outofbound.dimension.MazeSafeSpawn;
import net.lixis.outofbound.entity.BlackSquareSpawnManager;
import net.lixis.outofbound.world.WorldGameStage;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ThreadLocalRandom;

public class OverworldPortalBlock extends Block {

	public OverworldPortalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(level, pos, state, entity);
		if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
			return;
		}
		if (MazeSafeSpawn.hasPortalCooldown(player)) {
			return;
		}
		if (!WorldInternalConfig.hasBoundedcowCollision(player.server)) {
			return;
		}

		WorldGameStage stage = WorldInternalConfig.getGameStage(player.server);
		if (stage != WorldGameStage.SINKING && stage != WorldGameStage.IN_MAZE) {
			return;
		}

		if (MazeDimensions.isMazeDimension(level.dimension().location())) {
			teleportToOverworld(player);
			return;
		}

		if (level.dimension() == Level.OVERWORLD) {
			teleportToMaze(player);
		}
	}

	private static void teleportToOverworld(ServerPlayer player) {
		MazeOverworldTeleport.teleportToOverworld(player);
	}

	private static void teleportToMaze(ServerPlayer player) {
		long index = WorldInternalConfig.getMazeIndex(player.server);
		if (index <= 0L) {
			index = player.getPersistentData().getLong("outofbound_maze_index");
		}
		if (index <= 0L) {
			index = ThreadLocalRandom.current().nextLong(1L, 1_000_000L);
			WorldInternalConfig.markTeleportedToMaze(player.server, index);
		}

		ServerLevel maze = DimensionManager.getOrCreateMazeLevel(player.server, index);
		if (maze == null) {
			return;
		}

		MazePortalPlacer.ensurePortal(maze);
		MazeSafeSpawn.teleportPlayerToMaze(player, maze);
		player.getPersistentData().putLong("outofbound_maze_index", index);
		WorldProgressionServerHandler.clearReturnDeadline(player);
		BlackSquareSpawnManager.trySpawnForPlayer(maze, player);
	}
}
