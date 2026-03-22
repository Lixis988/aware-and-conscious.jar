package net.lixis9.eventjar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.lixis9.eventjar.entity.SeekeractEntity;

public class SeekeractRenderer extends HumanoidMobRenderer<SeekeractEntity, HumanoidModel<SeekeractEntity>> {
	public SeekeractRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer<>(this,
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(SeekeractEntity entity) {
		return new ResourceLocation("eventjar:textures/entities/fdsafsfsaa222sfafs.png");
	}

	/**
	 * Если нужно, чтобы сущность всегда считалась "дрожащей" — можно оставить true.
	 * (Это влияет на стандартное поведение туши/дрожания в некоторых рендерах.)
	 */
	@Override
	protected boolean isShaking(SeekeractEntity entity) {
		return true;
	}

	/**
	 * Накладываем небольшой "нервный тик" перед рендером модели.
	 * Параметры:
	 *   - freq          : базовая частота дрожи (чем больше — тем быстрее тик)
	 *   - ampTrans{X,Y} : амплитуды смещений в блоках (0.01-0.05 — едва заметно, 0.1+ — сильно)
	 *   - ampRotDegZ    : амплитуда вращения в градусах вокруг Z (немного наклоняет)
	 */
	@Override
	public void render(SeekeractEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		poseStack.pushPose();

		// "время" для синусов — тики + дробная часть
		float age = entity.tickCount + partialTicks;

		// Фаза по сущности, чтобы разные экземпляры дрожали по-разному
		float phase = (entity.getId() % 16) * 0.7f;

		// Настройки — подгоняй по вкусу
		float freq = 30.0f;           // как быстро дергается (больше = быстрее)
		float ampTransX = 0.03f;      // смещение влево/вправо (в блоках)
		float ampTransY = 0.02f;      // вертикальная мелкая дрожь
		float ampRotDegZ = 6.0f;      // небольшая качка (градусы)
		float ampRotDegX = 3.0f;      // легкое покачивание вперед/назад

		// Комбинируем несколько синусоид для "нервного" неравномерного тика
		float jitterX = (float) Math.sin(age * freq + phase) * ampTransX
					  + (float) Math.sin(age * (freq * 1.37f) + phase * 0.9f) * (ampTransX * 0.45f);
		float jitterY = (float) Math.cos(age * (freq * 1.1f) + phase * 1.5f) * ampTransY
					  + (float) Math.sin(age * (freq * 0.6f) + phase * 0.6f) * (ampTransY * 0.6f);

		float rotZ = (float) Math.sin(age * (freq * 0.85f) + phase * 1.2f) * ampRotDegZ
				   + (float) Math.cos(age * (freq * 1.6f) + phase * 0.4f) * (ampRotDegZ * 0.33f);
		float rotX = (float) Math.sin(age * (freq * 0.95f) + phase * 0.7f) * ampRotDegX;

		// Накладываем трансформации: сначала перевод, затем вращение
		poseStack.translate(jitterX, jitterY, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));

		// Рендер модели уже с применённой тряской
		super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);

		poseStack.popPose();
	}
}
