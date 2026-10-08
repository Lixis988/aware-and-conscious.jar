package net.lixis9.eventjar.client.renderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lixis.outofbound.client.renderer.BillboardFacing;
import net.lixis9.eventjar.entity.SonOfgodEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import java.util.Random;

import javax.annotation.Nonnull;

public class SonOfgodRenderer extends EntityRenderer<SonOfgodEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("eventjar:textures/entities/lvy_strashilka_4.png");
    private static final float SCALE_FACTOR = 3.0f;
    private static final float SHAKE_INTENSITY = 0.03f;
    private final Random random = new Random();

    public SonOfgodRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
        this.shadowStrength = 0.0f;
    }

    @Override
    public void render(@Nonnull SonOfgodEntity entity, float entityYaw, float partialTicks,
                       @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer,
                       int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0, 0.35f, 0);

        float shakeX = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY;
        float shakeY = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY * 0.2f;
        float shakeZ = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY;
        poseStack.translate(shakeX, shakeY, shakeZ);

        BillboardFacing.yawOnly(poseStack, this.entityRenderDispatcher);

        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);

        float width = 1.0f;
        float height = 2.0f;
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = 0.0F;
        float v1 = 1.0F;

        addVertex(vertexBuilder, pose, normal, -halfWidth, -halfHeight, 0, u0, v1, packedLight);

        addVertex(vertexBuilder, pose, normal, halfWidth, -halfHeight, 0, u1, v1, packedLight);

        addVertex(vertexBuilder, pose, normal, halfWidth, halfHeight, 0, u1, v0, packedLight);

        addVertex(vertexBuilder, pose, normal, -halfWidth, halfHeight, 0, u0, v0, packedLight);

        poseStack.popPose();
    }

    private void addVertex(VertexConsumer builder, Matrix4f pose, Matrix3f normal,
                           float x, float y, float z, float u, float v, int light) {
        builder.vertex(pose, x, y, z)
               .color(255, 255, 255, 255)
               .uv(u, v)
               .overlayCoords(OverlayTexture.NO_OVERLAY)
               .uv2(light)
               .normal(normal, 0, 0, 1)
               .endVertex();
    }

    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(@Nonnull SonOfgodEntity entity) {
        return TEXTURE;
    }
}
