package net.lixis.outofbound;

import net.lixis9.eventjar.DarknessConfig;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class NoiseShaderClientHandler {

	private static int loadRetryCooldown = 0;

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
			return;
		}
		tryLoadNoise();
	}

	@SubscribeEvent
	public static void onScreenOpen(ScreenEvent.Opening event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc != null && mc.level == null) {
			unloadIfActive();
		}
	}

	private static void tryLoadNoise() {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			return;
		}

		if (TestShaderHandler.isActive()) {
			return;
		}

		if (mc.level == null) {
			unloadIfActive();
			return;
		}

		if (!DarknessConfig.NOISE_ENABLED) {
			unloadIfActive();
			loadRetryCooldown = 0;
			return;
		}

		if (NoiseShaderHandler.active && mc.gameRenderer.currentEffect() != null) {

			NoiseShaderHandler.applyIntensity(mc);
			return;
		}

		if (loadRetryCooldown > 0) {
			loadRetryCooldown--;
			return;
		}

		NoiseShaderHandler.ensureNoise();
		loadRetryCooldown = NoiseShaderHandler.active ? 0 : 100;
	}

	private static void unloadIfActive() {
		if (NoiseShaderHandler.active) {
			NoiseShaderHandler.unloadNoise();
		}
		loadRetryCooldown = 0;
	}
}
