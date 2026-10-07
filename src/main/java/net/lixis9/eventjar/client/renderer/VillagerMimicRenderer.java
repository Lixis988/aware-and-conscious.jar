package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lixis9.eventjar.entity.VillagerMimicEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VillagerMimicRenderer extends MobRenderer<VillagerMimicEntity, VillagerModel<VillagerMimicEntity>> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/villager/villager.png");

	public VillagerMimicRenderer(EntityRendererProvider.Context context) {
		super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
		this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
		this.addLayer(new CrossedArmsItemLayer<>(this, context.getItemInHandRenderer()));
	}

	@Override
	public ResourceLocation getTextureLocation(VillagerMimicEntity entity) {
		return TEXTURE;
	}

	@Override
	protected void scale(VillagerMimicEntity entity, PoseStack poseStack, float partialTick) {
		poseStack.scale(0.9375F, 0.9375F, 0.9375F);
		if (entity.isGlitching()) {
			float t = entity.tickCount + partialTick;
			float sx = 0.85F + Mth.sin(t * 18.0F) * 0.35F;
			float sy = 1.15F + Mth.cos(t * 22.0F) * 0.45F;
			float sz = 0.7F + Mth.sin(t * 14.0F) * 0.4F;
			poseStack.scale(sx, sy, sz);
		}
	}

	@Override
	public void render(VillagerMimicEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		if (entity.isGlitching()) {
			float t = entity.tickCount + partialTicks;
			poseStack.translate(Mth.sin(t * 40.0F) * 0.12F, Mth.cos(t * 33.0F) * 0.08F, 0.0D);
			poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(t * 25.0F) * 18.0F));
			poseStack.mulPose(Axis.YP.rotationDegrees(Mth.cos(t * 19.0F) * 25.0F));
		}
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		poseStack.popPose();
	}

	@Override
	protected boolean isShaking(VillagerMimicEntity entity) {
		return entity.isGlitching();
	}
}
