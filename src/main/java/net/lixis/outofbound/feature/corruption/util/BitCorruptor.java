package net.lixis.outofbound.feature.corruption.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class BitCorruptor {

	private BitCorruptor() {
	}

	public static Random random() {
		return ThreadLocalRandom.current();
	}

	public static float corruptFloat(float value) {
		int bits = Float.floatToIntBits(value);
		bits ^= 1 << random().nextInt(32);
		return SafeCorruptor.safeFloat(Float.intBitsToFloat(bits));
	}

	public static int corruptInt(int value) {
		return SafeCorruptor.safeInt(value ^ (1 << random().nextInt(32)));
	}

	public static String corruptText(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		char[] chars = text.toCharArray();
		int count = random().nextInt(3) + 1;
		for (int i = 0; i < count; i++) {
			int index = random().nextInt(chars.length);
			chars[index] = (char) random().nextInt(65535);
		}
		return new String(chars);
	}
}
