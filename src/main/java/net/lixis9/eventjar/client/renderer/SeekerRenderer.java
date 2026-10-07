package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lixis9.eventjar.entity.SeekerEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SeekerRenderer extends HumanoidMobRenderer<SeekerEntity, HumanoidModel<SeekerEntity>> {

	public SeekerRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(SeekerEntity entity) {
		return SeekerGlitchVisuals.textureFor(entity);
	}

	@Override
	protected boolean isShaking(SeekerEntity entity) {
		return true;
	}

	@Override
	public void render(SeekerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffers, int packedLight) {
		poseStack.pushPose();
		applyGlitch(entity, partialTicks, poseStack);
		super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
		poseStack.popPose();
	}

	@Override
	protected void scale(SeekerEntity entity, PoseStack poseStack, float partialTick) {
		poseStack.scale(
				SeekerGlitchVisuals.scaleX(entity, partialTick),
				SeekerGlitchVisuals.scaleY(entity, partialTick),
				SeekerGlitchVisuals.scaleZ(entity, partialTick)
		);
	}

	private static void applyGlitch(SeekerEntity entity, float partialTicks, PoseStack poseStack) {
		float age = entity.tickCount + partialTicks;
		int id = entity.getId();
		float phase = (id % 16) * 0.7F;

		float jitterX = Mth.sin(age * 28.0F + phase) * 0.045F
				+ Mth.sin(age * 41.0F + phase * 0.9F) * 0.02F;
		float jitterY = Mth.cos(age * 33.0F + phase) * 0.03F;
		poseStack.translate(jitterX, jitterY, 0.0D);
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(age * 22.0F + phase) * 8.0F));
		poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(age * 18.0F) * 4.0F));

		if (SeekerGlitchVisuals.hardSnap(entity)) {
			poseStack.translate((entity.getRandom().nextFloat() - 0.5F) * 0.25F, 0.0F, 0.0F);
			poseStack.mulPose(Axis.YP.rotationDegrees((entity.getRandom().nextFloat() - 0.5F) * 35.0F));
		}
	}
}
