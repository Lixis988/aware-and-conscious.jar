package net.lixis.outofbound.mixin;

import net.lixis.outofbound.client.MeatTextureSwap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V",
					shift = At.Shift.AFTER
			)
	)
	private void outofbound$drawMeatSlotWells(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		AbstractContainerScreenAccessor access = (AbstractContainerScreenAccessor) (Object) this;
		MeatTextureSwap.drawSlotWells(graphics, access.outofbound$getLeftPos(), access.outofbound$getTopPos(),
				access.outofbound$getMenu().slots);
	}
}
