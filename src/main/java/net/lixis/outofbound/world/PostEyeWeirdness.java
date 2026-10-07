package net.lixis.outofbound.world;

import net.minecraft.server.MinecraftServer;

public final class PostEyeWeirdness {

	public static final double CHANCE_MULTIPLIER = 1.5D;
	public static final double INTERVAL_DIVISOR = 1.35D;

	private PostEyeWeirdness() {
	}

	public static boolean isActive(MinecraftServer server) {
		if (server == null) {
			return false;
		}
		FinaleState finale = WorldInternalConfig.getFinaleState(server);
		if (finale == FinaleState.ACTIVE || finale == FinaleState.DONE) {
			return true;
		}
		return WorldInternalConfig.isPostEyeWeirdness(server);
	}

	public static double scaledChance(MinecraftServer server, double baseChance) {
		if (!isActive(server)) {
			return baseChance;
		}
		return Math.min(1.0D, baseChance * CHANCE_MULTIPLIER);
	}

	public static int scaledInterval(MinecraftServer server, int baseInterval) {
		if (!isActive(server)) {
			return baseInterval;
		}
		return Math.max(1, (int) Math.round(baseInterval / INTERVAL_DIVISOR));
	}

	public static long scaledCooldown(MinecraftServer server, long baseCooldown) {
		if (!isActive(server)) {
			return baseCooldown;
		}
		return Math.max(20L, Math.round(baseCooldown / INTERVAL_DIVISOR));
	}
}
