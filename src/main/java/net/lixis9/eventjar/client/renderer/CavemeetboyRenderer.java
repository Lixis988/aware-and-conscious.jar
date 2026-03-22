
package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.lixis9.eventjar.entity.CavemeetboyEntity;

public class CavemeetboyRenderer extends HumanoidMobRenderer<CavemeetboyEntity, HumanoidModel<CavemeetboyEntity>> {
	public CavemeetboyRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(CavemeetboyEntity entity) {
		return new ResourceLocation("eventjar:textures/entities/24344ea9bfeb2f2c7e91f39f6026cdbf.png");
	}

	@Override
	protected boolean isShaking(CavemeetboyEntity entity) {
		return true;
	}
}
