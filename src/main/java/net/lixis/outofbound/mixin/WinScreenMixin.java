package net.lixis.outofbound.mixin;

import net.lixis.outofbound.client.OutofboundCreditsSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.WinScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WinScreen.class)
public abstract class WinScreenMixin {

	@Inject(method = "wrapCreditsIO", at = @At("HEAD"))
	private void outofbound$prependModCredits(String path, @Coerce Object reader, CallbackInfo ci) {
		if ("texts/credits.json".equals(path)) {
			OutofboundCreditsSection.append((WinScreenAccessor) this);
		}
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
					shift = At.Shift.AFTER))
	private void outofbound$renderModCredits(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		OutofboundCreditsSection.render(graphics, (WinScreen) (Object) this);
	}
}
