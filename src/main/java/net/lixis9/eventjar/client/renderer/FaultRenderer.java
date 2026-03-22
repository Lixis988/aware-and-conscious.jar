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
import net.lixis9.eventjar.entity.FaultEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import java.util.Random;

public class FaultRenderer extends EntityRenderer<FaultEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("eventjar:textures/entities/fayafyafyfayafy.png");
    private static final float SCALE_FACTOR = 2.8f; // Средний размер между старым и новым
    private static final float SHAKE_INTENSITY = 0.05f; // Уменьшена дрожь
    private final Random random = new Random();

    public FaultRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
        this.shadowStrength = 0.0f;
    }

    @Override
    public void render(FaultEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        // Позиционирование - поднимаем выше уровня блоков, чтобы ноги не залезали под блоки
        poseStack.translate(0, 1.5f, 0);
        
        // Эффект дрожи
        float shakeX = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY;
        float shakeY = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY * 0.5f;
        float shakeZ = (random.nextFloat() - 0.5f) * SHAKE_INTENSITY;
        poseStack.translate(shakeX, shakeY, shakeZ);
        
        // Поворот к игроку (billboard эффект)
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        
        // Коррекция ориентации
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        
        // Масштабирование
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);
        
        // Размеры спрайта - средний размер
        float width = 1.2f;
        float height = 2.4f;
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        
        // Рендеринг
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        
        // Правильные координаты UV для плоской модели
        float u0 = 0.0F; // Левая граница текстуры
        float u1 = 1.0F; // Правая граница текстуры
        float v0 = 0.0F; // Верх текстуры (голова)
        float v1 = 1.0F; // Низ текстуры (ноги)
        
        // Корректировка координат для правильного отображения
        float vHead = v0 + (v1 - v0) * 0.3f; // 30% текстуры для головы
        float vBody = v0 + (v1 - v0) * 0.7f; // 70% текстуры для тела
        
        // Построение прямоугольника с правильными координатами
        // Левый верхний угол (голова)
        addVertex(vertexBuilder, pose, normal, -halfWidth, -halfHeight, 0, u0, v0, packedLight);
        // Правый верхний угол (голова)
        addVertex(vertexBuilder, pose, normal, halfWidth, -halfHeight, 0, u1, v0, packedLight);
        // Правый нижний угол (ноги)
        addVertex(vertexBuilder, pose, normal, halfWidth, halfHeight, 0, u1, v1, packedLight);
        // Левый нижний угол (ноги)
        addVertex(vertexBuilder, pose, normal, -halfWidth, halfHeight, 0, u0, v1, packedLight);
        
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
    public ResourceLocation getTextureLocation(FaultEntity entity) {
        return TEXTURE;
    }
}