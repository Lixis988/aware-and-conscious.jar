package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

import net.lixis9.eventjar.entity.ErrundefineEntity;
import net.lixis9.eventjar.client.model.ModelRUN;

import com.mojang.blaze3d.vertex.PoseStack;

public class ErrundefineRenderer extends MobRenderer<ErrundefineEntity, ModelRUN<ErrundefineEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("eventjar:textures/entities/45cf117a935715d7528897330b5c7ff1.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("eventjar:textures/entities/err_undefine_glow.png");

    public ErrundefineRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelRUN<ErrundefineEntity>(context.bakeLayer(ModelRUN.LAYER_LOCATION)), 0.7f);
        this.addLayer(new GlowingLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ErrundefineEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ErrundefineEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        float scale = 1.0F + (Mth.sin(entity.tickCount * 0.2F) * 0.1F);
        poseStack.scale(scale, scale, scale);

        if (entity.getRandom().nextInt(100) < 30) {
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(entity.getRandom().nextFloat() * 10 - 5));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(entity.getRandom().nextFloat() * 10 - 5));
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected boolean isShaking(ErrundefineEntity entity) {
        return true;
    }

    @Override
    protected void scale(ErrundefineEntity entity, PoseStack poseStack, float partialTickTime) {

        float pulse = 1.0F + Mth.sin(entity.tickCount * 0.5F) * 0.1F;
        poseStack.scale(pulse, pulse, pulse);

        float distortX = 1.0F + (entity.getRandom().nextFloat() - 0.5F) * 0.2F;
        float distortY = 1.0F + (entity.getRandom().nextFloat() - 0.5F) * 0.2F;
        float distortZ = 1.0F + (entity.getRandom().nextFloat() - 0.5F) * 0.2F;
        poseStack.scale(distortX, distortY, distortZ);
    }

    private static class GlowingLayer extends net.minecraft.client.renderer.entity.layers.RenderLayer<ErrundefineEntity, ModelRUN<ErrundefineEntity>> {
        private final ResourceLocation texture;

        public GlowingLayer(ErrundefineRenderer renderer) {
            super(renderer);
            this.texture = GLOW_TEXTURE;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ErrundefineEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            var vertexConsumer = buffer.getBuffer(RenderType.eyes(texture));
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728640,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                1, 1, 1, 1.0F);
        }
    }
}
