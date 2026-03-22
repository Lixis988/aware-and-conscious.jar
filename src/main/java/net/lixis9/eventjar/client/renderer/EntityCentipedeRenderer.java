
package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.lixis9.eventjar.entity.EntityCentipedeEntity;

public class EntityCentipedeRenderer extends MobRenderer<EntityCentipedeEntity, HumanoidModel<EntityCentipedeEntity>> {
	public EntityCentipedeRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityCentipedeEntity entity) {
		return new ResourceLocation("eventjar:textures/entities/afsafsafsafsafsafsafs.png");
	}

	@Override
	protected boolean isShaking(EntityCentipedeEntity entity) {
		return true;
	}
}
