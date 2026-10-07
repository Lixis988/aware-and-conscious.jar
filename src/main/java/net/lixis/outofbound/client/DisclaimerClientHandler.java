package net.lixis.outofbound.client;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class DisclaimerClientHandler {

	private static boolean accepted;

	private DisclaimerClientHandler() {
	}

	public static void markAccepted() {
		accepted = true;
	}

	public static boolean isAccepted() {
		return accepted;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onScreenOpening(ScreenEvent.Opening event) {
		if (accepted) {
			return;
		}
		if (event.getNewScreen() instanceof TitleScreen && !(event.getNewScreen() instanceof DisclaimerScreen)) {
			event.setNewScreen(new DisclaimerScreen());
		}
	}
}
