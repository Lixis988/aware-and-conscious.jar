
package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.lixis9.eventjar.entity.WhoamiEntity;

public class WhoamiRenderer extends HumanoidMobRenderer<WhoamiEntity, HumanoidModel<WhoamiEntity>> {
	public WhoamiRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(WhoamiEntity entity) {
		return new ResourceLocation("eventjar:textures/entities/4p8ewu0.png");
	}

	@Override
	protected boolean isShaking(WhoamiEntity entity) {
		return true;
	}
}
