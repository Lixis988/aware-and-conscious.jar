package net.lixis.outofbound.entity;

import net.lixis.outofbound.init.OutofboundModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public final class CaveUndefiendSpawnManager {

	private static final int POSITION_ATTEMPTS = 16;
	private static final double MIN_SPAWN_DISTANCE = 6.0D;
	private static final double MAX_SPAWN_DISTANCE = 22.0D;
	private static final int NEARBY_CAP_RADIUS = 96;
	private static final int REQUIRED_HEADROOM = 4;
	private static final int VERTICAL_SEARCH_ABOVE = 4;
	private static final int VERTICAL_SEARCH_BELOW = 16;

	private CaveUndefiendSpawnManager() {
	}

	public static boolean trySpawnNearPlayer(ServerLevel overworld, ServerPlayer player) {
		if (overworld.dimension() != Level.OVERWORLD) {
			return false;
		}
		if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
			return false;
		}
		if (!isInCave(overworld, player.blockPosition())) {
			return false;
		}
		if (countManagedNear(overworld, player) > 0) {
			return false;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		SpawnCandidate candidate = findSpawnPosition(overworld, player, random);
		if (candidate == null) {
			return false;
		}

		CaveUndefiendEntity entity = OutofboundModEntities.CAVE_UNDEFIEND.get().create(overworld);
		if (entity == null) {
			return false;
		}

		entity.moveTo(candidate.position().x, candidate.position().y, candidate.position().z, candidate.yaw(), 0.0F);
		entity.getPersistentData().putBoolean(CaveUndefiendEntity.MANAGED_TAG, true);
		overworld.addFreshEntity(entity);
		return true;
	}

	public static boolean isInCave(ServerLevel level, BlockPos feet) {
		if (level.canSeeSky(feet) || level.canSeeSky(feet.above())) {
			return false;
		}
		int skyLight = level.getMaxLocalRawBrightness(feet);
		return skyLight <= 7;
	}

	private static int countManagedNear(ServerLevel level, ServerPlayer player) {
		AABB box = player.getBoundingBox().inflate(NEARBY_CAP_RADIUS);
		return level.getEntitiesOfClass(CaveUndefiendEntity.class, box,
				mob -> mob.getPersistentData().getBoolean(CaveUndefiendEntity.MANAGED_TAG) && !mob.isRemoved()).size();
	}

	@Nullable
	private static SpawnCandidate findSpawnPosition(ServerLevel level, ServerPlayer player, ThreadLocalRandom random) {
		int playerY = player.getBlockY();
		for (int attempt = 0; attempt < POSITION_ATTEMPTS; attempt++) {
			double angle = random.nextDouble() * Math.PI * 2.0D;
			double distance = random.nextDouble(MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE);
			double x = player.getX() + Math.cos(angle) * distance;
			double z = player.getZ() + Math.sin(angle) * distance;
			int blockX = Mth.floor(x);
			int blockZ = Mth.floor(z);
			if (!level.hasChunk(blockX >> 4, blockZ >> 4)) {
				continue;
			}

			BlockPos feet = findFeetOnGround(level, blockX, blockZ, playerY);
			if (feet == null) {
				continue;
			}
			if (!isInCave(level, feet)) {
				continue;
			}

			float yaw = yawToward(player, feet);
			return new SpawnCandidate(new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D), yaw);
		}
		return null;
	}

	@Nullable
	private static BlockPos findFeetOnGround(ServerLevel level, int blockX, int blockZ, int anchorY) {
		int minY = level.getMinBuildHeight() + 1;
		int maxY = Math.min(anchorY + VERTICAL_SEARCH_ABOVE, level.getMaxBuildHeight() - REQUIRED_HEADROOM - 1);
		int searchFrom = maxY;
		int searchTo = Math.max(minY, anchorY - VERTICAL_SEARCH_BELOW);

		for (int y = searchFrom; y >= searchTo; y--) {
			BlockPos feet = new BlockPos(blockX, y, blockZ);
			if (hasSolidGroundAndHeadroom(level, feet)) {
				return feet;
			}
		}
		return null;
	}

	private static boolean hasSolidGroundAndHeadroom(ServerLevel level, BlockPos feet) {
		BlockPos ground = feet.below();
		BlockState floor = level.getBlockState(ground);
		if (!floor.isSolidRender(level, ground)) {
			return false;
		}
		for (int offset = 0; offset < REQUIRED_HEADROOM; offset++) {
			if (!level.getBlockState(feet.above(offset)).isAir()) {
				return false;
			}
		}
		return true;
	}

	private static float yawToward(ServerPlayer player, BlockPos spawnPos) {
		Vec3 delta = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
		return (float) (Mth.atan2(-delta.x, delta.z) * (180.0D / Math.PI));
	}

	private record SpawnCandidate(Vec3 position, float yaw) {
	}
}
