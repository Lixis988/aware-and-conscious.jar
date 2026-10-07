package net.lixis.outofbound.client;

import net.lixis.outofbound.client.model.BedrockAnimationPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class UndefiendAnimationLoader {

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((preparationBarrier, resourceManager, prepareProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
				preparationBarrier.wait(null).thenRunAsync(() -> BedrockAnimationPlayer.get().load(resourceManager), gameExecutor));
	}
}
