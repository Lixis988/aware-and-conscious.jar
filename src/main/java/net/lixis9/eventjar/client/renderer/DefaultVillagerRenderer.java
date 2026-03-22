
package net.mcreator.mimicevent.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.VillagerModel;

import net.mcreator.mimicevent.entity.DefaultVillagerEntity;

import com.mojang.blaze3d.vertex.PoseStack;

public class DefaultVillagerRenderer extends MobRenderer<DefaultVillagerEntity, VillagerModel<DefaultVillagerEntity>> {
	public DefaultVillagerRenderer(EntityRendererProvider.Context context) {
		super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
	}

	@Override
	protected void scale(DefaultVillagerEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(0.9375f, 0.9375f, 0.9375f);
	}

	@Override
	public ResourceLocation getTextureLocation(DefaultVillagerEntity entity) {
		return new ResourceLocation("mimicevent:textures/entities/villager.png");
	}
}
