
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mimicevent.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.mimicevent.client.renderer.ScaryDefaultVillagerRenderer;
import net.mcreator.mimicevent.client.renderer.DefaultVillagerRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MimiceventModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MimiceventModEntities.DEFAULT_VILLAGER.get(), DefaultVillagerRenderer::new);
		event.registerEntityRenderer(MimiceventModEntities.SCARY_DEFAULT_VILLAGER.get(), ScaryDefaultVillagerRenderer::new);
	}
}
