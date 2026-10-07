package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.entity.Entity3Entity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Entity3Renderer extends HumanoidMobRenderer<Entity3Entity, PlayerModel<Entity3Entity>> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation(OutofboundMod.MODID, "textures/entities/entity3.png");
	private static final float SHAKE_AMPLITUDE = 0.08F;

	public Entity3Renderer(EntityRendererProvider.Context context) {
		super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity3Entity entity) {
		return TEXTURE;
	}

	@Override
	public void render(Entity3Entity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		float time = entity.tickCount + partialTicks;
		int id = entity.getId();
		float jitterX = Mth.sin(time * 1.7F + id) * SHAKE_AMPLITUDE
				+ Mth.cos(time * 2.3F + id * 0.31F) * SHAKE_AMPLITUDE * 0.6F;
		float jitterY = Mth.cos(time * 2.1F + id * 0.17F) * SHAKE_AMPLITUDE * 0.45F
				+ Mth.sin(time * 1.9F + id) * SHAKE_AMPLITUDE * 0.35F;
		float jitterZ = Mth.sin(time * 2.7F + id * 0.11F) * SHAKE_AMPLITUDE * 0.55F;
		float jitterRot = Mth.sin(time * 3.1F + id * 0.07F) * 4.0F;

		poseStack.translate(jitterX, jitterY, jitterZ);
		poseStack.mulPose(Axis.YP.rotationDegrees(jitterRot * 0.35F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(jitterRot * 0.25F));

		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
