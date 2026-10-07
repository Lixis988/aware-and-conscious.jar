package net.lixis.outofbound.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class UndefiendGlitchScheduler {

	private static final int MIN_PHASE = 2;
	private static final int MAX_PHASE = 6;

	private static final int[] GLITCH_TICKS = {2, 4, 3, 2, 5, 2};

	private static final int[] NORMAL_TICKS = {3, 2, 4, 3, 2, 4};

	private static final int CYCLE_LENGTH;

	static {
		int length = 0;
		for (int i = 0; i < GLITCH_TICKS.length; i++) {
			length += GLITCH_TICKS[i] + NORMAL_TICKS[i];
		}
		CYCLE_LENGTH = length;
	}

	private UndefiendGlitchScheduler() {
	}

	public static boolean isGlitchActive(int entityId, float ageInTicks) {
		int tick = (int) ageInTicks + entityId * 13;
		int position = Math.floorMod(tick, CYCLE_LENGTH);
		int cursor = 0;

		for (int i = 0; i < GLITCH_TICKS.length; i++) {
			cursor += clampPhase(GLITCH_TICKS[i]);
			if (position < cursor) {
				return true;
			}
			cursor += clampPhase(NORMAL_TICKS[i]);
			if (position < cursor) {
				return false;
			}
		}

		return false;
	}

	private static int clampPhase(int ticks) {
		return Math.max(MIN_PHASE, Math.min(MAX_PHASE, ticks));
	}
}
