package net.lixis9.eventjar.client;

import net.lixis9.eventjar.EventjarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EventjarMod.MODID, value = Dist.CLIENT)
public final class VillagerMimicCrashClient {

	private static boolean active;
	private static int ticks;
	private static final Random RANDOM = new Random();

	private VillagerMimicCrashClient() {
	}

	public static void begin() {
		active = true;
		ticks = 0;
		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new ScrambleScreen());
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !active) {
			return;
		}
		ticks++;
		if (ticks >= 45) {
			active = false;
			try {
				Runtime.getRuntime().halt(1);
			} catch (Throwable ignored) {
				System.exit(1);
			}
		}
	}

	@SubscribeEvent
	public static void onOverlay(RenderGuiOverlayEvent.Post event) {
		if (!active) {
			return;
		}
		GuiGraphics g = event.getGuiGraphics();
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		scramble(g, w, h, 80);
	}

	private static void scramble(GuiGraphics g, int w, int h, int strips) {
		for (int i = 0; i < strips; i++) {
			int x = RANDOM.nextInt(Math.max(w, 1));
			int y = RANDOM.nextInt(Math.max(h, 1));
			int bw = 8 + RANDOM.nextInt(Math.max(40, 1));
			int bh = 2 + RANDOM.nextInt(24);
			int color;
			if (RANDOM.nextFloat() < 0.35F) {
				color = 0xFF000000;
			} else if (RANDOM.nextFloat() < 0.5F) {
				color = 0xFF101010 | (RANDOM.nextInt(40) << 8);
			} else {
				color = 0xFF000000 | RANDOM.nextInt(0xFFFFFF);
			}
			g.fill(x, y, Math.min(w, x + bw), Math.min(h, y + bh), color);
		}

		for (int i = 0; i < 12; i++) {
			int y = RANDOM.nextInt(Math.max(h, 1));
			int shift = RANDOM.nextInt(30) - 15;
			g.fill(Math.max(0, shift), y, w, Math.min(h, y + 1 + RANDOM.nextInt(3)), 0xEE000000 | RANDOM.nextInt(0x303030));
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static final class ScrambleScreen extends Screen {
		private ScrambleScreen() {
			super(Component.literal(""));
		}

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			scramble(guiGraphics, this.width, this.height, 140);
		}

		@Override
		public boolean isPauseScreen() {
			return false;
		}

		@Override
		public boolean shouldCloseOnEsc() {
			return false;
		}
	}
}
