package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.client.EyeAnimFrames;
import net.lixis.outofbound.entity.SeraphEyeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SeraphEyeEntityRenderer extends EntityRenderer<SeraphEyeEntity> {

	private static final int FULL_BRIGHT = 15728880;
	private static final ResourceLocation TEXTURE = new ResourceLocation(OutofboundMod.MODID, "textures/entities/eye_anim.png");
	private static final ResourceLocation WHITE = new ResourceLocation("minecraft", "textures/misc/white.png");
	private static final ResourceLocation GOLD = new ResourceLocation("minecraft", "textures/block/gold_block.png");

	private static final float EYE_WIDTH = 2.2F;
	private static final float EYE_HEIGHT = 2.2F;
	private static final float CUBE_SIZE = 0.22F;

	private static final float[][] RING_CONFIG = {
			{1.4F, 60.0F, 2.5F, 20.0F},
			{1.7F, -45.0F, -1.8F, 26.0F},
			{2.0F, 30.0F, 1.2F, 32.0F}
	};

	public SeraphEyeEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.0F;
	}

	@Override
	public ResourceLocation getTextureLocation(SeraphEyeEntity entity) {
		return TEXTURE;
	}

	@Override
	public boolean shouldRender(SeraphEyeEntity entity, Frustum frustum, double camX, double camY, double camZ) {
		if (!super.shouldRender(entity, frustum, camX, camY, camZ)) {
			return entity.distanceToSqr(camX, camY, camZ) < 64.0D * 64.0D;
		}
		return true;
	}

	@Override
	public void render(SeraphEyeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		long timeMs = (long) ((entity.tickCount + partialTicks) * 50.0F);
		int frameIndex = EyeAnimFrames.frameForTime(timeMs);
		float u0 = frameIndex / (float) EyeAnimFrames.COUNT;
		float u1 = (frameIndex + 1) / (float) EyeAnimFrames.COUNT;
		float age = entity.tickCount + partialTicks;
		double bob = Math.sin(age * 0.05F) * 0.15D;

		Vec3 camPos = this.entityRenderDispatcher.camera.getPosition();
		double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
		double ey = Mth.lerp(partialTicks, entity.yo, entity.getY()) + bob;
		double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
		double dx = camPos.x - ex;
		double dy = camPos.y - ey;
		double dz = camPos.z - ez;
		double horiz = Math.sqrt(dx * dx + dz * dz);
		float lookYaw = (float) Math.toDegrees(Math.atan2(dx, dz));
		float lookPitch = (float) Math.toDegrees(Math.atan2(dy, horiz));

		poseStack.pushPose();
		poseStack.translate(0.0D, bob, 0.0D);

		renderCentralEye(poseStack, buffer, u0, u1, lookYaw, lookPitch);
		renderRings(poseStack, buffer, age);

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	private void renderCentralEye(PoseStack poseStack, MultiBufferSource buffer, float u0, float u1, float lookYaw, float lookPitch) {
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(lookYaw));
		poseStack.mulPose(Axis.XP.rotationDegrees(-lookPitch));

		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();

		float half = EYE_WIDTH * 0.5F;
		float top = EYE_HEIGHT * 0.5F;
		float bottom = -EYE_HEIGHT * 0.5F;

		VertexConsumer background = buffer.getBuffer(RenderType.entityCutoutNoCull(WHITE));
		blackVertex(background, matrix, normal, -half, bottom, -0.02F);
		blackVertex(background, matrix, normal, half, bottom, -0.02F);
		blackVertex(background, matrix, normal, half, top, -0.02F);
		blackVertex(background, matrix, normal, -half, top, -0.02F);

		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		vertex(consumer, matrix, normal, -half, bottom, u0, 1.0F);
		vertex(consumer, matrix, normal, half, bottom, u1, 1.0F);
		vertex(consumer, matrix, normal, half, top, u1, 0.0F);
		vertex(consumer, matrix, normal, -half, top, u0, 0.0F);

		poseStack.popPose();
	}

	private static void blackVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z) {
		consumer.vertex(matrix, x, y, z)
				.color(0, 0, 0, 255)
				.uv(0.5F, 0.5F)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(FULL_BRIGHT)
				.normal(normal, 0.0F, 0.0F, 1.0F)
				.endVertex();
	}

	private void renderRings(PoseStack poseStack, MultiBufferSource buffer, float age) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(GOLD));

		for (float[] ring : RING_CONFIG) {
			float radius = ring[0];
			float tilt = ring[1];
			float speed = ring[2];
			int cubeCount = (int) ring[3];

			poseStack.pushPose();
			poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
			poseStack.mulPose(Axis.YP.rotationDegrees(age * speed));

			for (int i = 0; i < cubeCount; i++) {
				float angle = (float) (Math.PI * 2.0D * i / cubeCount);
				float x = (float) Math.cos(angle) * radius;
				float z = (float) Math.sin(angle) * radius;

				poseStack.pushPose();
				poseStack.translate(x, 0.0D, z);
				poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(angle)));
				renderCube(consumer, poseStack.last(), 0.0F, 1.0F);
				poseStack.popPose();
			}

			poseStack.popPose();
		}
	}

	private static void renderCube(VertexConsumer consumer, PoseStack.Pose pose, float u0, float u1) {
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();
		float half = CUBE_SIZE * 0.5F;

		quad(consumer, matrix, normal, -half, -half, half, half, -half, half, half, half, half, -half, half, half, u0, u1, 0.0F, 0.0F, 1.0F);

		quad(consumer, matrix, normal, half, -half, -half, -half, -half, -half, -half, half, -half, half, half, -half, u0, u1, 0.0F, 0.0F, -1.0F);

		quad(consumer, matrix, normal, half, -half, half, half, half, half, half, half, -half, half, -half, -half, u0, u1, 1.0F, 0.0F, 0.0F);

		quad(consumer, matrix, normal, -half, -half, -half, -half, half, -half, -half, half, half, -half, -half, half, u0, u1, -1.0F, 0.0F, 0.0F);

		quad(consumer, matrix, normal, -half, half, -half, -half, half, half, half, half, half, half, half, -half, u0, u1, 0.0F, 1.0F, 0.0F);

		quad(consumer, matrix, normal, -half, -half, half, half, -half, half, half, -half, -half, -half, -half, -half, u0, u1, 0.0F, -1.0F, 0.0F);
	}

	private static void quad(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4,
			float u0, float u1, float nx, float ny, float nz) {
		vertex(consumer, matrix, normal, x1, y1, z1, u0, 1.0F, nx, ny, nz);
		vertex(consumer, matrix, normal, x2, y2, z2, u1, 1.0F, nx, ny, nz);
		vertex(consumer, matrix, normal, x3, y3, z3, u1, 0.0F, nx, ny, nz);
		vertex(consumer, matrix, normal, x4, y4, z4, u0, 0.0F, nx, ny, nz);
	}

	private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z,
			float u, float v, float nx, float ny, float nz) {
		consumer.vertex(matrix, x, y, z)
				.color(255, 255, 255, 255)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(FULL_BRIGHT)
				.normal(normal, nx, ny, nz)
				.endVertex();
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
