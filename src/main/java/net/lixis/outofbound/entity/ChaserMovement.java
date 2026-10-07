package net.lixis.outofbound.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ChaserMovement {

	private static final double BREAK_RADIUS = 1.0D;

	private ChaserMovement() {
	}

	public static Vec3 chaseTravelInput(Mob mob) {
		if (mob instanceof OverworldStalker stalker) {
			if (!stalker.isActivated()) {
				return null;
			}
		} else if (!(mob instanceof ChaserEntity) && !(mob instanceof BlackSquareEntity)
				&& !(mob instanceof UnknownEntity)) {
			return null;
		}

		Player player = findTarget(mob);
		if (player == null) {
			return null;
		}

		mob.setTarget(player);
		mob.getNavigation().stop();

		Vec3 delta = player.position().subtract(mob.position());
		double horizontalDistance = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

		if (horizontalDistance > 0.001D) {
			float yaw = (float) (Mth.atan2(-delta.x, delta.z) * (180.0D / Math.PI));
			mob.setYRot(yaw);
			mob.yRotO = yaw;
			mob.yBodyRot = yaw;
			mob.yBodyRotO = yaw;
			mob.setYHeadRot(yaw);
		}

		mob.setSpeed((float) mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
		return new Vec3(0.0D, 0.0D, 1.0D);
	}

	public static void afterChaseTravel(Mob mob) {
		Player player = findTarget(mob);
		if (player == null) {
			return;
		}

		if (mob.horizontalCollision || mob.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D) {
			breakSurroundingObstacles(mob);
		}

		if (mob.horizontalCollision) {
			Vec3 motion = mob.getDeltaMovement();
			mob.setDeltaMovement(motion.x, 0.42D, motion.z);
		}
	}

	public static void tryMelee(Mob mob) {
		Player player = findTarget(mob);
		if (player == null) {
			return;
		}
		double touchDistance = mob.getBbWidth() + player.getBbWidth() + 0.35D;
		if (mob.distanceTo(player) <= touchDistance) {
			mob.doHurtTarget(player);
		}
	}

	public static void breakSurroundingObstacles(Mob mob) {
		Level level = mob.level();
		if (level.isClientSide) {
			return;
		}

		AABB body = mob.getBoundingBox();
		int minBreakY = Mth.ceil(body.minY - 1.0E-4D);
		AABB scan = body.inflate(BREAK_RADIUS, 0.0D, BREAK_RADIUS);
		int minX = Mth.floor(scan.minX);
		int minY = Math.max(Mth.floor(scan.minY), minBreakY);
		int minZ = Mth.floor(scan.minZ);
		int maxX = Mth.ceil(scan.maxX);
		int maxY = Mth.ceil(scan.maxY);
		int maxZ = Mth.ceil(scan.maxZ);

		if (minY > maxY) {
			return;
		}

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);
					if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) {
						continue;
					}
					level.destroyBlock(pos, false);
				}
			}
		}
	}

	private static Player findTarget(Mob mob) {
		if (mob instanceof OverworldStalker stalker) {
			return stalker.isActivated() ? stalker.getLockedTarget() : null;
		}

		Level level = mob.level();
		double range = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
		if (range <= 0.0D) {
			range = 128.0D;
		}
		double rangeSq = range * range;

		if (level instanceof ServerLevel serverLevel) {
			Player nearest = null;
			double nearestSq = rangeSq;
			for (ServerPlayer player : serverLevel.players()) {
				if (!player.isAlive() || player.isSpectator()) {
					continue;
				}
				double distSq = mob.distanceToSqr(player);
				if (distSq <= nearestSq) {
					nearestSq = distSq;
					nearest = player;
				}
			}
			return nearest;
		}

		return level.getNearestPlayer(mob, range);
	}
}
