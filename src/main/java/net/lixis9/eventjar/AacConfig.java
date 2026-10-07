package net.lixis9.eventjar;

import net.minecraftforge.common.ForgeConfigSpec;

public final class AacConfig {

	public static final ForgeConfigSpec SPEC;

	private static final ForgeConfigSpec.BooleanValue SAFE_MODE_VALUE;
	private static final ForgeConfigSpec.BooleanValue SAFE_MODE_CHOSEN_VALUE;
	private static final ForgeConfigSpec.BooleanValue TEXT_DISTORTION_VALUE;
	private static final ForgeConfigSpec.BooleanValue ITEM_RENAMER_VALUE;
	private static final ForgeConfigSpec.DoubleValue ENTITY_SPAWN_MULT_VALUE;
	private static final ForgeConfigSpec.DoubleValue EVENT_SPAWN_MULT_VALUE;
	private static final ForgeConfigSpec.DoubleValue STRUCTURE_SPAWN_MULT_VALUE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_SIGNS_VALUE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_STRUCTURES_VALUE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_BLOOD_RAIN_VALUE;

	public static volatile boolean SAFE_MODE = false;
	public static volatile boolean SAFE_MODE_CHOSEN = false;
	public static volatile boolean TEXT_DISTORTION = false;
	public static volatile boolean ITEM_RENAMER = false;
	public static volatile double ENTITY_SPAWN_MULTIPLIER = 1.0D;
	public static volatile double EVENT_SPAWN_MULTIPLIER = 1.0D;
	public static volatile double STRUCTURE_SPAWN_MULTIPLIER = 1.0D;
	public static volatile boolean ENABLE_SIGNS = true;
	public static volatile boolean ENABLE_STRUCTURES = true;
	public static volatile boolean ENABLE_BLOOD_RAIN = true;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.comment("Safe Mode disables OS/desktop side-effects outside Minecraft.").push("safety");
		SAFE_MODE_VALUE = builder.comment("When true, ClientOsEffectPacket desktop/OS actions are blocked.")
				.define("safeMode", false);
		SAFE_MODE_CHOSEN_VALUE = builder.comment("True after the player has used /aac_config safemode at least once.")
				.define("safeModeChosen", false);
		builder.pop();

		builder.comment("UI / item chaos (also exposed via ConfigManager).").push("chaos");
		TEXT_DISTORTION_VALUE = builder.comment("Jumble menu/widget text (excludes Test questionnaire screens).")
				.define("textDistortion", false);
		ITEM_RENAMER_VALUE = builder.comment("Random hover names on pickup (makes stacks unique / 'unstackable').")
				.define("itemRenamer", false);
		builder.pop();

		builder.comment("Spawn rate multipliers (0 = off, 1 = default, 2 = double).").push("spawn");
		ENTITY_SPAWN_MULT_VALUE = builder.defineInRange("entitySpawnMultiplier", 1.0D, 0.0D, 2.0D);
		EVENT_SPAWN_MULT_VALUE = builder.defineInRange("eventSpawnMultiplier", 1.0D, 0.0D, 2.0D);
		STRUCTURE_SPAWN_MULT_VALUE = builder.defineInRange("structureSpawnMultiplier", 1.0D, 0.0D, 2.0D);
		builder.pop();

		builder.comment("World placement toggles.").push("worldgen");
		ENABLE_SIGNS_VALUE = builder.comment("Allow sign-related horror placers / features.").define("enableSigns", true);
		ENABLE_STRUCTURES_VALUE = builder.comment("Allow structure placers (e.g. christ).").define("enableStructures", true);
		builder.pop();

		builder.comment("Ambient horror events.").push("events");
		ENABLE_BLOOD_RAIN_VALUE = builder.comment("Allow the blood rain ambient event.").define("enableBloodRain", true);
		builder.pop();

