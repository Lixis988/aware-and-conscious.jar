
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.lixis9.eventjar.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.lixis9.eventjar.client.model.Modelunknown;
import net.lixis9.eventjar.client.model.ModelRUNH;
import net.lixis9.eventjar.client.model.ModelRUNC;
import net.lixis9.eventjar.client.model.ModelRUNB;
import net.lixis9.eventjar.client.model.ModelRUN;
import net.lixis9.eventjar.client.model.DefaultVillagerModel;
import net.lixis9.eventjar.client.model.ScaryDefaultVillagerModel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class EventjarModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelRUN.LAYER_LOCATION, ModelRUN::createBodyLayer);
		event.registerLayerDefinition(ModelRUNH.LAYER_LOCATION, ModelRUNH::createBodyLayer);
		event.registerLayerDefinition(ModelRUNB.LAYER_LOCATION, ModelRUNB::createBodyLayer);
		event.registerLayerDefinition(Modelunknown.LAYER_LOCATION, Modelunknown::createBodyLayer);
		event.registerLayerDefinition(ModelRUNC.LAYER_LOCATION, ModelRUNC::createBodyLayer);
		event.registerLayerDefinition(DefaultVillagerModel.LAYER_LOCATION, DefaultVillagerModel::createBodyLayer);
		event.registerLayerDefinition(ScaryDefaultVillagerModel.LAYER_LOCATION, ScaryDefaultVillagerModel::createBodyLayer);
	}
}
