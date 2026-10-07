package net.lixis.outofbound.block;

import net.lixis.outofbound.dimension.DimensionManager;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazePortalPlacer;
import net.lixis.outofbound.dimension.MazeSafeSpawn;
import net.lixis.outofbound.entity.BlackSquareSpawnManager;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionPortalBlock extends Block {

	public DimensionPortalBlock(Properties properties) {
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

		ServerLevel current = (ServerLevel) level;
		long index = MazeDimensions.indexFromLocation(current.dimension().location());
		long nextIndex = index < 0L ? 1L : index + 1L;

		ServerLevel next = DimensionManager.getOrCreateMazeLevel(player.server, nextIndex);
		if (next == null) {
			return;
		}

		MazeSafeSpawn.teleportPlayer(player, next);
		player.getPersistentData().putLong("outofbound_maze_index", nextIndex);
		WorldInternalConfig.markTeleportedToMaze(player.server, nextIndex);
		MazePortalPlacer.ensurePortal(next);
		BlackSquareSpawnManager.trySpawnForPlayer(next, player);
	}
}
