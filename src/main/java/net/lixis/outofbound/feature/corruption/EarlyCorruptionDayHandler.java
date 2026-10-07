package net.lixis.outofbound.feature.corruption;

import net.lixis9.eventjar.DarknessConfig;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.corruptors.ChatCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.MobSwapCorruptor;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class EarlyCorruptionDayHandler {

	@SuppressWarnings("unused")
	private static final String OWNERSHIP = net.lixis.outofbound.Authorship.NOTICE;

	private EarlyCorruptionDayHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!DarknessConfig.ENABLE_DAMAGE_CORRUPTION) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (server.getTickCount() % 20 != 0) {
			return;
		}
		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}

		long daysSince = WorldInternalConfig.getDaysSinceBoundedcowCollision(server);
		if (!CorruptionDayStages.isSimpleActive(daysSince)) {
			return;
		}
		if (server.getTickCount() % CorruptionDayStages.SIMPLE_INTERVAL_TICKS != 0) {
			return;
		}

		int ttl = OutofboundMod.CORRUPTION.effectTtl(Math.max(5.0F, daysSince * 5.0F));
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (OutofboundMod.CORRUPTION.hasManualOverride(player.getUUID())) {
				continue;
			}
			MobSwapCorruptor.swapNearPlayer(player.serverLevel(), player, ttl, false, daysSince);
			ChatCorruptor.sendOne(player);
		}
	}
}
