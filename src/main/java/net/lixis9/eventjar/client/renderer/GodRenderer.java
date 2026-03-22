package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.lixis9.eventjar.entity.GodEntity;
import net.lixis9.eventjar.client.model.ModelRUNB;

public class GodRenderer extends MobRenderer<GodEntity, ModelRUNB<GodEntity>> {
    public GodRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelRUNB(context.bakeLayer(ModelRUNB.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GodEntity entity) {
        return new ResourceLocation("eventjar:textures/entities/texture2.png");
    }

    /**
     * Добавляем сильную тряску/дергание модели.
     * Подбирать параметры freq/ampRot/ampTrans для желаемого эффекта.
     */
    @Override
    public void render(GodEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // возраст в тиках с дробной частью — хороший источник "времени" для синусоид
        float age = entity.tickCount + partialTicks;

        // Частоты (чем больше — тем быстрее дрожит)
        float freq1 = 7.0f;   // вращение по X
        float freq2 = 9.0f;   // вращение по Z
        float freq3 = 15.0f;  // вертикальная тряска
        float freq4 = 11.0f;  // горизонтальная тряска

        // Амплитуды (настройте: большие значения = более страшно дергается)
        float ampRotDegX = 35.0f;   // градусы вращения по X
        float ampRotDegZ = 40.0f;   // градусы вращения по Z
        float ampTransY = 0.18f;    // смещение вверх/вниз в блоках (метрах)
        float ampTransX = 0.14f;    // смещение влево/вправо

        // Дополнительная "неровность" — комбинируем несколько синусов для хаоса
        float rotX = (float) Math.sin(age * freq1) * ampRotDegX
                   + (float) Math.sin(age * (freq1 * 1.37)) * (ampRotDegX * 0.3f);
        float rotZ = (float) Math.cos(age * freq2) * ampRotDegZ
                   + (float) Math.cos(age * (freq2 * 1.19)) * (ampRotDegZ * 0.35f);

        float transY = (float) Math.sin(age * freq3) * ampTransY
                     + (float) Math.sin(age * (freq3 * 0.6)) * (ampTransY * 0.4f);
        float transX = (float) Math.sin(age * freq4) * ampTransX
                     + (float) Math.cos(age * (freq4 * 1.3)) * (ampTransX * 0.45f);

        // Применяем трансформации: сначала перевод, затем вращения
        poseStack.translate(transX, transY, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));

        // Сам рендер модели (после трансформаций)
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
