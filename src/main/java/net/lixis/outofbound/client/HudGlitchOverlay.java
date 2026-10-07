package net.lixis.outofbound.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class HudGlitchOverlay {

	private static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");

	private static final int NORMAL_TICKS = 10;
	private static final int BROKEN_TICKS = 2;
	private static final int CYCLE_TICKS = NORMAL_TICKS + BROKEN_TICKS;

	private static final int SLOTS = 10;
	private static final int ICON_SIZE = 9;

	private HudGlitchOverlay() {
	}

	@SubscribeEvent
	public static void onRenderOverlayPre(RenderGuiOverlayEvent.Pre event) {
		if (!WorldProgressClientState.hasBoundedcowCollision()) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.options.hideGui) {
			return;
		}

		int tick = minecraft.player.tickCount;
		if (!isBroken(tick)) {
			return;
		}

		ResourceLocation id = event.getOverlay().id();
		GuiGraphics graphics = event.getGuiGraphics();
		int width = event.getWindow().getGuiScaledWidth();
		int height = event.getWindow().getGuiScaledHeight();
		Random random = seededRandom(tick);

		if (id.equals(VanillaGuiOverlay.PLAYER_HEALTH.id())) {
			if (!minecraft.gameMode.canHurtPlayer()) {
				return;
			}
			event.setCanceled(true);
			drawGlitchedHearts(graphics, width, height, random);
		} else if (id.equals(VanillaGuiOverlay.FOOD_LEVEL.id())) {
			if (!minecraft.gameMode.canHurtPlayer()) {
				return;
			}
			event.setCanceled(true);
			drawGlitchedFood(graphics, width, height, random);
		} else if (id.equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
			if (!minecraft.gameMode.hasExperience()) {
				return;
			}
			event.setCanceled(true);
			drawGlitchedExperience(graphics, width, height, minecraft.font, random);
		}
	}

	private static boolean isBroken(int tick) {
		return Math.floorMod(tick, CYCLE_TICKS) >= NORMAL_TICKS;
	}

	private static Random seededRandom(int tick) {
		return new Random(Math.floorDiv(tick, CYCLE_TICKS) * 2654435761L);
	}

	private static void drawGlitchedHearts(GuiGraphics graphics, int width, int height, Random random) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

		int left = width / 2 - 91;
		int baseY = height - 39;
		for (int slot = 0; slot < SLOTS; slot++) {
			int x = left + slot * 8 + random.nextInt(5) - 2;
			int y = baseY + random.nextInt(5) - 2;
			graphics.blit(ICONS, x, y, 16, 0, ICON_SIZE, ICON_SIZE);
			int state = random.nextInt(3);
			if (state == 2) {
				graphics.blit(ICONS, x, y, 52, 0, ICON_SIZE, ICON_SIZE);
			} else if (state == 1) {
				graphics.blit(ICONS, x, y, 61, 0, ICON_SIZE, ICON_SIZE);
			}
		}
	}

	private static void drawGlitchedFood(GuiGraphics graphics, int width, int height, Random random) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

		int rightAnchor = width / 2 + 91;
		int baseY = height - 39;
		for (int slot = 0; slot < SLOTS; slot++) {
			int x = rightAnchor - slot * 8 - 9 + random.nextInt(5) - 2;
			int y = baseY + random.nextInt(5) - 2;
			graphics.blit(ICONS, x, y, 16, 27, ICON_SIZE, ICON_SIZE);
			int state = random.nextInt(3);
			if (state == 2) {
				graphics.blit(ICONS, x, y, 52, 27, ICON_SIZE, ICON_SIZE);
			} else if (state == 1) {
				graphics.blit(ICONS, x, y, 61, 27, ICON_SIZE, ICON_SIZE);
			}
		}
	}

	private static void drawGlitchedExperience(GuiGraphics graphics, int width, int height, Font font, Random random) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

		int barX = width / 2 - 91;
		int barY = height - 32 + 3;
		graphics.blit(ICONS, barX, barY, 0, 64, 182, 5);
		int fill = random.nextInt(183);
		if (fill > 0) {
			graphics.blit(ICONS, barX, barY, 0, 69, fill, 5);
		}

		String label = Integer.toString(random.nextInt(10000));
		int textX = (width - font.width(label)) / 2;
		int textY = height - 31 - 4;
		graphics.drawString(font, label, textX + 1, textY, 0, false);
		graphics.drawString(font, label, textX - 1, textY, 0, false);
		graphics.drawString(font, label, textX, textY + 1, 0, false);
		graphics.drawString(font, label, textX, textY - 1, 0, false);
		graphics.drawString(font, label, textX, textY, 8453920, false);
	}
}
