package net.lixis.outofbound.mixin;

import net.lixis.outofbound.feature.corruption.render.MemoryCorruptionGate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin {

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

	private void applyAtlasCorruption(CallbackInfoReturnable<Float> cir) {
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
