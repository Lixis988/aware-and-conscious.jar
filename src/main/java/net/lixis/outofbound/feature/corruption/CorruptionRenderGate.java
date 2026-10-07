package net.lixis.outofbound.feature.corruption;

import java.util.concurrent.ThreadLocalRandom;

public final class CorruptionRenderGate {

	private static volatile boolean active;
	private static volatile float level;

	private CorruptionRenderGate() {
	}

	public static void update(float corruptionLevel) {
		level = Math.max(0.0F, Math.min(100.0F, corruptionLevel));
		active = level >= 15.0F;
	}

	public static void clear() {
		level = 0.0F;
		active = false;
	}

	public static boolean shouldRenderCorrupt() {
		return active;
	}

	public static float getLevel() {
		return level;
	}

	public static boolean shouldCorruptUv() {
		if (!active) {
			return false;
		}
		return ThreadLocalRandom.current().nextFloat() < level * 0.008F;
	}

	public static float corruptFloat(float value) {
		int bits = Float.floatToIntBits(value);
		bits ^= 1 << ThreadLocalRandom.current().nextInt(32);
		float corrupted = Float.intBitsToFloat(bits);
		if (Float.isNaN(corrupted) || Float.isInfinite(corrupted)) {
			return value;
		}
		return corrupted;
	}
}
