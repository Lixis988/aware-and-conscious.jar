package net.lixis.outofbound.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.CowModel;

import net.lixis.outofbound.entity.NormalcowEntity;

public class NormalcowRenderer extends MobRenderer<NormalcowEntity, CowModel<NormalcowEntity>> {
	public NormalcowRenderer(EntityRendererProvider.Context context) {
		super(context, new CowModel(context.bakeLayer(ModelLayers.COW)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(NormalcowEntity entity) {
		return new ResourceLocation("outofbound:textures/entities/image-photoroom.png");
	}
}
