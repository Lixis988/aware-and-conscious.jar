package net.lixis9.eventjar.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class StalkerLookUtil {

	private static final double LOOK_DOT = 0.985D;

	private StalkerLookUtil() {
	}

	public static boolean isPlayerLookingAt(Player player, Entity entity) {
		return isPlayerLookingAt(player, entity, LOOK_DOT);
	}

	public static boolean isPlayerLookingAt(Player player, Entity entity, double minDot) {
		Vec3 toEntity = entity.getEyePosition().subtract(player.getEyePosition(1.0F));
		if (toEntity.lengthSqr() < 1.0E-6) {
			return true;
		}
		return player.getLookAngle().dot(toEntity.normalize()) > minDot
				&& player.hasLineOfSight(entity);
	}
}
