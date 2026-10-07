package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.network.EventjarModVariables;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class IfPlayerStayAFKinGUIProcedure {
	private static final int AFK_TICKS = 20 * 45;
	private static int idleTicks;
	private static double lastMouseX = Double.NaN;
	private static double lastMouseY = Double.NaN;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.screen;
		if (screen == null || screen instanceof PauseScreen || screen instanceof ChatScreen) {
			idleTicks = 0;
			lastMouseX = Double.NaN;
			lastMouseY = Double.NaN;
			return;
		}

		double mx = mc.mouseHandler.xpos();
		double my = mc.mouseHandler.ypos();
		boolean moved = !Double.isNaN(lastMouseX)
				&& (Math.abs(mx - lastMouseX) > 0.5 || Math.abs(my - lastMouseY) > 0.5);
		lastMouseX = mx;
		lastMouseY = my;

		if (moved || mc.options.keyUp.isDown() || mc.options.keyDown.isDown()
				|| mc.options.keyLeft.isDown() || mc.options.keyRight.isDown()
				|| mc.options.keyJump.isDown() || mc.options.keyShift.isDown()) {
			idleTicks = 0;
			return;
		}

		idleTicks++;
		if (idleTicks >= AFK_TICKS) {
			EventjarModVariables.guibool = true;
			idleTicks = 0;
		}
	}
}
