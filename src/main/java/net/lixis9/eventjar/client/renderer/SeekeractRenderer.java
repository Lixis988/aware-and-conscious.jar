package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lixis9.eventjar.entity.SeekeractEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SeekeractRenderer extends HumanoidMobRenderer<SeekeractEntity, HumanoidModel<SeekeractEntity>> {

	public SeekeractRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(SeekeractEntity entity) {
		return SeekerGlitchVisuals.textureFor(entity);
	}

	@Override
	protected boolean isShaking(SeekeractEntity entity) {
		return true;
	}

	@Override
	public void render(SeekeractEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffers, int packedLight) {
		poseStack.pushPose();

		float age = entity.tickCount + partialTicks;
		float phase = (entity.getId() % 16) * 0.7F;
		float freq = 36.0F;

		float jitterX = Mth.sin(age * freq + phase) * 0.055F
				+ Mth.sin(age * freq * 1.4F + phase) * 0.03F;
		float jitterY = Mth.cos(age * freq * 1.1F + phase) * 0.04F;
		poseStack.translate(jitterX, jitterY, 0.0D);
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(age * freq * 0.9F) * 10.0F));
		poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(age * freq * 0.8F) * 5.0F));

		if (SeekerGlitchVisuals.hardSnap(entity)) {
			poseStack.translate((entity.getRandom().nextFloat() - 0.5F) * 0.35F,
					(entity.getRandom().nextFloat() - 0.5F) * 0.15F, 0.0F);
			poseStack.mulPose(Axis.YP.rotationDegrees((entity.getRandom().nextFloat() - 0.5F) * 48.0F));
		}

		super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
		poseStack.popPose();
	}

	@Override
	protected void scale(SeekeractEntity entity, PoseStack poseStack, float partialTick) {
		poseStack.scale(
				SeekerGlitchVisuals.scaleX(entity, partialTick) * 1.05F,
				SeekerGlitchVisuals.scaleY(entity, partialTick) * 1.08F,
				SeekerGlitchVisuals.scaleZ(entity, partialTick)
		);
	}
}
