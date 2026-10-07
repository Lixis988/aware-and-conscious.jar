package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.client.model.HeartMesh;
import net.lixis.outofbound.entity.HeartDecorEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HeartDecorRenderer extends EntityRenderer<HeartDecorEntity> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation(OutofboundMod.MODID, "textures/entities/heart.png");
	private static final int FULL_BRIGHT = 15728880;
	private static final float SCALE = 1.35F;

	private static final int BEAT_PERIOD = HeartDecorEntity.BEAT_PERIOD_TICKS;

	public HeartDecorRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.45F;
	}

	@Override
	public ResourceLocation getTextureLocation(HeartDecorEntity entity) {
		return TEXTURE;
	}

	@Override
	public void render(HeartDecorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		if (!HeartMesh.isReady()) {
			return;
		}
		poseStack.pushPose();
		float t = entity.tickCount + partialTicks;
		float pulse = heartbeatScale(t);
		float bob = Mth.sin(t * 0.08F) * 0.06F;
		poseStack.translate(0.0D, bob + 0.2D, 0.0D);
		poseStack.scale(SCALE * pulse, SCALE * pulse, SCALE * pulse);

		VertexConsumer solid = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		HeartMesh.render(poseStack, solid, FULL_BRIGHT, 255, 255, 255, 255);

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	private static float heartbeatScale(float t) {
		float phase = t % BEAT_PERIOD;
		if (phase < 0.0F) {
			phase += BEAT_PERIOD;
		}
		float lub = beatBump(phase, 0.0F, 3.5F, 0.22F);
		float dub = beatBump(phase, 7.0F, 2.8F, 0.12F);
		return 1.0F + Math.max(lub, dub);
	}

	private static float beatBump(float phase, float start, float width, float amp) {
		float local = phase - start;
		if (local < 0.0F || local > width) {
			return 0.0F;
		}
		return amp * Mth.sin(local / width * (float) Math.PI);
	}
}
