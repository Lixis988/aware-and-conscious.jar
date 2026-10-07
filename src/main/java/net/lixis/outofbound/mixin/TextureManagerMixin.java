package net.lixis.outofbound.mixin;

import net.lixis.outofbound.client.MeatTextureSwap;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

	@Inject(
			method = "getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void outofbound$meatTexture(ResourceLocation path, CallbackInfoReturnable<AbstractTexture> cir) {
		if (!MeatTextureSwap.shouldSwapTexture(path)) {
			return;
		}
		ResourceLocation meat = MeatTextureSwap.meatTextureFor(path);
		TextureManager self = (TextureManager) (Object) this;
		MeatTextureSwap.runWithoutSwap(() -> cir.setReturnValue(self.getTexture(meat)));
	}
}
