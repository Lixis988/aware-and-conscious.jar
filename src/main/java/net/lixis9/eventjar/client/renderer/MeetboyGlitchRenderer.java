package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lixis9.eventjar.client.model.MeetboyGlitchModel;
import net.lixis9.eventjar.entity.MeetboyGlitchEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MeetboyGlitchRenderer extends HumanoidMobRenderer<MeetboyGlitchEntity, MeetboyGlitchModel> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation("eventjar:textures/entities/24344ea9bfeb2f2c7e91f39f6026cdbf.png");

	public MeetboyGlitchRenderer(EntityRendererProvider.Context context) {
		super(context, new MeetboyGlitchModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
		this.addLayer(new MeetboyBlackSquareLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(MeetboyGlitchEntity entity) {
		return TEXTURE;
	}

	@Override
	protected boolean isShaking(MeetboyGlitchEntity entity) {
		return true;
	}

	@Override
	protected void scale(MeetboyGlitchEntity entity, PoseStack poseStack, float partialTick) {
		float t = entity.tickCount + partialTick;
		int id = entity.getId();
		float sx = 0.92F + Mth.sin(t * 0.13F + id) * 0.1F;
		float sy = 1.08F + Mth.cos(t * 0.11F + id * 0.2F) * 0.14F;
		float sz = 0.88F + Mth.sin(t * 0.17F + 1.0F) * 0.12F;
		poseStack.scale(sx, sy, sz);

		float amp = 0.04F;
		poseStack.translate(
				Mth.sin(t * 2.4F + id) * amp,
				Mth.cos(t * 1.8F + id) * amp * 0.5F,
				Mth.sin(t * 3.0F) * amp * 0.4F
		);
		if ((entity.tickCount + id) % 37 < 2) {
			poseStack.translate((entity.getRandom().nextFloat() - 0.5F) * 0.2F, 0.0F, 0.0F);
			poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((entity.getRandom().nextFloat() - 0.5F) * 12.0F));
		}
	}
}
