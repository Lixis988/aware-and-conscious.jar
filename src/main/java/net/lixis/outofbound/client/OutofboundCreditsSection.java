package net.lixis.outofbound.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.client.texture.GifTextureAtlas;
import net.lixis.outofbound.mixin.WinScreenAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class OutofboundCreditsSection {

	private static final ResourceLocation EYE_GIF = new ResourceLocation(OutofboundMod.MODID, "textures/entities/eye.gif");
	private static final ResourceLocation EYE_PNG_FALLBACK = new ResourceLocation(OutofboundMod.MODID, "textures/entities/eye_strip.png");

	private static final int PADDING_LINES = 6;
	private static final int TITLE_BLOCK_LINES = 10;
	private static final int EYE_BLOCK_LINES = 11;
	private static final int SPACING_LINES = 2;

	private static int sectionStartLine = -1;

	private OutofboundCreditsSection() {
	}

	public static void append(WinScreenAccessor screen) {
		sectionStartLine = screen.outofbound$getLines().size();

		for (int i = 0; i < PADDING_LINES; i++) {
			screen.outofbound$addEmptyLine();
		}
		for (int i = 0; i < TITLE_BLOCK_LINES; i++) {
			screen.outofbound$addEmptyLine();
		}
		for (int i = 0; i < SPACING_LINES; i++) {
			screen.outofbound$addEmptyLine();
		}
		for (int i = 0; i < EYE_BLOCK_LINES; i++) {
			screen.outofbound$addEmptyLine();
		}
		for (int i = 0; i < SPACING_LINES; i++) {
			screen.outofbound$addEmptyLine();
		}

		screen.outofbound$addCreditsLine(Component.literal("you are a fool"), true);
		for (int i = 0; i < 4; i++) {
			screen.outofbound$addEmptyLine();
		}
	}

	public static void render(GuiGraphics graphics, WinScreen screen) {
		if (sectionStartLine < 0) {
			return;
		}

		int width = screen.width;
		int height = screen.height;
		int baseY = height + 150;

		int titleLine = sectionStartLine + PADDING_LINES;
		int titleY = baseY + titleLine * 12;
		renderTitle(graphics, width, titleY);

		int eyeLine = titleLine + TITLE_BLOCK_LINES + SPACING_LINES;
		int eyeY = baseY + eyeLine * 12;
		renderEye(graphics, width, eyeY);
	}

	private static void renderTitle(GuiGraphics graphics, int screenWidth, int y) {
		int naturalWidth = Math.min(screenWidth - 32, OutofboundMainMenuTitle.MAX_DISPLAY_WIDTH);
		int displayHeight = Math.max(1, naturalWidth * OutofboundMainMenuTitle.SRC_HEIGHT / OutofboundMainMenuTitle.SRC_WIDTH);
		int displayWidth = Math.max(1, Math.round(naturalWidth * 0.9F));
		int x = (screenWidth - displayWidth) / 2;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		graphics.blit(
				OutofboundMainMenuTitle.MINECRAFT_TITLE,
				x,
				y,
				displayWidth,
				displayHeight,
				(float) OutofboundMainMenuTitle.SRC_X,
				(float) OutofboundMainMenuTitle.SRC_Y,
				OutofboundMainMenuTitle.SRC_WIDTH,
				OutofboundMainMenuTitle.SRC_HEIGHT,
				OutofboundMainMenuTitle.TEXTURE_WIDTH,
				OutofboundMainMenuTitle.TEXTURE_HEIGHT);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private static void renderEye(GuiGraphics graphics, int screenWidth, int y) {
		GifTextureAtlas atlas = GifTextureAtlas.getOrLoad(EYE_GIF, EYE_PNG_FALLBACK);
		if (atlas == null) {
			return;
		}

		int frameWidth = atlas.frameWidth();
		int frameHeight = atlas.frameHeight();
		int displaySize = Math.min(160, screenWidth - 48);
		int frame = atlas.frameIndexForTime(System.currentTimeMillis());
		int u = frame * frameWidth;
		int x = (screenWidth - displaySize) / 2;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.blit(
				atlas.textureLocation(),
				x,
				y,
				u,
				0,
				displaySize,
				displaySize,
				atlas.frameCount() * frameWidth,
				frameHeight);
	}
}
