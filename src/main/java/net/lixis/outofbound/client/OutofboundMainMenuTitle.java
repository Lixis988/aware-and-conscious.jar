package net.lixis.outofbound.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class OutofboundMainMenuTitle {

	public static final ResourceLocation MINECRAFT_TITLE = new ResourceLocation(OutofboundMod.MODID,
			"textures/titles/minecraft_title.png");

	public static final int TEXTURE_WIDTH = 500;
	public static final int TEXTURE_HEIGHT = 500;

	public static final int SRC_X = 113;
	public static final int SRC_Y = 215;
	public static final int SRC_WIDTH = 275;
	public static final int SRC_HEIGHT = 96;
	public static final int MAX_DISPLAY_WIDTH = 240;
	private static final int SCREEN_MARGIN = 16;
	private static final int MIN_TOP_MARGIN = 12;

	private static final float HORIZONTAL_SQUEEZE = 0.9F;

	private OutofboundMainMenuTitle() {
	}

	public static void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
		int buttonRow = screenHeight / 4 + 48;
		int naturalWidth = Math.min(screenWidth - SCREEN_MARGIN * 2, MAX_DISPLAY_WIDTH);
		int displayHeight = Math.max(1, naturalWidth * SRC_HEIGHT / SRC_WIDTH);
		int displayWidth = Math.max(1, Math.round(naturalWidth * HORIZONTAL_SQUEEZE));
		int x = (screenWidth - displayWidth) / 2;
		int y = Math.max(MIN_TOP_MARGIN, (buttonRow - displayHeight) / 2);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		graphics.blit(MINECRAFT_TITLE, x, y, displayWidth, displayHeight, (float) SRC_X, (float) SRC_Y, SRC_WIDTH,
				SRC_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
