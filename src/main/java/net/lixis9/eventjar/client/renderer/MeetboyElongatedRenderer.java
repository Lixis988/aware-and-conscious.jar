package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lixis9.eventjar.client.model.MeetboyElongatedModel;
import net.lixis9.eventjar.entity.MeetboyElongatedEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MeetboyElongatedRenderer extends HumanoidMobRenderer<MeetboyElongatedEntity, MeetboyElongatedModel> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation("eventjar:textures/entities/24344ea9bfeb2f2c7e91f39f6026cdbf.png");

	public MeetboyElongatedRenderer(EntityRendererProvider.Context context) {
		super(context, new MeetboyElongatedModel(context.bakeLayer(ModelLayers.PLAYER)), 0.45F);
		this.addLayer(new MeetboyBlackSquareLayer<>(this, 0.22F));
	}

	@Override
	public ResourceLocation getTextureLocation(MeetboyElongatedEntity entity) {
		return TEXTURE;
	}

	@Override
	protected boolean isShaking(MeetboyElongatedEntity entity) {
		return true;
	}

	@Override
	protected void scale(MeetboyElongatedEntity entity, PoseStack poseStack, float partialTick) {
		float t = entity.tickCount + partialTick;
		int id = entity.getId();
		float sx = 0.85F + Mth.sin(t * 0.1F + id) * 0.06F;
		float sy = 1.35F + Mth.cos(t * 0.09F + id * 0.2F) * 0.08F;
		float sz = 0.82F + Mth.sin(t * 0.12F) * 0.05F;
		poseStack.scale(sx, sy, sz);

		float amp = 0.03F;
		poseStack.translate(
				Mth.sin(t * 2.0F + id) * amp,
				Mth.cos(t * 1.6F + id) * amp * 0.4F,
				Mth.sin(t * 2.5F) * amp * 0.3F
		);
	}
}
