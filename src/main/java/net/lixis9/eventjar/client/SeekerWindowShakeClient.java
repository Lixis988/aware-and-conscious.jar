package net.lixis9.eventjar.client;

import net.lixis9.eventjar.EventjarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.nio.IntBuffer;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID, value = Dist.CLIENT)
public final class SeekerWindowShakeClient {

	private static final int SETUP_TICKS = 3;

	private static boolean wantActive;
	private static boolean applied;
	private static int setupAge;

	private static int shakeBaseX;
	private static int shakeBaseY;
	private static int halfW;
	private static int halfH;

	private static int origX;
	private static int origY;
	private static int origW;
	private static int origH;
	private static boolean wasFullscreen;

	private SeekerWindowShakeClient() {
	}

	public static void setActive(boolean active) {
		Minecraft.getInstance().execute(() -> {
			if (active) {
				if (!wantActive) {
					wantActive = true;
					applied = false;
					setupAge = 0;
				}
			} else if (wantActive) {
				wantActive = false;
				restore();
				applied = false;
				setupAge = 0;
			}
		});
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !wantActive) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.getWindow() == null) {
			return;
		}
		long handle = mc.getWindow().getWindow();
		if (handle == 0L) {
			return;
		}

		if (!applied) {
			setupAge++;
			if (setupAge == 1) {
				captureOriginal(mc, handle);
				exitFullscreen(mc, handle);
			}
			if (setupAge >= SETUP_TICKS) {
				applyHalfSize(mc, handle);
				applied = true;
			}
			return;
		}

		setupAge++;
		float t = setupAge;
		int ox = Mth.floor(Mth.sin(t * 2.7F) * 22.0F + Mth.cos(t * 4.1F) * 14.0F);
		int oy = Mth.floor(Mth.cos(t * 3.3F) * 18.0F + Mth.sin(t * 5.2F) * 12.0F);

		mc.getWindow().setWindowed(halfW, halfH);
		GLFW.glfwSetWindowSize(handle, halfW, halfH);
		GLFW.glfwSetWindowPos(handle, shakeBaseX + ox, shakeBaseY + oy);
	}

	private static void captureOriginal(Minecraft mc, long handle) {
		IntBuffer xb = BufferUtils.createIntBuffer(1);
		IntBuffer yb = BufferUtils.createIntBuffer(1);
		IntBuffer wb = BufferUtils.createIntBuffer(1);
		IntBuffer hb = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowPos(handle, xb, yb);
		GLFW.glfwGetWindowSize(handle, wb, hb);
		origX = xb.get(0);
		origY = yb.get(0);
		origW = Math.max(640, wb.get(0));
		origH = Math.max(360, hb.get(0));
		wasFullscreen = mc.getWindow().isFullscreen()
				|| Boolean.TRUE.equals(mc.options.fullscreen().get())
				|| GLFW.glfwGetWindowMonitor(handle) != 0L;
	}

	private static void exitFullscreen(Minecraft mc, long handle) {
		Options options = mc.options;
		if (Boolean.TRUE.equals(options.fullscreen().get())) {
			options.fullscreen().set(false);
			options.save();
		}
		if (mc.getWindow().isFullscreen()) {
			mc.getWindow().toggleFullScreen();
		}
		GLFW.glfwRestoreWindow(handle);
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		if (GLFW.glfwGetWindowMonitor(handle) != 0L) {
			long monitor = GLFW.glfwGetPrimaryMonitor();
			GLFWVidMode mode = monitor != 0L ? GLFW.glfwGetVideoMode(monitor) : null;
			int w = mode != null ? mode.width() / 2 : 960;
			int h = mode != null ? mode.height() / 2 : 540;
			GLFW.glfwSetWindowMonitor(handle, 0L, 100, 100, w, h, GLFW.GLFW_DONT_CARE);
		}
	}

	private static void applyHalfSize(Minecraft mc, long handle) {
		int screenW = 1920;
		int screenH = 1080;
		long monitor = GLFW.glfwGetPrimaryMonitor();
		if (monitor != 0L) {
			GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
			if (mode != null) {
				screenW = mode.width();
				screenH = mode.height();
			}
		}
		halfW = Math.max(480, screenW / 2);
		halfH = Math.max(270, screenH / 2);
		shakeBaseX = Math.max(0, (screenW - halfW) / 2);
		shakeBaseY = Math.max(0, (screenH - halfH) / 2);

		GLFW.glfwRestoreWindow(handle);
		mc.getWindow().setWindowed(halfW, halfH);
		GLFW.glfwSetWindowSize(handle, halfW, halfH);
		GLFW.glfwSetWindowPos(handle, shakeBaseX, shakeBaseY);
	}

	private static void restore() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.getWindow() == null) {
			return;
		}
		long handle = mc.getWindow().getWindow();
		if (handle == 0L) {
			return;
		}
		try {
			GLFW.glfwRestoreWindow(handle);
			mc.getWindow().setWindowed(origW, origH);
			GLFW.glfwSetWindowSize(handle, origW, origH);
			GLFW.glfwSetWindowPos(handle, origX, origY);
			if (wasFullscreen && !mc.getWindow().isFullscreen()) {
				mc.options.fullscreen().set(true);
				mc.getWindow().toggleFullScreen();
			}
		} catch (Throwable ignored) {
		}
		wasFullscreen = false;
	}
}
