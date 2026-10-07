package net.lixis.outofbound;

import net.lixis.outofbound.client.WorldProgressClientState;
import net.lixis.outofbound.feature.corruption.render.MemoryCorruptionGate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class WorldProgressClientHandler {

	private WorldProgressClientHandler() {
	}

	@SubscribeEvent
	public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		WorldProgressClientState.clear();
	}

	@SubscribeEvent
	public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
		MemoryCorruptionGate.clearManualOverride();
	}
}
