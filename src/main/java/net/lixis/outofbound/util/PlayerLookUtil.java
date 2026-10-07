package net.lixis.outofbound.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class PlayerLookUtil {

	private PlayerLookUtil() {
	}

	public static boolean isLookingAt(Player player, Entity entity, double range, double dotThreshold) {
		if (player.distanceTo(entity) > range) {
			return false;
		}

		Vec3 eye = player.getEyePosition(1.0F);
		Vec3 look = player.getViewVector(1.0F);
		Vec3 reach = eye.add(look.scale(range));

		AABB target = entity.getBoundingBox().inflate(0.35D);
		Optional<Vec3> hit = target.clip(eye, reach);
		if (hit.isPresent()) {
			return true;
		}

		Vec3 toEntity = entity.getEyePosition().subtract(eye);
		double distance = toEntity.length();
		if (distance < 0.001D) {
			return false;
		}

		return look.dot(toEntity.normalize()) >= dotThreshold;
	}

	public static boolean isPositionInFov(Player player, Vec3 target, double minDot) {
		Vec3 eye = player.getEyePosition(1.0F);
		Vec3 look = player.getViewVector(1.0F);
		Vec3 toTarget = target.subtract(eye);
		double distance = toTarget.length();
		if (distance < 0.001D) {
			return false;
		}
		return look.dot(toTarget.normalize()) >= minDot;
	}

	public static boolean isLookingExactlyAt(Player player, Entity entity, double range) {
		if (player.distanceTo(entity) > range) {
			return false;
		}
		if (!player.hasLineOfSight(entity)) {
			return false;
		}

		Vec3 eye = player.getEyePosition(1.0F);
		Vec3 look = player.getViewVector(1.0F);
		Vec3 reach = eye.add(look.scale(range));
		AABB target = entity.getBoundingBox().inflate(0.05D);
		return target.clip(eye, reach).isPresent();
	}

	public static double viewAngleDegrees(Player player, Entity entity) {
		Vec3 view = player.getViewVector(1.0F);
		Vec3 toEntity = entity.getEyePosition(1.0F).subtract(player.getEyePosition(1.0F));
		double length = toEntity.length();
		if (length < 1.0E-4D) {
			return 0.0D;
		}
		toEntity = toEntity.scale(1.0D / length);
		double dot = view.dot(toEntity);
		dot = Math.max(-1.0D, Math.min(1.0D, dot));
		return Math.toDegrees(Math.acos(dot));
	}
}
