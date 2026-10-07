package net.lixis.outofbound;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public class DailyShaderEventHandler {

	private static final double DAILY_CHANCE = 0.30D;
	private static final int DURATION_TICKS = 10_000;
	private static final long TICKS_PER_DAY = 24_000L;

	private static final ResourceLocation[] POOL = {
			TestShaderHandler.INVERSION,
			TestShaderHandler.POSTERIZE,
			TestShaderHandler.RANDOM,
	};

	private static long lastDay = Long.MIN_VALUE;
	private static ResourceLocation ownedShader = null;
	private static int remainingTicks = 0;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc == null || mc.level == null) {
			lastDay = Long.MIN_VALUE;
			if (ownedShader != null) {
				TestShaderHandler.setDaily(null);
				ownedShader = null;
			}
			remainingTicks = 0;
			return;
		}

		if (mc.isPaused()) {
			return;
		}

		if (ownedShader != null && --remainingTicks <= 0) {
			TestShaderHandler.setDaily(null);
			ownedShader = null;
			remainingTicks = 0;
		}

		long day = Math.floorDiv(mc.level.getDayTime(), TICKS_PER_DAY);
		if (lastDay == Long.MIN_VALUE) {
			lastDay = day;
			return;
		}
		if (day == lastDay) {
			return;
		}

		boolean newDay = day > lastDay;
		lastDay = day;
		if (!newDay) {
			return;
		}

		if (ThreadLocalRandom.current().nextDouble() < DAILY_CHANCE) {
			ResourceLocation pick = POOL[ThreadLocalRandom.current().nextInt(POOL.length)];
			TestShaderHandler.setDaily(pick);
			ownedShader = pick;
			remainingTicks = DURATION_TICKS;
		}
	}
}
