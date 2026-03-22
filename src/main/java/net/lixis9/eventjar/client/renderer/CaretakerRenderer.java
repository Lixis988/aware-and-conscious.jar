
package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.lixis9.eventjar.entity.CaretakerEntity;
import net.lixis9.eventjar.client.model.ModelRUNH;

public class CaretakerRenderer extends MobRenderer<CaretakerEntity, ModelRUNH<CaretakerEntity>> {
	public CaretakerRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRUNH(context.bakeLayer(ModelRUNH.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(CaretakerEntity entity) {
		return new ResourceLocation("eventjar:textures/entities/fayafyfayfayfay.png");
	}
}
