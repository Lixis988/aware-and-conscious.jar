package net.lixis.outofbound.client.glitch;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public final class GlobalWorldGlitchScheduler {

	private static final int INTERVAL_TICKS = 6000;
	private static final int INTERVAL_JITTER = 800;
	private static final int GLITCH_DURATION_TICKS = 5;

	private static int cooldownTicks = randomCooldown();
	private static int glitchTicksRemaining;
	private static int glitchSeed;
	private static boolean sinkingActive;
	private static float sinkProgress;

	private GlobalWorldGlitchScheduler() {
	}

	public static void onEncounterEnabled() {
		cooldownTicks = randomCooldown();
		glitchTicksRemaining = 0;
	}

	public static void onSinkingEnabled() {
		sinkingActive = true;
		cooldownTicks = 80;
		glitchTicksRemaining = 0;
	}

	public static void reset() {
		cooldownTicks = randomCooldown();
		glitchTicksRemaining = 0;
		glitchSeed = 0;
		sinkingActive = false;
		sinkProgress = 0.0F;
	}

	public static void tick(boolean enabled, boolean sinking, float progress) {
		sinkingActive = sinking;
		sinkProgress = progress;

		if (!enabled && !sinking) {
			glitchTicksRemaining = 0;
			return;
		}

		if (glitchTicksRemaining > 0) {
			glitchTicksRemaining--;
			return;
		}

		cooldownTicks--;
		if (cooldownTicks <= 0) {
			glitchTicksRemaining = sinking ? glitchDurationForSink(progress) : GLITCH_DURATION_TICKS;
			glitchSeed = ThreadLocalRandom.current().nextInt();
			cooldownTicks = sinking ? cooldownForSink(progress) : randomCooldown();
		}
	}

	public static boolean isGlitching() {
		if (glitchTicksRemaining > 0) {
			return true;
		}
		return sinkingActive && sinkProgress >= 0.98F;
	}

	public static float getGlitchIntensity() {
		if (sinkingActive) {
			return 0.25F + sinkProgress * 0.75F;
		}
		return 1.0F;
	}

	public static int getGlitchSeed() {
		return glitchSeed;
	}

	private static int cooldownForSink(float progress) {
		int base = 400 - (int) (progress * 360.0F);
		return Math.max(20, base + ThreadLocalRandom.current().nextInt(-20, 21));
	}

	private static int glitchDurationForSink(float progress) {
		return 3 + (int) (progress * 12.0F);
	}

	private static int randomCooldown() {
		return INTERVAL_TICKS + ThreadLocalRandom.current().nextInt(-INTERVAL_JITTER, INTERVAL_JITTER + 1);
	}
}
