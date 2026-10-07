package net.lixis.outofbound;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MenuWidgetNoiseRenderer {

	private static final int TEX_SIZE = 32;
	private static final float OVERLAY_ALPHA = 0.55F;

	private MenuWidgetNoiseRenderer() {
	}

	public static void render(GuiGraphics graphics, AbstractWidget widget, ResourceLocation noise, long frameSeed) {
		int x = widget.getX();
		int y = widget.getY();
		int w = widget.getWidth();
		int h = widget.getHeight();

		if (w <= 0 || h <= 0) {
			return;
		}

		int u = (int) ((x * 13 + frameSeed * 3L) % TEX_SIZE);
		int v = (int) ((y * 17 + frameSeed * 5L) % TEX_SIZE);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		graphics.enableScissor(x, y, x + w, y + h);
		graphics.setColor(1.0F, 1.0F, 1.0F, OVERLAY_ALPHA);
		graphics.blit(noise, x, y, w, h, u, v, w, h, TEX_SIZE, TEX_SIZE);
		graphics.disableScissor();
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
