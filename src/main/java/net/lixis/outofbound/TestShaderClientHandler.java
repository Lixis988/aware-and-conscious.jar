package net.lixis.outofbound;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TestShaderClientHandler {

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
			return;
		}
		if (!TestShaderHandler.isActive()) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc == null || mc.level == null) {
			TestShaderHandler.unload();
			return;
		}
		TestShaderHandler.ensureLoaded();
	}

	@SubscribeEvent
	public static void onScreenOpen(ScreenEvent.Opening event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc != null && mc.level == null) {
			TestShaderHandler.unload();
		}
	}
}
