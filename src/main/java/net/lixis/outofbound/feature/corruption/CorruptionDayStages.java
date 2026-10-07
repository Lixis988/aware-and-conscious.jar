package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.feature.corruption.corruptors.BossBarCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ChatCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ChunkCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.DataCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.EntityCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ICorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.InventoryCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ItemReplaceCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.MobFlashCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.MobSwapCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.PhysicsCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.RenderCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.SoundCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.StackCountCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.WorldCorruptor;

public final class CorruptionDayStages {

	public static final long SIMPLE_START_DAY = 1L;
	public static final long SIMPLE_INTERVAL_TICKS = 2000L;
	public static final long PREVIEW_START_DAY = 2L;
	public static final long HEAVY_DAY = 5L;
	public static final long MOD_MOB_POOL_DAY = 20L;
	public static final int PREVIEW_INTERVAL_TICKS = 7000;
	public static final float PREVIEW_ROLL_CHANCE = 0.08F;

	private CorruptionDayStages() {
	}

	public static boolean isSimpleActive(long daysSince) {
		return daysSince >= SIMPLE_START_DAY;
	}

	public static boolean isPreviewActive(long daysSince) {
		return daysSince >= PREVIEW_START_DAY && daysSince < HEAVY_DAY;
	}

	public static boolean isHeavyActive(long daysSince) {
		return daysSince >= HEAVY_DAY;
	}

	public static boolean allowsAmbient(ICorruptor corruptor, long daysSince) {
		if (daysSince < SIMPLE_START_DAY) {
			return false;
		}
		if (daysSince >= HEAVY_DAY) {
			return true;
		}
		return isEarlyAmbient(corruptor);
	}

	public static boolean allowsPreview(ICorruptor corruptor, long daysSince) {
		if (!isPreviewActive(daysSince)) {
			return false;
		}
		return corruptor instanceof ChunkCorruptor
				|| corruptor instanceof PhysicsCorruptor
				|| corruptor instanceof WorldCorruptor
				|| corruptor instanceof MobFlashCorruptor
				|| corruptor instanceof BossBarCorruptor
				|| corruptor instanceof InventoryCorruptor
				|| corruptor instanceof ItemReplaceCorruptor
				|| corruptor instanceof StackCountCorruptor
				|| corruptor instanceof RenderCorruptor
				|| corruptor instanceof DataCorruptor;
	}

	private static boolean isEarlyAmbient(ICorruptor corruptor) {
		return corruptor instanceof MobSwapCorruptor
				|| corruptor instanceof ChatCorruptor
				|| corruptor instanceof EntityCorruptor
				|| corruptor instanceof SoundCorruptor;
	}
}
