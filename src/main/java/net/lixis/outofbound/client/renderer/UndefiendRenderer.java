package net.lixis.outofbound.client.renderer;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.client.model.Modelunknown;
import net.lixis.outofbound.client.model.UndefiendModel;
import net.lixis.outofbound.entity.UndefiendEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;

public class UndefiendRenderer extends MobRenderer<UndefiendEntity, UndefiendModel> {

	private static final float SCALE = 2.0F;
	private static final ResourceLocation TEXTURE = new ResourceLocation(OutofboundMod.MODID, "textures/entities/texture22.png");
	private static final int FULL_BRIGHT = 15728880;

	public UndefiendRenderer(EntityRendererProvider.Context context) {
		super(context, new UndefiendModel(context.bakeLayer(Modelunknown.LAYER_LOCATION)), 1.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(UndefiendEntity entity) {
		return TEXTURE;
	}

	@Override
	protected void scale(UndefiendEntity entity, PoseStack poseStack, float partialTick) {
		poseStack.scale(SCALE, SCALE, SCALE);
	}

	@Override
	public void render(UndefiendEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, FULL_BRIGHT);
	}
}
