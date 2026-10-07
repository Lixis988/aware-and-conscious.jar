package net.lixis.outofbound.feature.corruption.util;

import java.util.concurrent.ThreadLocalRandom;

public final class CorruptionScaling {

	private static final float SOFT_BAND = 9.0F;

	private CorruptionScaling() {
	}

	public static float intensity(float level) {
		return Math.max(0.0F, Math.min(1.0F, level / 100.0F));
	}

	public static float tierStrength(float level, float minLevel) {
		if (level <= 0.0F) {
			return 0.0F;
		}
		float softMin = Math.max(1.0F, minLevel - SOFT_BAND);
		if (level < softMin) {
			return level / softMin * 0.08F;
		}
		return Math.min(1.0F, 0.08F + (level - softMin) / (100.0F - softMin) * 0.92F);
	}

	public static int effectiveWeight(float level, float minLevel, int baseWeight) {
		float strength = tierStrength(level, minLevel);
		if (strength <= 0.0F) {
			return 0;
		}
		return Math.max(1, Math.round(baseWeight * (0.15F + 0.85F * strength)));
	}

	public static boolean rollAction(float level, float minLevel, ThreadLocalRandom random) {
		float strength = tierStrength(level, minLevel);
		if (strength <= 0.0F) {
			return false;
		}
		float chance = 0.05F + strength * 0.55F;
		return random.nextFloat() < chance;
	}

	public static int scaledBlockCount(float level, float minLevel, int minBlocks, int maxBlocks, ThreadLocalRandom random) {
		float strength = tierStrength(level, minLevel);
		int cap = Math.max(1, Math.round(minBlocks + (maxBlocks - minBlocks) * strength));
		return 1 + random.nextInt(cap);
	}

	public static int ambientIntervalTicks(float level) {
		return Math.max(50, 220 - Math.round(level * 1.7F));
	}
}
