package net.lixis.outofbound.feature.corruption;

import java.util.concurrent.ThreadLocalRandom;

public final class CorruptionScheduler {

	private static int burstRemaining;

	private CorruptionScheduler() {
	}

	public static void tick(float level) {
		if (burstRemaining > 0) {
			burstRemaining--;
			return;
		}
		if (level <= 0.0F) {
			return;
		}
		float chance = 0.006F + level * 0.00011F;
		if (ThreadLocalRandom.current().nextFloat() < chance) {
			burstRemaining = burstDuration(level);
		}
	}

	public static int burstDuration(float level) {
		return 6 + (int) (level * 0.18F);
	}

	public static boolean isBurstActive() {
		return burstRemaining > 0;
	}

	public static int getBurstRemaining() {
		return burstRemaining;
	}

	public static void forceBurst(int ticks) {
		burstRemaining = Math.max(burstRemaining, ticks);
	}

	public static void reset() {
		burstRemaining = 0;
	}
}
