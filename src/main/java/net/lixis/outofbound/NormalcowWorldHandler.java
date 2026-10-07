package net.lixis.outofbound;

import net.lixis.outofbound.procedures.NormalcowFovSpawnManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.MinecraftServer;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class NormalcowWorldHandler {

	private NormalcowWorldHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (server == null) {
			return;
		}

		NormalcowFovSpawnManager.tick(server);
	}
}
