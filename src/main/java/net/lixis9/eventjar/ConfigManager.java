package net.lixis9.eventjar;

public class ConfigManager {
    private static boolean textDistortionEnabled = false;
    private static boolean itemRenamerEnabled = false;

    public static boolean isTextDistortionEnabled() {
        return textDistortionEnabled;
    }

    public static void toggleTextDistortion() {
        textDistortionEnabled = !textDistortionEnabled;
    }

    public static boolean isItemRenamerEnabled() {
        return itemRenamerEnabled;
    }

    public static void toggleItemRenamer() {
        itemRenamerEnabled = !itemRenamerEnabled;
    }
}
