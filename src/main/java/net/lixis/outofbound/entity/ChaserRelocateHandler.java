package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class ChaserRelocateHandler {

	private ChaserRelocateHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		ChaserSpawnManager.tickRelocations(event.getServer());
	}
}
