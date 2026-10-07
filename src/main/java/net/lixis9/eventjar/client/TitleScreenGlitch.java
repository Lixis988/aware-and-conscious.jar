package net.lixis9.eventjar.client;

import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public final class TitleScreenGlitch {

	private static long nextBackgroundSwitchMs;
	private static boolean backgroundBlack;

	private static long nextTitleSwitchMs;
	private static boolean customTitle;

	private TitleScreenGlitch() {
	}

	public static void update() {
		long now = Util.getMillis();
		ThreadLocalRandom rng = ThreadLocalRandom.current();

		if (now >= nextBackgroundSwitchMs) {
			if (backgroundBlack) {
				backgroundBlack = false;

				nextBackgroundSwitchMs = now + rng.nextLong(90L, 520L);
			} else {

				if (rng.nextFloat() < 0.22F) {
					backgroundBlack = true;
					nextBackgroundSwitchMs = now + rng.nextLong(30L, 110L);
				} else {
					nextBackgroundSwitchMs = now + rng.nextLong(50L, 280L);
				}
			}
		}

		if (now >= nextTitleSwitchMs) {
			customTitle = !customTitle;
			if (customTitle) {

				nextTitleSwitchMs = now + (rng.nextFloat() < 0.35F
						? rng.nextLong(40L, 120L)
						: rng.nextLong(180L, 700L));
			} else {

				nextTitleSwitchMs = now + (rng.nextFloat() < 0.55F
						? rng.nextLong(35L, 100L)
						: rng.nextLong(120L, 450L));
			}
		}
	}

	public static boolean isBackgroundBlack() {
		return backgroundBlack;
	}

	public static boolean useCustomTitle() {
		return customTitle;
	}
}
