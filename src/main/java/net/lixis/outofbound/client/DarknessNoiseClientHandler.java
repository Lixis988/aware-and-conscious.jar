package net.lixis.outofbound.client;

import net.lixis9.eventjar.DarknessConfig;
import net.lixis.outofbound.util.DarknessUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public final class DarknessNoiseClientHandler {

	private static final int DARK_THRESHOLD_TICKS = 40 * 20;
	private static final float RAMP_UP_PER_TICK = 0.002F;
	private static final float RAMP_DOWN_PER_TICK = 0.004F;

	private static int darkTicks;
	private static float rampStrength;

	private DarknessNoiseClientHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		if (!DarknessConfig.NOISE_ENABLED) {
			darkTicks = 0;
			rampStrength = 0.0F;
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.level == null) {
			darkTicks = 0;
			rampStrength = 0.0F;
			return;
		}

		BlockPos eyePos = BlockPos.containing(minecraft.player.getEyePosition());
		if (DarknessUtil.isDarkEnough(minecraft.level, eyePos)) {
			darkTicks++;
			if (darkTicks >= DARK_THRESHOLD_TICKS) {
				rampStrength = Math.min(1.0F, rampStrength + RAMP_UP_PER_TICK);
			}
		} else {
			darkTicks = 0;
			rampStrength = Math.max(0.0F, rampStrength - RAMP_DOWN_PER_TICK);
		}
	}

	public static float getRampStrength() {
		return DarknessConfig.NOISE_ENABLED ? rampStrength : 0.0F;
	}
}
