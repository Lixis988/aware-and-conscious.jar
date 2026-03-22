
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.lixis9.eventjar.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.lixis9.eventjar.client.renderer.WhoamiRenderer;
import net.lixis9.eventjar.client.renderer.WatcherRenderer;
import net.lixis9.eventjar.client.renderer.SonOfgodRenderer;
import net.lixis9.eventjar.client.renderer.SeekeractRenderer;
import net.lixis9.eventjar.client.renderer.SeekerRenderer;
import net.lixis9.eventjar.client.renderer.ScavengerRenderer;
import net.lixis9.eventjar.client.renderer.RageThen60sRenderer;
import net.lixis9.eventjar.client.renderer.PlayerRenderer;
import net.lixis9.eventjar.client.renderer.NoiseEntityRenderer;
import net.lixis9.eventjar.client.renderer.MeetboyfastRenderer;
import net.lixis9.eventjar.client.renderer.MeetboyRenderer;
import net.lixis9.eventjar.client.renderer.MeetboyLongRenderer;
import net.lixis9.eventjar.client.renderer.MeatboydistortedRenderer;
import net.lixis9.eventjar.client.renderer.Lixis9Renderer;
import net.lixis9.eventjar.client.renderer.InvisibleWeirdEntityRenderer;
import net.lixis9.eventjar.client.renderer.InvisibleDistRenderer;
import net.lixis9.eventjar.client.renderer.GodRenderer;
import net.lixis9.eventjar.client.renderer.FaultRenderer;
import net.lixis9.eventjar.client.renderer.EyesindarkRenderer;
import net.lixis9.eventjar.client.renderer.EyesRenderer;
import net.lixis9.eventjar.client.renderer.EyeRenderer;
import net.lixis9.eventjar.client.renderer.ErrundefineRenderer;
import net.lixis9.eventjar.client.renderer.EntitywhowatchyouRenderer;
import net.lixis9.eventjar.client.renderer.EntityCentipedeRenderer;
import net.lixis9.eventjar.client.renderer.Entity000125Renderer;
import net.lixis9.eventjar.client.renderer.CavemeetboyRenderer;
import net.lixis9.eventjar.client.renderer.CaretakerRenderer;
import net.lixis9.eventjar.client.renderer.DefaultVillagerRenderer;
import net.lixis9.eventjar.client.renderer.ScaryDefaultVillagerRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventjarModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EventjarModEntities.MEETBOY.get(), MeetboyRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.WHOAMI.get(), WhoamiRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.ERRUNDEFINE.get(), ErrundefineRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.ENTITY_000125.get(), Entity000125Renderer::new);
		event.registerEntityRenderer(EventjarModEntities.ENTITYWHOWATCHYOU.get(), EntitywhowatchyouRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.ENTITY_CENTIPEDE.get(), EntityCentipedeRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.LIXIS_9.get(), Lixis9Renderer::new);
		event.registerEntityRenderer(EventjarModEntities.RAGE_THEN_60S.get(), RageThen60sRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.MEETBOYFAST.get(), MeetboyfastRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.CARETAKER.get(), CaretakerRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.GOD.get(), GodRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.PLAYER.get(), PlayerRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.EYES.get(), EyesRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.EYE.get(), EyeRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.CAVEMEETBOY.get(), CavemeetboyRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.INVISIBLE_WEIRD_ENTITY.get(), InvisibleWeirdEntityRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.NOISE_ENTITY.get(), NoiseEntityRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.INVISIBLE_DIST.get(), InvisibleDistRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.SEEKER.get(), SeekerRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.SEEKERACT.get(), SeekeractRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.WATCHER.get(), WatcherRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.MEETBOY_LONG.get(), MeetboyLongRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.FAULT.get(), FaultRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.SCAVENGER.get(), ScavengerRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.MEATBOYDISTORTED.get(), MeatboydistortedRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.EYESINDARK.get(), EyesindarkRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.SON_OFGOD.get(), SonOfgodRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.DEFAULT_VILLAGER.get(), DefaultVillagerRenderer::new);
		event.registerEntityRenderer(EventjarModEntities.SCARY_DEFAULT_VILLAGER.get(), ScaryDefaultVillagerRenderer::new);
	}
}
