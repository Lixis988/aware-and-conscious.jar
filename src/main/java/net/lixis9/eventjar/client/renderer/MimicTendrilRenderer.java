package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lixis.outofbound.OutofboundMod;
import net.lixis9.eventjar.client.model.MimicTendrilModel;
import net.lixis9.eventjar.entity.MimicTendrilEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MimicTendrilRenderer extends HumanoidMobRenderer<MimicTendrilEntity, MimicTendrilModel> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation(OutofboundMod.MODID, "textures/entities/black_square.png");

	public MimicTendrilRenderer(EntityRendererProvider.Context context) {
		super(context, new MimicTendrilModel(context.bakeLayer(ModelLayers.PLAYER)), 0.35F);
	}

	@Override
	public ResourceLocation getTextureLocation(MimicTendrilEntity entity) {
		return TEXTURE;
	}

	@Override
	protected boolean isShaking(MimicTendrilEntity entity) {
		return true;
	}

	@Override
	protected void scale(MimicTendrilEntity entity, PoseStack poseStack, float partialTick) {
		float t = entity.tickCount + partialTick;
		float emerge = Mth.clamp(entity.tickCount / 12.0F, 0.15F, 1.0F);
		poseStack.scale(0.45F * emerge, 2.4F * emerge, 0.45F * emerge);
		poseStack.translate(0.0D, (1.0F - emerge) * -1.2D, 0.0D);
		poseStack.translate(Mth.sin(t * 3.0F) * 0.04F, 0.0D, Mth.cos(t * 2.5F) * 0.03F);
	}

	@Override
	public void render(MimicTendrilEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		float t = entity.tickCount + partialTicks;
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(t * 0.4F) * 6.0F));
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
