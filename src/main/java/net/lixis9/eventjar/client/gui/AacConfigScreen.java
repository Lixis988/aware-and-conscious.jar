package net.lixis9.eventjar.client.gui;

import net.lixis.outofbound.RandomLabelClientHandler;
import net.lixis9.eventjar.AacConfig;
import net.lixis9.eventjar.ConfigManager;
import net.lixis9.eventjar.DarknessConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AacConfigScreen extends Screen {

	private static final Component TITLE = Component.literal("AAC config");
	private static final int BUTTON_WIDTH = 240;
	private static final int BUTTON_HEIGHT = 18;
	private static final int BUTTON_SPACING = 20;

	private final Screen returnScreen;
	private int scroll;

	public AacConfigScreen() {
		super(TITLE);
		this.returnScreen = Minecraft.getInstance().screen;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int y = 28 - scroll;
		int left = centerX - BUTTON_WIDTH / 2;

		addToggle(left, y, safeModeText(), b -> {
			AacConfig.setSafeMode(!AacConfig.SAFE_MODE);
			b.setMessage(Component.literal(safeModeText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, textDistortionText(), b -> {
			boolean next = !AacConfig.TEXT_DISTORTION;
			ConfigManager.setTextDistortion(next);
			DarknessConfig.setRandomLabels(next);
			refreshUnderlyingScreen();
			b.setMessage(Component.literal(textDistortionText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, itemRenamerText(), b -> {
			ConfigManager.toggleItemRenamer();
			b.setMessage(Component.literal(itemRenamerText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, signsText(), b -> {
			AacConfig.setEnableSigns(!AacConfig.ENABLE_SIGNS);
			b.setMessage(Component.literal(signsText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, structuresText(), b -> {
			AacConfig.setEnableStructures(!AacConfig.ENABLE_STRUCTURES);
			b.setMessage(Component.literal(structuresText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, bloodRainText(), b -> {
			AacConfig.setEnableBloodRain(!AacConfig.ENABLE_BLOOD_RAIN);
			b.setMessage(Component.literal(bloodRainText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, spawnEntityText(), b -> {
			AacConfig.setEntitySpawnMultiplier(cycleMult(AacConfig.ENTITY_SPAWN_MULTIPLIER));
			b.setMessage(Component.literal(spawnEntityText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, spawnEventText(), b -> {
			AacConfig.setEventSpawnMultiplier(cycleMult(AacConfig.EVENT_SPAWN_MULTIPLIER));
			b.setMessage(Component.literal(spawnEventText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, spawnStructureText(), b -> {
			AacConfig.setStructureSpawnMultiplier(cycleMult(AacConfig.STRUCTURE_SPAWN_MULTIPLIER));
			b.setMessage(Component.literal(spawnStructureText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, darknessText(), b -> {
			DarknessConfig.setDarknessEnabled(!DarknessConfig.ENABLED);
			b.setMessage(Component.literal(darknessText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, noiseText(), b -> {
			DarknessConfig.setNoiseEnabled(!DarknessConfig.NOISE_ENABLED);
			b.setMessage(Component.literal(noiseText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, damageCorruptionText(), b -> {
			DarknessConfig.setDamageCorruptionEnabled(!DarknessConfig.ENABLE_DAMAGE_CORRUPTION);
			b.setMessage(Component.literal(damageCorruptionText()));
		});
		y += BUTTON_SPACING;

		addToggle(left, y, memoryCorruptionText(), b -> {
			DarknessConfig.setMemoryCorruptionEnabled(!DarknessConfig.ENABLE_MEMORY_CORRUPTION);
			b.setMessage(Component.literal(memoryCorruptionText()));
		});
		y += BUTTON_SPACING + 4;

		this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
				.bounds(left, y, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());
	}

	private void addToggle(int x, int y, String label, Button.OnPress press) {
		this.addRenderableWidget(Button.builder(Component.literal(label), press)
				.bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());
	}

	private void refreshUnderlyingScreen() {
		if (returnScreen == null || returnScreen instanceof AacConfigScreen) {
			return;
		}
		RandomLabelClientHandler.refreshCurrentScreen(this.minecraft);
	}

	private static double cycleMult(double current) {
		if (current <= 0.01D) {
			return 0.5D;
		}
		if (current < 0.75D) {
			return 1.0D;
		}
		if (current < 1.25D) {
			return 1.5D;
		}
		if (current < 1.75D) {
			return 2.0D;
		}
		return 0.0D;
	}

	private static String safeModeText() {
		return "Safe Mode (no OS/desktop): " + onOff(AacConfig.SAFE_MODE);
	}

	private static String textDistortionText() {
		return "Jumbled menu text: " + onOff(AacConfig.TEXT_DISTORTION);
	}

	private static String itemRenamerText() {
		return "Unstackable/rename items: " + onOff(AacConfig.ITEM_RENAMER);
	}

	private static String signsText() {
		return "Horror signs/worldgen tables: " + onOff(AacConfig.ENABLE_SIGNS);
	}

	private static String structuresText() {
		return "Horror structures: " + onOff(AacConfig.ENABLE_STRUCTURES);
	}

	private static String bloodRainText() {
		return "Blood rain event: " + onOff(AacConfig.ENABLE_BLOOD_RAIN);
	}

	private static String spawnEntityText() {
		return "Entity spawn rate: " + fmt(AacConfig.ENTITY_SPAWN_MULTIPLIER) + "x";
	}

	private static String spawnEventText() {
		return "Event rate: " + fmt(AacConfig.EVENT_SPAWN_MULTIPLIER) + "x";
	}

	private static String spawnStructureText() {
		return "Structure rate: " + fmt(AacConfig.STRUCTURE_SPAWN_MULTIPLIER) + "x";
	}

	private static String darknessText() {
		return "Darkness: " + onOff(DarknessConfig.ENABLED);
	}

	private static String noiseText() {
		return "Screen noise: " + onOff(DarknessConfig.NOISE_ENABLED);
	}

	private static String damageCorruptionText() {
		return "Damage corruption: " + onOff(DarknessConfig.ENABLE_DAMAGE_CORRUPTION);
	}

	private static String memoryCorruptionText() {
		return "Memory corruption: " + onOff(DarknessConfig.ENABLE_MEMORY_CORRUPTION);
	}

	private static String onOff(boolean v) {
		return v ? "ON" : "OFF";
	}

	private static String fmt(double v) {
		return String.format(java.util.Locale.ROOT, "%.1f", v);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
		graphics.drawCenteredString(this.font, "§7/aac_config · Event.jar + outofbound", this.width / 2, 20, 0xAAAAAA);
		super.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		int next = (int) Math.max(0, Math.min(160, scroll - delta * 12));
		if (next != scroll) {
			scroll = next;
			this.clearWidgets();
			this.init();
		}
		return true;
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.returnScreen instanceof AacConfigScreen ? null : this.returnScreen);
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}
}
