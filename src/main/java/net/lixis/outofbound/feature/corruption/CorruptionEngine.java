package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.corruptors.BossBarCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ChatCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ChunkCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.EntityCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ICorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.InventoryCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.ItemReplaceCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.MobFlashCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.MobSwapCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.PhysicsCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.SoundCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.StackCountCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.WorldCorruptor;
import net.lixis.outofbound.feature.corruption.util.CorruptorSelector;
import net.lixis.outofbound.feature.corruption.util.CorruptionScaling;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class CorruptionEngine {

	private static final List<ICorruptor> CORRUPTORS = new ArrayList<>();

	private CorruptionEngine() {
	}

	public static void init() {
		CORRUPTORS.clear();
		CORRUPTORS.add(new EntityCorruptor());
		CORRUPTORS.add(new MobSwapCorruptor());
		CORRUPTORS.add(new MobFlashCorruptor());
		CORRUPTORS.add(new PhysicsCorruptor());
		CORRUPTORS.add(new ChunkCorruptor());
		CORRUPTORS.add(new WorldCorruptor());
		CORRUPTORS.add(new SoundCorruptor());
		CORRUPTORS.add(new ChatCorruptor());
		CORRUPTORS.add(new BossBarCorruptor());
		CORRUPTORS.add(new InventoryCorruptor());
		CORRUPTORS.add(new ItemReplaceCorruptor());
		CORRUPTORS.add(new StackCountCorruptor());
	}

	public static void runPlayerBurst(ServerPlayer player, float level) {
		MinecraftServer server = player.getServer();
		long daysSince = server != null && WorldInternalConfig.hasBoundedcowCollision(server)
				? WorldInternalConfig.getDaysSinceBoundedcowCollision(server)
				: 0L;
		runActions(player, level, OutofboundMod.CORRUPTION.actionCount(level), daysSince);
	}

	public static void tick(MinecraftServer server) {
		float maxLevel = 0.0F;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			float level = OutofboundMod.CORRUPTION.getLevel(player.getUUID());
			if (level > maxLevel) {
				maxLevel = level;
			}
		}

		int burstBefore = CorruptionScheduler.getBurstRemaining();
		CorruptionScheduler.tick(maxLevel);
		int burstAfter = CorruptionScheduler.getBurstRemaining();

		if (burstAfter > burstBefore) {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				float level = OutofboundMod.CORRUPTION.getLevel(player.getUUID());
				if (level > 0.0F) {
					CorruptionSyncPacket.sendToPlayer(player, level, true, burstAfter);
				}
			}
		}

		long daysSince = WorldInternalConfig.hasBoundedcowCollision(server)
				? WorldInternalConfig.getDaysSinceBoundedcowCollision(server)
				: 0L;
		int tick = server.getTickCount();

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			float level = OutofboundMod.CORRUPTION.getLevel(player.getUUID());
			if (level <= 0.0F) {
				continue;
			}

			if (CorruptionScheduler.isBurstActive()) {
				runActions(player, level, OutofboundMod.CORRUPTION.actionCount(level), daysSince);
			} else if (tick % CorruptionScaling.ambientIntervalTicks(level) == 0) {
				runActions(player, level, 1, daysSince);
			}

			if (CorruptionDayStages.isPreviewActive(daysSince)
					&& tick % CorruptionDayStages.PREVIEW_INTERVAL_TICKS == 0) {
				runPreviewActions(player, level, daysSince);
			}

			MobProximitySwapHandler.tick(player, level, daysSince);
		}
	}

	private static void runActions(ServerPlayer player, float level, int actions, long daysSince) {
		if (!OutofboundMod.CORRUPTION.enabled() || level <= 0.0F || actions <= 0) {
			return;
		}

		List<ICorruptor> eligible = CorruptorSelector.eligibleForLevel(CORRUPTORS, level, daysSince);
		if (eligible.isEmpty()) {
			return;
		}

		CorruptionRunContext ctx = CorruptionRunContext.server(player, level);
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (int i = 0; i < actions; i++) {
			ICorruptor picked = CorruptorSelector.pickWeighted(eligible, level, random);
			if (CorruptionScaling.rollAction(level, picked.minLevel(), random)) {
				picked.run(ctx);
			}
		}
	}

	private static void runPreviewActions(ServerPlayer player, float level, long daysSince) {
		if (!OutofboundMod.CORRUPTION.enabled() || level <= 0.0F) {
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
		CorruptionRunContext ctx = CorruptionRunContext.server(player, level);
		picked.run(ctx);
	}

	public static boolean shouldCorrupt(float level) {
		return OutofboundMod.CORRUPTION.enabled()
				&& level > 0.0F
				&& CorruptionScheduler.isBurstActive();
	}
}
