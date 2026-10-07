package net.lixis9.eventjar.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lixis.outofbound.client.MeatTextureSwap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class TitleScreenBackground {

	private TitleScreenBackground() {
	}

	public static void render(GuiGraphics graphics, int width, int height) {
		render(graphics, width, height, false);
	}

	public static void render(GuiGraphics graphics, int width, int height, boolean black) {
		if (black) {
			graphics.fill(0, 0, width, height, 0xFF000000);
			return;
		}
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		MeatTextureSwap.blitMeatBackground(graphics, width, height);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}
}
