package net.lixis.outofbound.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;

import java.util.concurrent.ThreadLocalRandom;

public final class OccludedSoundUtil {

	private OccludedSoundUtil() {
	}

	public static boolean playBehindOrThroughWall(ServerLevel level, ServerPlayer player, SoundEvent sound,
			SoundSource source, float volume, float pitch, int minDistance, int maxDistance,
			ThreadLocalRandom random) {
		if (sound == null) {
			return false;
		}

		Vec3 eye = player.getEyePosition();
		Vec3 best = null;
		boolean bestOccluded = false;

		for (int attempt = 0; attempt < 16; attempt++) {
			Vec3 candidate = pickCandidate(player, minDistance, maxDistance, random);
			boolean occluded = isOccluded(level, eye, candidate);
			if (best == null || (occluded && !bestOccluded)) {
				best = candidate;
				bestOccluded = occluded;
				if (occluded) {
					break;
				}
			}
		}

		if (best == null) {
			return false;
		}

		Vec3 origin = bestOccluded ? nudgeIntoWall(level, best, player, random) : best;
		level.playSound(null, origin.x, origin.y, origin.z, sound, source, volume, pitch);
		return true;
	}

	private static Vec3 pickCandidate(ServerPlayer player, int minDistance, int maxDistance,
			ThreadLocalRandom random) {
		double yaw = Math.toRadians(player.getYRot() + 180.0D + random.nextDouble(-70.0D, 70.0D));
		double distance = random.nextDouble(minDistance, maxDistance + 1);
		double x = player.getX() - Math.sin(yaw) * distance;
		double z = player.getZ() + Math.cos(yaw) * distance;
		double y = player.getY() + random.nextInt(-2, 3);
		return new Vec3(x, y, z);
	}

	private static boolean isOccluded(ServerLevel level, Vec3 from, Vec3 to) {
		BlockHitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
		if (hit.getType() == HitResult.Type.MISS) {
			return false;
		}
		double hitDistance = hit.getLocation().distanceTo(from);
		double totalDistance = to.distanceTo(from);
		return hitDistance < totalDistance - 0.35D;
	}

	private static Vec3 nudgeIntoWall(ServerLevel level, Vec3 pos, ServerPlayer player, ThreadLocalRandom random) {
		BlockPos center = BlockPos.containing(pos);
		Direction behind = player.getDirection().getOpposite();
		BlockPos wallPos = center;
		for (int step = 0; step < 4; step++) {
			BlockPos probe = center.relative(behind, step);
			BlockState state = level.getBlockState(probe);
			if (state.canOcclude()) {
				wallPos = probe;
				break;
			}
		}

		Vec3 wallCenter = Vec3.atCenterOf(wallPos);
		if (wallCenter.distanceToSqr(pos) <= 9.0D) {
			return wallCenter;
		}
		return pos;
	}
}
