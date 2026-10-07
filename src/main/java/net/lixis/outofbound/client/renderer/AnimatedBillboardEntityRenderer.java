package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.client.EyeAnimFrames;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AnimatedBillboardEntityRenderer<T extends Entity> extends EntityRenderer<T> {

	private static final int FULL_BRIGHT = 15728880;
	private static final ResourceLocation TEXTURE = new ResourceLocation(OutofboundMod.MODID, "textures/entities/eye_anim.png");

	private final float width;
	private final float height;

	public AnimatedBillboardEntityRenderer(EntityRendererProvider.Context context, float width, float height) {
		super(context);
		this.width = width;
		this.height = height;
		this.shadowRadius = width * 0.5F;
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEXTURE;
	}

	@Override
	public boolean shouldRender(T entity, Frustum frustum, double camX, double camY, double camZ) {
		if (!super.shouldRender(entity, frustum, camX, camY, camZ)) {
			return entity.distanceToSqr(camX, camY, camZ) < 64.0D * 64.0D;
		}
		return true;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		long timeMs = (entity.tickCount + (long) (partialTicks * 20.0F)) * 50L;
		int frameIndex = EyeAnimFrames.frameForTime(timeMs);

		float u0 = frameIndex / (float) EyeAnimFrames.COUNT;
		float u1 = (frameIndex + 1) / (float) EyeAnimFrames.COUNT;

		poseStack.pushPose();
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

		float half = this.width * 0.5F;
		float top = this.height * 0.5F;
		float bottom = -this.height * 0.5F;

		vertex(consumer, matrix, normal, -half, bottom, u0, 1.0F);
		vertex(consumer, matrix, normal, half, bottom, u1, 1.0F);
		vertex(consumer, matrix, normal, half, top, u1, 0.0F);
		vertex(consumer, matrix, normal, -half, top, u0, 0.0F);

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
