package net.lixis.outofbound.client;

public final class EyeAnimFrames {

	public static final int COUNT = 30;
	public static final int TOTAL_MS = 3000;

	private static final int[] DELAY_MS = {
			100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
			100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
			100, 100, 100, 100, 100, 100, 100, 100, 100, 100
	};

	private EyeAnimFrames() {
	}

	public static int frameForTime(long timeMs) {
		if (COUNT <= 1) {
			return 0;
		}
		int elapsed = (int) (Math.floorMod(timeMs, TOTAL_MS));
		int accumulated = 0;
		for (int i = 0; i < COUNT; i++) {
			accumulated += DELAY_MS[i];
			if (elapsed < accumulated) {
				return i;
			}
		}
		return COUNT - 1;
	}
}
