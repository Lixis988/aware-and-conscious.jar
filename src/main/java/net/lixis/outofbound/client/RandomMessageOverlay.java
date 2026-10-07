package net.lixis.outofbound.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.RandomMessagePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class RandomMessageOverlay {

	public static final int MESSAGE_COUNT = RandomMessagePacket.MESSAGE_COUNT;
	private static final int TEXTURE_SIZE = 500;

	private static final float SCREEN_HEIGHT_FRACTION = 0.55F;
	private static final int FADE_TICKS = 3;

	private static final ResourceLocation[] TEXTURES = new ResourceLocation[MESSAGE_COUNT];

	static {
		for (int i = 0; i < MESSAGE_COUNT; i++) {
			TEXTURES[i] = new ResourceLocation(OutofboundMod.MODID, "textures/titles/random/rnd_message_" + (i + 1) + ".png");
		}
	}

	private static int messageIndex = -1;
	private static int ticksRemaining;
	private static int totalTicks;

	private RandomMessageOverlay() {
	}

	public static void trigger(int index, int durationTicks) {
		messageIndex = Math.floorMod(index, MESSAGE_COUNT);
		totalTicks = Math.max(1, durationTicks);
		ticksRemaining = totalTicks;
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null || minecraft.player == null) {
			ticksRemaining = 0;
			return;
		}
		if (minecraft.isPaused()) {
			return;
		}
		if (ticksRemaining > 0) {
			ticksRemaining--;
		}
	}

	@SubscribeEvent
	public static void onRenderGui(RenderGuiEvent.Post event) {
		if (ticksRemaining <= 0 || messageIndex < 0) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null || minecraft.options.hideGui) {
			return;
		}

		float remaining = ticksRemaining - event.getPartialTick();
		float alpha = computeAlpha(remaining);
		if (alpha <= 0.0F) {
			return;
		}

		int screenWidth = event.getWindow().getGuiScaledWidth();
		int screenHeight = event.getWindow().getGuiScaledHeight();
		int size = Math.max(1, Math.round(screenHeight * SCREEN_HEIGHT_FRACTION));
		int x = (screenWidth - size) / 2;
		int y = (screenHeight - size) / 2;

		GuiGraphics graphics = event.getGuiGraphics();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
		graphics.blit(TEXTURES[messageIndex], x, y, size, size, 0.0F, 0.0F, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE,
				TEXTURE_SIZE);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
	}

	private static float computeAlpha(float remaining) {
		if (remaining <= 0.0F) {
			return 0.0F;
		}
		float elapsed = totalTicks - remaining;
		float fadeIn = Math.min(1.0F, elapsed / FADE_TICKS);
		float fadeOut = Math.min(1.0F, remaining / FADE_TICKS);
		return Math.max(0.0F, Math.min(fadeIn, fadeOut));
	}
}
