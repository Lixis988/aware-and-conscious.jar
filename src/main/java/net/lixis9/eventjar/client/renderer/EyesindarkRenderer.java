package net.lixis9.eventjar.client.renderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.lixis9.eventjar.entity.EyesindarkEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.LightTexture;

import java.util.Random;
import javax.annotation.Nonnull;

public class EyesindarkRenderer extends EntityRenderer<EyesindarkEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("eventjar:textures/entities/afsfsaasfafsasfasfsafasf.png");
    private static final float SCALE_FACTOR = 3.0f;
    private static final float SHAKE_INTENSITY = 0.03f;
    private final Random random = new Random();

    public EyesindarkRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
    }

    @Override
    public void render(@Nonnull EyesindarkEntity entity, float entityYaw, float partialTicks, 
                       @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        poseStack.translate(0, 0.35f, 0);
        float shakeX = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY;
        float shakeZ = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY;
        poseStack.translate(shakeX, 0, shakeZ);

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);

        float halfWidth = 0.5f;
        float halfHeight = 0.5f;

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Светящийся слой
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.eyes(TEXTURE));
        buildFace(glowConsumer, pose, normal, -halfWidth, -halfHeight, halfWidth, halfHeight, LightTexture.FULL_BRIGHT);

        // Основной слой
        VertexConsumer mainConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        buildFace(mainConsumer, pose, normal, -halfWidth, -halfHeight, halfWidth, halfHeight, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void buildFace(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, 
                          float x1, float y1, float x2, float y2, int light) {
        consumer.vertex(pose, x1, y1, 0.001f)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(0.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();
                
        consumer.vertex(pose, x2, y1, 0.001f)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(1.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();
                
        consumer.vertex(pose, x2, y2, 0.001f)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(1.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();
                
        consumer.vertex(pose, x1, y2, 0.001f)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(0.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(@Nonnull EyesindarkEntity entity) {
        return TEXTURE;
    }
}