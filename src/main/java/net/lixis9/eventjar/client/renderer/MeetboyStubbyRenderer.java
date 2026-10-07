package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lixis9.eventjar.client.model.MeetboyStubbyModel;
import net.lixis9.eventjar.entity.MeetboyStubbyEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MeetboyStubbyRenderer extends HumanoidMobRenderer<MeetboyStubbyEntity, MeetboyStubbyModel> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation("eventjar:textures/entities/24344ea9bfeb2f2c7e91f39f6026cdbf.png");

	public MeetboyStubbyRenderer(EntityRendererProvider.Context context) {
		super(context, new MeetboyStubbyModel(context.bakeLayer(ModelLayers.PLAYER)), 0.55F);
		this.addLayer(new MeetboyBlackSquareLayer<>(this, 0.34F));
	}

	@Override
	public ResourceLocation getTextureLocation(MeetboyStubbyEntity entity) {
		return TEXTURE;
	}

	@Override
	protected boolean isShaking(MeetboyStubbyEntity entity) {
		return true;
	}

	@Override
	protected void scale(MeetboyStubbyEntity entity, PoseStack poseStack, float partialTick) {
		float t = entity.tickCount + partialTick;
		int id = entity.getId();
		float sx = 1.15F + Mth.sin(t * 0.14F + id) * 0.08F;
		float sy = 0.72F + Mth.cos(t * 0.11F + id * 0.2F) * 0.06F;
		float sz = 1.1F + Mth.sin(t * 0.16F) * 0.07F;
		poseStack.scale(sx, sy, sz);

		float amp = 0.035F;
		poseStack.translate(
				Mth.sin(t * 2.2F + id) * amp,
				Mth.cos(t * 1.9F + id) * amp * 0.35F,
				Mth.sin(t * 2.8F) * amp * 0.35F
		);
		if ((entity.tickCount + id) % 29 < 2) {
			poseStack.translate((entity.getRandom().nextFloat() - 0.5F) * 0.15F, 0.0F, 0.0F);
		}
	}
}
