package net.lixis.outofbound;

import net.lixis.outofbound.client.BoundedcowGlitchEffect;
import net.lixis.outofbound.client.UndefiendGlitchScheduler;
import net.lixis.outofbound.entity.BoundedcowEntity;
import net.lixis.outofbound.entity.UndefiendEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class BoundedcowGlitchClientHandler {

	private static final float UNDEFIEND_GLITCH_INTENSITY = 0.22F;

	@SubscribeEvent
	public static void onRenderPre(RenderLivingEvent.Pre<?, ?> event) {
		if (event.getEntity() instanceof BoundedcowEntity entity) {
			float time = entity.tickCount + event.getPartialTick();
			BoundedcowGlitchEffect.beginTextureGlitch(time, entity.getId());
			return;
		}

		if (event.getEntity() instanceof UndefiendEntity entity) {
			float time = entity.tickCount + event.getPartialTick();
			if (!UndefiendGlitchScheduler.isGlitchActive(entity.getId(), time)) {
				return;
			}
			BoundedcowGlitchEffect.beginTextureGlitch(time, entity.getId(), UNDEFIEND_GLITCH_INTENSITY);
		}
	}

	@SubscribeEvent
	public static void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
		if (event.getEntity() instanceof BoundedcowEntity || event.getEntity() instanceof UndefiendEntity) {
			BoundedcowGlitchEffect.endTextureGlitch();
		}
	}
}
