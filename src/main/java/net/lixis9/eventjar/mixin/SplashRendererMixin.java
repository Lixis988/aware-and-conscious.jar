package net.lixis9.eventjar.mixin;

import net.lixis9.eventjar.client.TitleScreenSplash;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashRenderer.class)
public abstract class SplashRendererMixin {

	@Inject(method = "render", at = @At("HEAD"))
	private void eventjar$forceSplashText(GuiGraphics graphics, int screenWidth, Font font, int color, CallbackInfo ci) {
		if (Minecraft.getInstance().screen instanceof TitleScreen) {
			((SplashRendererAccessor) (Object) this).eventjar$setSplash(TitleScreenSplash.TEXT);
		}
	}
}
