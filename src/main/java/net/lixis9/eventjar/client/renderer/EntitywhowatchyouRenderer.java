
package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.lixis9.eventjar.entity.EntitywhowatchyouEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class EntitywhowatchyouRenderer extends HumanoidMobRenderer<EntitywhowatchyouEntity, HumanoidModel<EntitywhowatchyouEntity>> {
	public EntitywhowatchyouRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
		this.addLayer(new RenderLayer<EntitywhowatchyouEntity, HumanoidModel<EntitywhowatchyouEntity>>(this) {
			final ResourceLocation LAYER_TEXTURE = new ResourceLocation("eventjar:textures/entities/afsafsasffasasffas.png");

			@Override
			public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, EntitywhowatchyouEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(LAYER_TEXTURE));
				this.getParentModel().renderToBuffer(poseStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);
			}
		});
	}

	@Override
	public ResourceLocation getTextureLocation(EntitywhowatchyouEntity entity) {
		return new ResourceLocation("eventjar:textures/entities/fasafsasfsafafsfasafs.png");
	}

	@Override
	protected boolean isShaking(EntitywhowatchyouEntity entity) {
		return true;
	}
}
