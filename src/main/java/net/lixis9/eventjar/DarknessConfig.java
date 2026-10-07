package net.lixis9.eventjar;

import net.minecraftforge.common.ForgeConfigSpec;

public final class DarknessConfig {

	public static final ForgeConfigSpec SPEC;

	private static final ForgeConfigSpec.BooleanValue ENABLED_VALUE;
	private static final ForgeConfigSpec.DoubleValue STRENGTH_VALUE;
	private static final ForgeConfigSpec.BooleanValue NOISE_ENABLED_VALUE;
	private static final ForgeConfigSpec.DoubleValue NOISE_INTENSITY_VALUE;
	private static final ForgeConfigSpec.BooleanValue RANDOM_LABELS_VALUE;
	private static final ForgeConfigSpec.ConfigValue<String> LIMINAL_EXE_PATH_VALUE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_DAMAGE_CORRUPTION_VALUE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_MEMORY_CORRUPTION_VALUE;

	public static volatile boolean ENABLED = true;
	public static volatile float STRENGTH = 1.0F;
	public static volatile boolean NOISE_ENABLED = true;
	public static volatile float NOISE_INTENSITY = 0.10F;
	public static volatile boolean RANDOM_LABELS = true;
	public static volatile String LIMINAL_EXE_PATH = "E:\\outofbound\\game\\cpp_liminal\\build\\bin\\Release\\liminal.exe";
	public static volatile boolean ENABLE_DAMAGE_CORRUPTION = true;
	public static volatile boolean ENABLE_MEMORY_CORRUPTION = true;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.comment("True-darkness lighting. Unlit areas become near-black.").push("darkness");
		ENABLED_VALUE = builder
				.comment("Enable total darkness.")
				.define("enabled", true);
		STRENGTH_VALUE = builder
				.comment("Darkness strength. 0.0 = vanilla lighting, 1.0 = maximum darkness.")
				.defineInRange("strength", 1.0D, 0.0D, 1.0D);
		builder.pop();

		builder.comment("Screen noise (film-grain) settings").push("noise");
		NOISE_ENABLED_VALUE = builder
				.comment("Enable the full-screen noise/grain post effect.")
				.define("enabled", true);
		NOISE_INTENSITY_VALUE = builder
				.comment("Noise intensity. 0.0 = no grain, 1.0 = maximum grain.")
				.defineInRange("intensity", 0.10D, 0.0D, 1.0D);
		builder.pop();

		builder.comment("GUI / menu settings").push("menu");
		RANDOM_LABELS_VALUE = builder
				.comment("Replace button and widget labels with random symbols (synced with /aac_config jumbled).")
				.define("randomLabels", false);
		builder.pop();

		builder.comment("Finale / liminal game handoff").push("finale");
		LIMINAL_EXE_PATH_VALUE = builder
				.comment("Absolute path to liminal.exe launched at the Seraph Eye finale.")
				.define("liminalExePath", LIMINAL_EXE_PATH);
		builder.pop();

		builder.comment("Corruption effect toggles").push("corruption");
		ENABLE_DAMAGE_CORRUPTION_VALUE = builder
				.comment("Enable damage corruption (blocks, mobs, chat, world progression).")
				.define("enableDamageCorruption", true);
		ENABLE_MEMORY_CORRUPTION_VALUE = builder
				.comment("Enable memory corruption (shader6 / UV-matrix-atlas glitches).")
				.define("enableMemoryCorruption", true);
		builder.pop();

		SPEC = builder.build();
	}

	private DarknessConfig() {
	}

	public static void bake() {
		ENABLED = ENABLED_VALUE.get();
		STRENGTH = STRENGTH_VALUE.get().floatValue();
		NOISE_ENABLED = NOISE_ENABLED_VALUE.get();
		NOISE_INTENSITY = NOISE_INTENSITY_VALUE.get().floatValue();
		RANDOM_LABELS = RANDOM_LABELS_VALUE.get();
		LIMINAL_EXE_PATH = LIMINAL_EXE_PATH_VALUE.get();
		ENABLE_DAMAGE_CORRUPTION = ENABLE_DAMAGE_CORRUPTION_VALUE.get();
		ENABLE_MEMORY_CORRUPTION = ENABLE_MEMORY_CORRUPTION_VALUE.get();
	}

	public static void setRandomLabels(boolean enabled) {
		RANDOM_LABELS_VALUE.set(enabled);
		RANDOM_LABELS = enabled;
		SPEC.save();
	}

	public static void setDarknessEnabled(boolean enabled) {
		ENABLED_VALUE.set(enabled);
		ENABLED = enabled;
		SPEC.save();
	}

	public static void setNoiseEnabled(boolean enabled) {
		NOISE_ENABLED_VALUE.set(enabled);
		NOISE_ENABLED = enabled;
		SPEC.save();
	}

	public static void setDamageCorruptionEnabled(boolean enabled) {
		ENABLE_DAMAGE_CORRUPTION_VALUE.set(enabled);
		ENABLE_DAMAGE_CORRUPTION = enabled;
		SPEC.save();
	}

	public static void setMemoryCorruptionEnabled(boolean enabled) {
		ENABLE_MEMORY_CORRUPTION_VALUE.set(enabled);
		ENABLE_MEMORY_CORRUPTION = enabled;
		SPEC.save();
	}
}
