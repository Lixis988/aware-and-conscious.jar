package net.lixis.outofbound.mixin;

import net.lixis.outofbound.client.MeatTextureSwap;
import net.lixis.outofbound.feature.corruption.render.MemoryCorruptionGate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin {

	@Inject(method = "getU0", at = @At("HEAD"), cancellable = true)
	private void outofbound$meatU0(CallbackInfoReturnable<Float> cir) {
		if (remapMeat(cir, true, true)) {
			return;
		}
	}

	@Inject(method = "getU1", at = @At("HEAD"), cancellable = true)
	private void outofbound$meatU1(CallbackInfoReturnable<Float> cir) {
		remapMeat(cir, false, true);
	}

	@Inject(method = "getV0", at = @At("HEAD"), cancellable = true)
	private void outofbound$meatV0(CallbackInfoReturnable<Float> cir) {
		remapMeat(cir, true, false);
	}

	@Inject(method = "getV1", at = @At("HEAD"), cancellable = true)
	private void outofbound$meatV1(CallbackInfoReturnable<Float> cir) {
		remapMeat(cir, false, false);
	}

	@Inject(method = "getU0", at = @At("RETURN"), cancellable = true)
	private void outofbound$corruptU0(CallbackInfoReturnable<Float> cir) {
		applyAtlasCorruption(cir);
	}

	@Inject(method = "getU1", at = @At("RETURN"), cancellable = true)
	private void outofbound$corruptU1(CallbackInfoReturnable<Float> cir) {
		applyAtlasCorruption(cir);
	}

	@Inject(method = "getV0", at = @At("RETURN"), cancellable = true)
	private void outofbound$corruptV0(CallbackInfoReturnable<Float> cir) {
		applyAtlasCorruption(cir);
	}

	@Inject(method = "getV1", at = @At("RETURN"), cancellable = true)
	private void outofbound$corruptV1(CallbackInfoReturnable<Float> cir) {
		applyAtlasCorruption(cir);
	}

	private boolean remapMeat(CallbackInfoReturnable<Float> cir, boolean zero, boolean uAxis) {
		TextureAtlasSprite self = (TextureAtlasSprite) (Object) this;
		if (!MeatTextureSwap.shouldSwapSprite(self)) {
			return false;
		}
		TextureAtlasSprite meat = MeatTextureSwap.meatSpriteFor(self);
		if (meat == null || meat == self) {
			return false;
		}
		MeatTextureSwap.runWithoutSwap(() -> {
			float value = uAxis ? (zero ? meat.getU0() : meat.getU1()) : (zero ? meat.getV0() : meat.getV1());
			cir.setReturnValue(value);
		});
		return true;
	}

	private void applyAtlasCorruption(CallbackInfoReturnable<Float> cir) {
		if (cir.isCancelled()) {
			return;
		}
		if (!MemoryCorruptionGate.shouldCorruptAtlas() || !isWorldAtlas()) {
			return;
		}
		cir.setReturnValue(MemoryCorruptionGate.corruptAtlasCoord(cir.getReturnValueF()));
	}

	private boolean isWorldAtlas() {
		ResourceLocation atlas = ((TextureAtlasSprite) (Object) this).atlasLocation();
		if (atlas == null) {
			return false;
		}
		String path = atlas.getPath();
		return path.contains("blocks") || path.contains("entities") || path.contains("particles") || path.contains("items");
	}
}
