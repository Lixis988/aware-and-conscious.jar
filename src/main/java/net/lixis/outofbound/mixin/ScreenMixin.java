package net.lixis.outofbound.mixin;

import net.lixis.outofbound.client.MeatTextureSwap;
import net.lixis9.eventjar.AacConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {

	@Inject(method = "renderDirtBackground", at = @At("HEAD"), cancellable = true)
	private void outofbound$meatDirtBackground(GuiGraphics graphics, CallbackInfo ci) {
		if (!AacConfig.meatSwapEnabled()) {
			return;
		}
		Screen self = (Screen) (Object) this;
		MeatTextureSwap.blitMeatBackground(graphics, self.width, self.height);
		ci.cancel();
	}
}
