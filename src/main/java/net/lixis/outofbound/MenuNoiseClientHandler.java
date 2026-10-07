package net.lixis.outofbound;

import net.lixis9.eventjar.DarknessConfig;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class MenuNoiseClientHandler {

	private static long frameCounter = 0L;

	private MenuNoiseClientHandler() {
	}

	@SubscribeEvent
	public static void onTitleScreenPost(ScreenEvent.Render.Post event) {
		if (!DarknessConfig.NOISE_ENABLED) {
			return;
		}
		if (!(event.getScreen() instanceof TitleScreen titleScreen)) {
			return;
		}

		frameCounter++;
		ResourceLocation noise = MenuGaussianNoiseTexture.getForFrame(frameCounter);

		for (Renderable renderable : titleScreen.renderables) {
			if (renderable instanceof AbstractWidget widget && widget.visible) {
				MenuWidgetNoiseRenderer.render(event.getGuiGraphics(), widget, noise, frameCounter);
			}
		}
	}
}