		SPEC = builder.build();
	}

	private AacConfig() {
	}

	public static void bake() {
		SAFE_MODE = SAFE_MODE_VALUE.get();
		SAFE_MODE_CHOSEN = SAFE_MODE_CHOSEN_VALUE.get();
		TEXT_DISTORTION = TEXT_DISTORTION_VALUE.get();
		ITEM_RENAMER = ITEM_RENAMER_VALUE.get();
		ENTITY_SPAWN_MULTIPLIER = ENTITY_SPAWN_MULT_VALUE.get();
		EVENT_SPAWN_MULTIPLIER = EVENT_SPAWN_MULT_VALUE.get();
		STRUCTURE_SPAWN_MULTIPLIER = STRUCTURE_SPAWN_MULT_VALUE.get();
		ENABLE_SIGNS = ENABLE_SIGNS_VALUE.get();
		ENABLE_STRUCTURES = ENABLE_STRUCTURES_VALUE.get();
		ENABLE_BLOOD_RAIN = ENABLE_BLOOD_RAIN_VALUE.get();
	}

	public static void setSafeMode(boolean enabled) {
		SAFE_MODE_VALUE.set(enabled);
		SAFE_MODE = enabled;
		SAFE_MODE_CHOSEN_VALUE.set(true);
		SAFE_MODE_CHOSEN = true;
		SPEC.save();
	}

	public static void markSafeModeChosen() {
		SAFE_MODE_CHOSEN_VALUE.set(true);
		SAFE_MODE_CHOSEN = true;
		SPEC.save();
	}

	public static void setTextDistortion(boolean enabled) {
		TEXT_DISTORTION_VALUE.set(enabled);
		TEXT_DISTORTION = enabled;
		SPEC.save();
	}

	public static void setItemRenamer(boolean enabled) {
		ITEM_RENAMER_VALUE.set(enabled);
		ITEM_RENAMER = enabled;
		SPEC.save();
	}

	public static void setEntitySpawnMultiplier(double value) {
		ENTITY_SPAWN_MULT_VALUE.set(clampMult(value));
		ENTITY_SPAWN_MULTIPLIER = ENTITY_SPAWN_MULT_VALUE.get();
		SPEC.save();
	}

	public static void setEventSpawnMultiplier(double value) {
		EVENT_SPAWN_MULT_VALUE.set(clampMult(value));
		EVENT_SPAWN_MULTIPLIER = EVENT_SPAWN_MULT_VALUE.get();
		SPEC.save();
	}

	public static void setStructureSpawnMultiplier(double value) {
		STRUCTURE_SPAWN_MULT_VALUE.set(clampMult(value));
		STRUCTURE_SPAWN_MULTIPLIER = STRUCTURE_SPAWN_MULT_VALUE.get();
		SPEC.save();
	}

	public static void setEnableSigns(boolean enabled) {
		ENABLE_SIGNS_VALUE.set(enabled);
		ENABLE_SIGNS = enabled;
		SPEC.save();
	}

	public static void setEnableStructures(boolean enabled) {
		ENABLE_STRUCTURES_VALUE.set(enabled);
		ENABLE_STRUCTURES = enabled;
		SPEC.save();
	}

	public static void setEnableBloodRain(boolean enabled) {
		ENABLE_BLOOD_RAIN_VALUE.set(enabled);
		ENABLE_BLOOD_RAIN = enabled;
		SPEC.save();
	}

	public static boolean allowEntitySpawnRoll() {
		double m = ENTITY_SPAWN_MULTIPLIER;
		if (m <= 0.0D) {
			return false;
		}
		if (m >= 1.0D) {
			return Math.random() < Math.min(1.0D, m);
		}
		return Math.random() < m;
	}

	public static float scaleEntityChance(float baseChance) {
		return (float) Math.min(1.0D, Math.max(0.0D, baseChance * ENTITY_SPAWN_MULTIPLIER));
	}

	public static long scaleEntityInterval(long baseTicks) {
		double m = ENTITY_SPAWN_MULTIPLIER;
		if (m <= 0.0D) {
			return Long.MAX_VALUE / 4L;
		}
		return Math.max(1L, Math.round(baseTicks / m));
	}

	public static boolean allowStructureRoll(double baseChance) {
		if (!ENABLE_STRUCTURES || STRUCTURE_SPAWN_MULTIPLIER <= 0.0D) {
			return false;
		}
		return Math.random() < Math.min(1.0D, baseChance * STRUCTURE_SPAWN_MULTIPLIER);
	}

	public static boolean signsEnabled() {
		return ENABLE_SIGNS;
	}

	public static boolean structuresEnabled() {
		return ENABLE_STRUCTURES;
	}

	private static double clampMult(double value) {
		return Math.max(0.0D, Math.min(2.0D, value));
	}
}
