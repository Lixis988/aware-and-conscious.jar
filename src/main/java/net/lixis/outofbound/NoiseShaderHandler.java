package net.lixis.outofbound;

import net.lixis9.eventjar.DarknessConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.lixis.outofbound.mixin.PostChainAccessor;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class NoiseShaderHandler {

	public static boolean active = false;

	private static final ResourceLocation NOISE_SHADER =
			new ResourceLocation(OutofboundMod.MODID, "shaders/post/noise.json");

	public static void ensureNoise() {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			active = false;
			return;
		}

		if (!DarknessConfig.NOISE_ENABLED) {
			active = false;
			return;
		}

		PostChain current = mc.gameRenderer.currentEffect();
		if (current == null) {
			mc.gameRenderer.loadEffect(NOISE_SHADER);
		}

		active = mc.gameRenderer.currentEffect() != null;
		applyIntensity(mc);
	}

	public static void applyIntensity(Minecraft mc) {
		if (mc == null) {
			return;
		}
		PostChain chain = mc.gameRenderer.currentEffect();
		if (chain == null) {
			return;
		}
		try {
			List<PostPass> passes = ((PostChainAccessor) chain).outofbound$getPasses();
			if (passes == null) {
				return;
			}
			float base = DarknessConfig.NOISE_INTENSITY;
			float max = Math.min(1.0F, base * 4.0F);
			float effective = base + (max - base) * net.lixis.outofbound.client.DarknessNoiseClientHandler.getRampStrength();
			for (PostPass pass : passes) {
				pass.getEffect().safeGetUniform("Intensity").set(effective);
			}
		} catch (Throwable ignored) {

		}
	}

	public static void loadNoise() {
		ensureNoise();
	}

	public static void unloadNoise() {
		Minecraft mc = Minecraft.getInstance();
		if (mc != null) {
			mc.gameRenderer.shutdownEffect();
		}
		active = false;
	}
}
