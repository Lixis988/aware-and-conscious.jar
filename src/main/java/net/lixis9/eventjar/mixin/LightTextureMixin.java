package net.lixis9.eventjar.mixin;

import net.lixis9.eventjar.DarknessConfig;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTexture.class)
public class LightTextureMixin {

	@Inject(method = "getBrightness", at = @At("RETURN"), cancellable = true)
	private static void eventjar$darkenRamp(DimensionType dimensionType, int lightLevel, CallbackInfoReturnable<Float> cir) {
		if (!DarknessConfig.ENABLED) {
			return;
		}
		float vanilla = cir.getReturnValueF();
		float t = lightLevel / 15.0F;
		float exponent = 1.0F + 5.0F * DarknessConfig.STRENGTH;
		float factor = (float) Math.pow(t, exponent);
		cir.setReturnValue(vanilla * factor);
	}

	@Inject(method = "notGamma", at = @At("RETURN"), cancellable = true)
	private void eventjar$flattenGamma(float value, CallbackInfoReturnable<Float> cir) {
		if (!DarknessConfig.ENABLED) {
			return;
		}
		float vanilla = cir.getReturnValueF();
		cir.setReturnValue(vanilla + (value - vanilla) * DarknessConfig.STRENGTH);
	}
}
