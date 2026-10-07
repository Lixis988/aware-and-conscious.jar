package net.lixis9.eventjar.mixin;

import net.lixis.outofbound.client.OutofboundMainMenuTitle;
import net.lixis9.eventjar.client.TitleScreenBackground;
import net.lixis9.eventjar.client.TitleScreenGlitch;
import net.lixis9.eventjar.client.TitleScreenSplash;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.internal.BrandingControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

	@Inject(method = "init", at = @At("TAIL"))
	private void eventjar$forceSplash(CallbackInfo ci) {
		((TitleScreenAccessor) (Object) this).eventjar$setSplash(new SplashRenderer(TitleScreenSplash.TEXT));
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void eventjar$renderMenu(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		TitleScreen screen = (TitleScreen) (Object) this;
		TitleScreenAccessor access = (TitleScreenAccessor) (Object) this;

		TitleScreenGlitch.update();
		TitleScreenBackground.render(guiGraphics, screen.width, screen.height, TitleScreenGlitch.isBackgroundBlack());

		for (GuiEventListener child : screen.children()) {
			if (child instanceof AbstractWidget widget) {
				widget.setAlpha(1.0F);
			}
		}

		if (TitleScreenGlitch.useCustomTitle()) {
			OutofboundMainMenuTitle.render(guiGraphics, screen.width, screen.height);
		} else {
			access.eventjar$getLogoRenderer().renderLogo(guiGraphics, screen.width, 1.0F);
		}

		SplashRenderer splash = access.eventjar$getSplash();
		if (splash == null) {
			splash = new SplashRenderer(TitleScreenSplash.TEXT);
			access.eventjar$setSplash(splash);
		}
		((SplashRendererAccessor) (Object) splash).eventjar$setSplash(TitleScreenSplash.TEXT);
		splash.render(guiGraphics, screen.width, Minecraft.getInstance().font, 0xFF000000);

		for (Renderable renderable : screen.renderables) {
			renderable.render(guiGraphics, mouseX, mouseY, partialTick);
		}

		Font font = Minecraft.getInstance().font;
		int color = 0xFFFFFFFF;
		BrandingControl.forEachLine(true, true, (line, text) -> guiGraphics.drawString(
				font, text, 2, screen.height - (10 + line * (font.lineHeight + 1)), color));
		BrandingControl.forEachAboveCopyrightLine((line, text) -> guiGraphics.drawString(
				font, text, screen.width - font.width(text),
				screen.height - (10 + (line + 1) * (font.lineHeight + 1)), color));

		ci.cancel();
	}
}
