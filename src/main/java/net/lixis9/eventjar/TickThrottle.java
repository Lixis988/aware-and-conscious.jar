package net.lixis9.eventjar;

import net.minecraft.world.entity.Entity;

public final class TickThrottle {

	public static final int INTERVAL = 20;

	private TickThrottle() {
	}

	public static boolean due(Entity entity) {
		return entity != null && entity.tickCount % INTERVAL == 0;
	}

	public static boolean due(Entity entity, int interval) {
		return entity != null && interval > 0 && entity.tickCount % interval == 0;
	}

	public static boolean rollDenom(int denom) {
		if (denom <= 0) {
			return false;
		}
		return Math.random() < (INTERVAL / (double) denom);
	}

	public static boolean rollDenomEvent(int denom) {
		return rollDenomScaled(denom, AacConfig.EVENT_SPAWN_MULTIPLIER);
	}

	public static boolean rollDenomEntity(int denom) {
		return rollDenomScaled(denom, AacConfig.ENTITY_SPAWN_MULTIPLIER);
	}

	private static boolean rollDenomScaled(int denom, double mult) {
		if (mult <= 0.0D || denom <= 0) {
			return false;
		}
		double scaled = denom / mult;
		if (scaled < 1.0D) {
			scaled = 1.0D;
		}
		return Math.random() < (INTERVAL / scaled);
	}

	public static boolean rollFraction(double numerator, double denom) {
		if (denom <= 0.0) {
			return false;
		}
		return Math.random() < Math.min(1.0, (numerator * INTERVAL) / denom);
	}
}
