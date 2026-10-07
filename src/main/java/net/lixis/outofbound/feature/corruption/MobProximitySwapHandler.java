package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.corruptors.MobSwapCorruptor;
import net.lixis.outofbound.feature.corruption.util.CorruptionScaling;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.ThreadLocalRandom;

public final class MobProximitySwapHandler {

	private static final float MIN_LEVEL = 3.0F;
	private static final double PROXIMITY_RADIUS = 12.0D;

	private MobProximitySwapHandler() {
	}

	public static void tick(ServerPlayer player, float level, long daysSince) {
		if (level < MIN_LEVEL || daysSince < CorruptionDayStages.SIMPLE_START_DAY) {
			return;
		}

		int interval = Math.max(6, 18 - Math.round(level * 0.1F));
		if ((player.getServer().getTickCount() + player.getId()) % interval != 0) {
			return;
		}

		ServerLevel serverLevel = player.serverLevel();
		LivingEntity closest = MobSwapCorruptor.findClosestSwapCandidate(serverLevel, player, PROXIMITY_RADIUS);
		if (closest == null) {
			return;
		}

		float strength = CorruptionScaling.tierStrength(level, MIN_LEVEL);
		float chance = 0.08F + strength * 0.28F;
		if (ThreadLocalRandom.current().nextFloat() >= chance) {
			return;
		}

		int ttl = OutofboundMod.CORRUPTION.effectTtl(level);
		MobSwapCorruptor.swapEntity(serverLevel, closest, ttl, daysSince);
	}
}
