package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BillboardEntityRenderer<T extends Entity> extends EntityRenderer<T> {

	private static final int FULL_BRIGHT = 15728880;

	private final ResourceLocation texture;
	private final float width;
	private final float height;

	public BillboardEntityRenderer(EntityRendererProvider.Context context, ResourceLocation texture, float width, float height) {
		super(context);
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.shadowRadius = width * 0.5F;
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return texture;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		BillboardFacing.yawOnly(poseStack, this.entityRenderDispatcher);

		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

		float half = this.width * 0.5F;
		float top = this.height;

		vertex(consumer, matrix, normal, -half, 0.0F, 0.0F, 1.0F);
		vertex(consumer, matrix, normal, half, 0.0F, 1.0F, 1.0F);
		vertex(consumer, matrix, normal, half, top, 1.0F, 0.0F);
		vertex(consumer, matrix, normal, -half, top, 0.0F, 0.0F);

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
