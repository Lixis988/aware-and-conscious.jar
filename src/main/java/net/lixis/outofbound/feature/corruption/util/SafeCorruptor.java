package net.lixis.outofbound.feature.corruption.util;

import net.minecraft.util.Mth;

public final class SafeCorruptor {

	private SafeCorruptor() {
	}

	public static float safeFloat(float value) {
		if (Float.isNaN(value) || Float.isInfinite(value)) {
			return 0.0F;
		}
		return Mth.clamp(value, -10_000.0F, 10_000.0F);
	}

	public static int safeInt(int value) {
		return Mth.clamp(value, -64, 9999);
	}

	public static double safeDouble(double value) {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			return 0.0D;
		}
		return Mth.clamp(value, -10_000.0D, 10_000.0D);
	}
}
