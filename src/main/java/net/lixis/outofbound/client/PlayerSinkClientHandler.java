package net.lixis.outofbound.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lixis.outofbound.OutofboundMod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class PlayerSinkClientHandler {

	private PlayerSinkClientHandler() {
	}

	@SubscribeEvent
	public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {

	}
}
