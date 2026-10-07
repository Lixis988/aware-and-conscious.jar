package net.lixis9.eventjar;

public class ConfigManager {

	public static boolean isTextDistortionEnabled() {
		return AacConfig.TEXT_DISTORTION;
	}

	public static void toggleTextDistortion() {
		AacConfig.setTextDistortion(!AacConfig.TEXT_DISTORTION);
	}

	public static void setTextDistortion(boolean enabled) {
		AacConfig.setTextDistortion(enabled);
	}

	public static boolean isItemRenamerEnabled() {
		return AacConfig.ITEM_RENAMER;
	}

	public static void toggleItemRenamer() {
		AacConfig.setItemRenamer(!AacConfig.ITEM_RENAMER);
	}

	public static void setItemRenamer(boolean enabled) {
		AacConfig.setItemRenamer(enabled);
	}
}
