package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.block.OutofboundBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MazeSafeSpawn {

	public static final double DEFAULT_X = 1.5D;
	public static final double DEFAULT_Y = 1.0D;
	public static final double DEFAULT_Z = 1.5D;
	public static final double MAZE_SPAWN_HINT_X = 9.5D;
	public static final double MAZE_SPAWN_HINT_Z = 9.5D;

	private static final int SEARCH_RADIUS = 28;
	private static final int PORTAL_CLEARANCE = 2;
	private static final String PORTAL_COOLDOWN_TAG = "outofbound_portal_cooldown";
	private static final int PORTAL_COOLDOWN_TICKS = 20;

	private MazeSafeSpawn() {
	}

	public static void teleportPlayer(ServerPlayer player, ServerLevel level) {
		teleportPlayer(player, level, DEFAULT_X, DEFAULT_Y, DEFAULT_Z);
	}

	public static void teleportPlayerToMaze(ServerPlayer player, ServerLevel maze) {
		teleportPlayer(player, maze, MAZE_SPAWN_HINT_X, DEFAULT_Y, MAZE_SPAWN_HINT_Z);
		for (int attempt = 0; attempt < 4 && isStandingOnPortal(player); attempt++) {
			Vec3 respawn = findSafeSpawn(maze, player, player.getX() + 6.0D, player.getY(), player.getZ() + 6.0D);
			applyTeleport(player, maze, respawn);
		}
	}

	public static void teleportPlayer(ServerPlayer player, ServerLevel level, double hintX, double hintY, double hintZ) {
		Vec3 spawn = findSafeSpawn(level, player, hintX, hintY, hintZ);
		applyTeleport(player, level, spawn);
	}

	public static boolean isStandingOnPortal(ServerPlayer player) {
		if (!(player.level() instanceof ServerLevel level)) {
			return false;
		}
		BlockPos feet = player.blockPosition();
		if (isPortalBlock(level.getBlockState(feet.below())) || isPortalBlock(level.getBlockState(feet))) {
			return true;
		}
		return touchesPortalNearFeet(level, feet);
	}

	private static void applyTeleport(ServerPlayer player, ServerLevel level, Vec3 spawn) {
		player.setDeltaMovement(0.0D, 0.0D, 0.0D);
		player.teleportTo(level, spawn.x, spawn.y, spawn.z, player.getYRot(), player.getXRot());
		player.gameMode.setLevel(level);
		player.resetFallDistance();
		player.getPersistentData().putInt(PORTAL_COOLDOWN_TAG, PORTAL_COOLDOWN_TICKS);
	}

	public static boolean hasPortalCooldown(ServerPlayer player) {
		return player.getPersistentData().getInt(PORTAL_COOLDOWN_TAG) > 0;
	}

	public static void tickPortalCooldown(ServerPlayer player) {
		int ticks = player.getPersistentData().getInt(PORTAL_COOLDOWN_TAG);
		if (ticks > 0) {
			player.getPersistentData().putInt(PORTAL_COOLDOWN_TAG, ticks - 1);
		}
	}

	public static Vec3 findSafeSpawn(ServerLevel level, ServerPlayer player, double hintX, double hintY, double hintZ) {
		int centerX = Mth.floor(hintX);
		int centerZ = Mth.floor(hintZ);
		int hintFeetY = Mth.floor(hintY);

		int[] floorFeetYs = MazeFloors.mazeFloorFeetYs(level);
		int[] yPriority = MazeFloors.orderFeetYsByHint(floorFeetYs, hintFeetY);

		Vec3 best = null;
		double bestScore = Double.MAX_VALUE;

		for (int ring = 0; ring <= SEARCH_RADIUS; ring++) {
			for (int dx = -ring; dx <= ring; dx++) {
				for (int dz = -ring; dz <= ring; dz++) {
					if (ring > 0 && Math.abs(dx) != ring && Math.abs(dz) != ring) {
						continue;
					}

					int x = centerX + dx;
					int z = centerZ + dz;
					ensureChunkLoaded(level, x, z);

					for (int feetY : yPriority) {
						BlockPos feet = new BlockPos(x, feetY, z);
						if (!isSafeSpawn(level, player, feet)) {
							continue;
						}

						double score = horizontalDistanceSq(dx, dz) + 0.35D * Math.abs(feetY - hintFeetY);
						if (score < bestScore) {
							bestScore = score;
							best = spawnCenter(feet);
						}
					}
				}
			}

			if (best != null) {
				return best;
			}
		}

		OutofboundMod.LOGGER.warn("[outofbound] No safe maze spawn found near ({}, {}, {}), using fallback scan",
				hintX, hintY, hintZ);
		return fallbackSpawn(level, player);
	}

	private static Vec3 fallbackSpawn(ServerLevel level, ServerPlayer player) {
		for (int feetY : MazeFloors.mazeFloorFeetYs(level)) {
			for (int x = 0; x <= 8; x++) {
				for (int z = 0; z <= 8; z++) {
					BlockPos feet = new BlockPos(x, feetY, z);
					ensureChunkLoaded(level, x, z);
					if (isSafeSpawn(level, player, feet)) {
						return spawnCenter(feet);
					}
				}
			}
		}

		int y = level.getMinBuildHeight() + 2;
		return new Vec3(DEFAULT_X, y, DEFAULT_Z);
	}

	private static boolean isSafeSpawn(ServerLevel level, ServerPlayer player, BlockPos feet) {
		if (feet.getY() < level.getMinBuildHeight() + 1) {
			return false;
		}

		BlockPos floor = feet.below();
		BlockState floorState = level.getBlockState(floor);
		if (!floorState.isSolidRender(level, floor) || isPortalBlock(floorState)) {
			return false;
		}

		if (!isPassable(level, feet) || !isPassable(level, feet.above())) {
			return false;
		}

		if (isPortalBlock(level.getBlockState(feet)) || touchesPortalNearFeet(level, feet)) {
			return false;
		}

		Vec3 center = spawnCenter(feet);
		if (!fitsPlayer(level, player, center)) {
			return false;
		}

		return true;
	}

	private static boolean touchesPortalNearFeet(ServerLevel level, BlockPos feet) {
		for (int dx = -PORTAL_CLEARANCE; dx <= PORTAL_CLEARANCE; dx++) {
			for (int dz = -PORTAL_CLEARANCE; dz <= PORTAL_CLEARANCE; dz++) {
				if (isPortalBlock(level.getBlockState(feet.below().offset(dx, 0, dz)))) {
					return true;
				}
				if (isPortalBlock(level.getBlockState(feet.offset(dx, 0, dz)))) {
					return true;
				}
				if (isPortalBlock(level.getBlockState(feet.above().offset(dx, 0, dz)))) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isPassable(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.isAir() || !state.isSolidRender(level, pos);
	}

	private static boolean fitsPlayer(ServerLevel level, ServerPlayer player, Vec3 center) {
		EntityDimensions dimensions = player.getDimensions(Pose.STANDING);
		AABB box = dimensions.makeBoundingBox(center.x, center.y, center.z);
		return !level.containsAnyLiquid(box) && level.noCollision(player, box);
	}

	private static Vec3 spawnCenter(BlockPos feet) {
		return new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D);
	}

	private static void ensureChunkLoaded(ServerLevel level, int x, int z) {
		level.getChunk(x >> 4, z >> 4);
	}

	private static double horizontalDistanceSq(int dx, int dz) {
		return (double) dx * dx + (double) dz * dz;
	}

	private static boolean isPortalBlock(BlockState state) {
		return state.is(OutofboundBlocks.DIMENSION_PORTAL.get())
				|| state.is(OutofboundBlocks.OVERWORLD_PORTAL.get());
	}
}
