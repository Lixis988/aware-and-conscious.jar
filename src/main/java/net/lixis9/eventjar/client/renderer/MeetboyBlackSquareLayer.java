package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MeetboyBlackSquareLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

	private static final ResourceLocation BLACK_SQUARE =
			new ResourceLocation(OutofboundMod.MODID, "textures/entities/black_square.png");
	private static final int FULL_BRIGHT = 15728880;
	private static final float SHAKE = 0.08F;

	private final float halfSize;

	public MeetboyBlackSquareLayer(RenderLayerParent<T, M> parent) {
		this(parent, 0.28F);
	}

	public MeetboyBlackSquareLayer(RenderLayerParent<T, M> parent, float halfSize) {
		super(parent);
		this.halfSize = halfSize;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		poseStack.pushPose();
		this.getParentModel().head.translateAndRotate(poseStack);

		float time = ageInTicks;
		int id = entity.getId();
		poseStack.translate(
				Mth.sin(time * 1.7F + id) * SHAKE,
				-0.05F + Mth.cos(time * 2.1F + id * 0.17F) * SHAKE,
				Mth.sin(time * 2.7F + id) * SHAKE * 0.5F
		);
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(time * 3.1F + id * 0.07F) * 10.0F));
		poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(time * 2.4F) * 6.0F));

		float half = this.halfSize;
		if ((entity.tickCount + id) % 31 < 2) {
			half *= 1.35F;
			poseStack.mulPose(Axis.YP.rotationDegrees((entity.getRandom().nextFloat() - 0.5F) * 50.0F));
		}

		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BLACK_SQUARE));
		cube(consumer, poseStack.last(), half);

		poseStack.popPose();
	}

	private static void cube(VertexConsumer consumer, PoseStack.Pose pose, float half) {
		Matrix4f m = pose.pose();
		Matrix3f n = pose.normal();
		quad(consumer, m, n, -half, -half, -half, half, -half, -half, half, half, -half, -half, half, -half, 0, 0, -1);
		quad(consumer, m, n, -half, -half, half, -half, half, half, half, half, half, half, -half, half, 0, 0, 1);
		quad(consumer, m, n, -half, -half, -half, -half, half, -half, -half, half, half, -half, -half, half, -1, 0, 0);
		quad(consumer, m, n, half, -half, -half, half, -half, half, half, half, half, half, half, -half, 1, 0, 0);
		quad(consumer, m, n, -half, -half, -half, -half, -half, half, half, -half, half, half, -half, -half, 0, -1, 0);
		quad(consumer, m, n, -half, half, -half, half, half, -half, half, half, half, -half, half, half, 0, 1, 0);
	}

	private static void quad(VertexConsumer c, Matrix4f m, Matrix3f n,
			float x1, float y1, float z1, float x2, float y2, float z2,
			float x3, float y3, float z3, float x4, float y4, float z4,
			float nx, float ny, float nz) {
		vert(c, m, n, x1, y1, z1, 0.0F, 1.0F, nx, ny, nz);
		vert(c, m, n, x2, y2, z2, 1.0F, 1.0F, nx, ny, nz);
		vert(c, m, n, x3, y3, z3, 1.0F, 0.0F, nx, ny, nz);
		vert(c, m, n, x4, y4, z4, 0.0F, 0.0F, nx, ny, nz);
	}

	private static void vert(VertexConsumer c, Matrix4f m, Matrix3f n, float x, float y, float z,
			float u, float v, float nx, float ny, float nz) {
		c.vertex(m, x, y, z)
				.color(0, 0, 0, 255)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(FULL_BRIGHT)
				.normal(n, nx, ny, nz)
				.endVertex();
	}
}
