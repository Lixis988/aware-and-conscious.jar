package net.lixis.outofbound.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.lixis.outofbound.client.renderer.UndefiendRenderer;
import net.lixis.outofbound.client.renderer.CaveUndefiendRenderer;
import net.lixis.outofbound.client.renderer.OverworldUndefiendRenderer;
import net.lixis.outofbound.client.renderer.NormalcowRenderer;
import net.lixis.outofbound.client.renderer.BoundedcowRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OutofboundModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(OutofboundModEntities.BOUNDEDCOW.get(), BoundedcowRenderer::new);
		event.registerEntityRenderer(OutofboundModEntities.NORMALCOW.get(), NormalcowRenderer::new);
		event.registerEntityRenderer(OutofboundModEntities.UNDEFIEND.get(), UndefiendRenderer::new);
		event.registerEntityRenderer(OutofboundModEntities.OVERWORLD_UNDEFIEND.get(), OverworldUndefiendRenderer::new);
		event.registerEntityRenderer(OutofboundModEntities.CAVE_UNDEFIEND.get(), CaveUndefiendRenderer::new);
	}
}
