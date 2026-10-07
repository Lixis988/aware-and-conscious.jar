package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class CorruptionServerEvents {

	private CorruptionServerEvents() {
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		OutofboundMod.CORRUPTION.clear(event.getEntity().getUUID());
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (OutofboundMod.CORRUPTION.getLevel(event.getEntity().getUUID()) <= 0.0F) {
			return;
		}
		MinecraftServer server = event.getEntity().getServer();
		if (server != null) {
			CorruptionSnapshots.restoreAll(server);
		}
	}
}
