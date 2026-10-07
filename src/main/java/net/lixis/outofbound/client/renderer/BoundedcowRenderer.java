package net.lixis.outofbound.client.renderer;

import net.lixis.outofbound.client.model.GlitchCowModel;
import net.lixis.outofbound.entity.BoundedcowEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BoundedcowRenderer extends MobRenderer<BoundedcowEntity, GlitchCowModel> {
	public BoundedcowRenderer(EntityRendererProvider.Context context) {
		super(context, new GlitchCowModel(context.bakeLayer(ModelLayers.COW)), 1.8f);
	}

	@Override
	public ResourceLocation getTextureLocation(BoundedcowEntity entity) {
		return new ResourceLocation("outofbound:textures/entities/image-photoroom.png");
	}

	@Override
	protected boolean isShaking(BoundedcowEntity entity) {
		return true;
	}
}
