package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.client.WorldProgressClientState;
import net.lixis.outofbound.feature.corruption.corruptors.DataCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ICorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.RenderCorruptor;
import net.lixis.outofbound.feature.corruption.util.CorruptorSelector;
import net.lixis.outofbound.feature.corruption.util.CorruptionScaling;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public final class CorruptionClientEngine {

	private static final List<ICorruptor> CORRUPTORS = new ArrayList<>();
	private static int clientTickCounter;

	private CorruptionClientEngine() {
	}

	public static void init() {
		CORRUPTORS.clear();
		CORRUPTORS.add(new DataCorruptor());
		CORRUPTORS.add(new RenderCorruptor());
	}

	public static void tick() {
		CorruptionClientState.clientTick();
		if (!CorruptionClientState.isActive()) {
			return;
		}

		float level = CorruptionClientState.getLevel();
		int burstBefore = CorruptionScheduler.getBurstRemaining();
		CorruptionScheduler.tick(level);
		int burstAfter = CorruptionScheduler.getBurstRemaining();
		if (burstAfter > burstBefore) {
			CorruptionClientState.extendRenderCorruption(burstAfter * 4);
		}

		Minecraft minecraft = Minecraft.getInstance();
		long daysSince = WorldProgressClientState.getDaysSinceBoundedcowCollision(minecraft);

		clientTickCounter++;
		boolean burst = CorruptionScheduler.isBurstActive();
		boolean ambient = !burst && clientTickCounter % CorruptionScaling.ambientIntervalTicks(level) == 0;
		if (!burst && !ambient) {
			maybeRunPreview(minecraft, level, daysSince);
			return;
		}

		runActions(minecraft, level, daysSince, burst
				? OutofboundMod.CORRUPTION.actionCount(level)
				: 1);

		maybeRunPreview(minecraft, level, daysSince);
	}

	private static void runActions(Minecraft minecraft, float level, long daysSince, int actions) {
		List<ICorruptor> eligible = CorruptorSelector.eligibleForLevel(CORRUPTORS, level, daysSince);
		if (eligible.isEmpty() || actions <= 0) {
			return;
		}

		CorruptionRunContext ctx = CorruptionRunContext.client(minecraft, level);
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (int i = 0; i < actions; i++) {
			ICorruptor picked = CorruptorSelector.pickWeighted(eligible, level, random);
			if (CorruptionScaling.rollAction(level, picked.minLevel(), random)) {
				picked.run(ctx);
			}
		}
	}

	private static void maybeRunPreview(Minecraft minecraft, float level, long daysSince) {
		if (!CorruptionDayStages.isPreviewActive(daysSince) || level <= 0.0F) {
			return;
		}
		if (clientTickCounter % CorruptionDayStages.PREVIEW_INTERVAL_TICKS != 0) {
			return;
		}

		List<ICorruptor> eligible = CorruptorSelector.eligiblePreview(CORRUPTORS, level, daysSince);
		if (eligible.isEmpty()) {
			return;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextFloat() >= CorruptionDayStages.PREVIEW_ROLL_CHANCE) {
			return;
		}

		ICorruptor picked = CorruptorSelector.pickWeighted(eligible, level, random);
		CorruptionRunContext ctx = CorruptionRunContext.client(minecraft, level);
		picked.run(ctx);
	}

	public static boolean shouldCorrupt(float level) {
		return CorruptionClientState.isActive()
				&& level > 0.0F
				&& CorruptionScheduler.isBurstActive();
	}

	public static boolean shouldRenderCorrupt() {
		return CorruptionClientState.isActive() && CorruptionClientState.getLevel() >= 15.0F;
	}
}
