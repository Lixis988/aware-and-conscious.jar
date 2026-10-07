package net.lixis.outofbound.entity;

import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

public final class TheSunSpawnManager {

	private static final double OFFSET_ABOVE_HEAD = 4.5D;
	private static final double EXISTING_CHECK_RADIUS = 12.0D;

	private TheSunSpawnManager() {
	}

	public static void trySpawnForPlayer(ServerLevel overworld, ServerPlayer player) {
		if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
			return;
		}
		if (hasSunOverPlayer(overworld, player)) {
			return;
		}

		TheSunEntity sun = OutofboundExtraEntities.THE_SUN.get().create(overworld);
		if (sun == null) {
			return;
		}

		double headY = player.getEyePosition().y + OFFSET_ABOVE_HEAD;
		sun.moveTo(player.getX(), headY, player.getZ(), 0.0F, 0.0F);
		sun.setTarget(player.getUUID());
		overworld.addFreshEntity(sun);
	}

	private static boolean hasSunOverPlayer(ServerLevel level, ServerPlayer player) {
		AABB box = player.getBoundingBox().inflate(EXISTING_CHECK_RADIUS, EXISTING_CHECK_RADIUS + OFFSET_ABOVE_HEAD, EXISTING_CHECK_RADIUS);
		return !level.getEntitiesOfClass(TheSunEntity.class, box, sun -> !sun.isRemoved()).isEmpty();
	}
}
