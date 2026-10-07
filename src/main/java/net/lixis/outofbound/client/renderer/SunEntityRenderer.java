package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.entity.TheSunEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SunEntityRenderer extends EntityRenderer<TheSunEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(OutofboundMod.MODID, "textures/entities/thesun.png");
	private static final int FULL_BRIGHT = 15728880;

	private final float size;

	public SunEntityRenderer(EntityRendererProvider.Context context, float size) {
		super(context);
		this.size = size;
		this.shadowRadius = 0.0F;
	}

	@Override
	public ResourceLocation getTextureLocation(TheSunEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(TheSunEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		poseStack.translate(0.0D, this.size * 0.5D, 0.0D);
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

		float half = this.size * 0.5F;

		vertex(consumer, matrix, normal, half, -half, 0.0F, 0.0F);
		vertex(consumer, matrix, normal, -half, -half, 1.0F, 0.0F);
		vertex(consumer, matrix, normal, -half, half, 1.0F, 1.0F);
		vertex(consumer, matrix, normal, half, half, 0.0F, 1.0F);

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float u, float v) {
		consumer.vertex(matrix, x, y, 0.0F)
				.color(255, 255, 255, 255)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(FULL_BRIGHT)
				.normal(normal, 0.0F, 0.0F, 1.0F)
				.endVertex();
	}
}
