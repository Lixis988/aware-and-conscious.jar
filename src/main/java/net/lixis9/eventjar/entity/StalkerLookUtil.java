package net.lixis9.eventjar.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

final class StalkerLookUtil {

	private static final double LOOK_DOT = 0.985D;

	private StalkerLookUtil() {
	}

	static boolean isPlayerLookingAt(Player player, Entity entity) {
		Vec3 toEntity = entity.getEyePosition().subtract(player.getEyePosition(1.0F));
		if (toEntity.lengthSqr() < 1.0E-6) {
			return true;
		}
		return player.getLookAngle().dot(toEntity.normalize()) > LOOK_DOT
				&& player.hasLineOfSight(entity);
	}
}
