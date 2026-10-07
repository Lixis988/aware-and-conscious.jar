package net.lixis.outofbound.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public final class BoundedOneAtmosphereClientState {

	private static int glitchTicksRemaining;
	private static int glitchSeed;
	private static float glitchIntensity = 0.65F;

	private static int titleFlickerTicksRemaining;
	private static String savedTitle;
	private static boolean titleOverridden;

	private BoundedOneAtmosphereClientState() {
	}

	public static void triggerGlitch(int durationTicks) {
		glitchTicksRemaining = Math.max(glitchTicksRemaining, durationTicks);
		glitchSeed = ThreadLocalRandom.current().nextInt();
	}

	public static void triggerTitleFlicker(int durationTicks) {
		titleFlickerTicksRemaining = Math.max(titleFlickerTicksRemaining, durationTicks);
	}

	public static void clientTick() {
		if (glitchTicksRemaining > 0) {
			glitchTicksRemaining--;
		}
		if (titleFlickerTicksRemaining > 0) {
			titleFlickerTicksRemaining--;
		}
	}

	public static boolean isGlitching() {
		return glitchTicksRemaining > 0;
	}

	public static int getGlitchSeed() {
		return glitchSeed;
	}

	public static float getGlitchIntensity() {
		return glitchIntensity;
	}

	public static boolean isTitleFlickerActive() {
		return titleFlickerTicksRemaining > 0;
	}

	public static String getSavedTitle() {
		return savedTitle;
	}

	public static void setSavedTitle(String title) {
		savedTitle = title;
	}

	public static boolean isTitleOverridden() {
		return titleOverridden;
	}

	public static void setTitleOverridden(boolean overridden) {
		titleOverridden = overridden;
	}
}
