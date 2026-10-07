package net.lixis.outofbound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public final class DarknessUtil {

	public static final int DARKNESS_LIGHT_THRESHOLD = 7;

	private DarknessUtil() {
	}

	public static boolean isDarkEnough(Level level, BlockPos pos) {
		int brightness = level.getMaxLocalRawBrightness(pos);
		if (brightness <= DARKNESS_LIGHT_THRESHOLD) {
			return true;
		}

		return brightness <= 8 && level.getBrightness(LightLayer.BLOCK, pos) <= 1;
	}
}
