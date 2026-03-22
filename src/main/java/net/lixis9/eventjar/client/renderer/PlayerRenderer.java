
package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.lixis9.eventjar.entity.PlayerEntity;
import net.lixis9.eventjar.client.model.ModelRUNC;

public class PlayerRenderer extends MobRenderer<PlayerEntity, ModelRUNC<PlayerEntity>> {
	public PlayerRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRUNC(context.bakeLayer(ModelRUNC.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(PlayerEntity entity) {
		return new ResourceLocation("eventjar:textures/entities/texture.222png.png");
	}
}
