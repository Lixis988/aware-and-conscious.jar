package net.lixis.outofbound;

import net.lixis9.eventjar.DarknessConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OutofboundConfigScreen extends Screen {

	private static final Component TITLE = Component.literal("outofbound config");

	private final Screen returnScreen;

	public OutofboundConfigScreen() {
		super(TITLE);
		this.returnScreen = Minecraft.getInstance().screen;
	}

	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_SPACING = 24;
	private static final int TITLE_GAP = 16;

	private static int buttonStartY(int screenHeight) {
		return screenHeight / 2 - 60;
	}

	private static int titleY(int screenHeight, int fontLineHeight) {
		return buttonStartY(screenHeight) - TITLE_GAP - fontLineHeight;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int buttonY = buttonStartY(this.height);

		this.addRenderableWidget(Button.builder(
						Component.literal(randomLabelsButtonText()),
						button -> {
							DarknessConfig.setRandomLabels(!DarknessConfig.RANDOM_LABELS);
							refreshUnderlyingScreen();
							button.setMessage(Component.literal(randomLabelsButtonText()));
						})
				.bounds(centerX - 100, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());

		this.addRenderableWidget(Button.builder(
						Component.literal(darknessButtonText()),
						button -> {
							DarknessConfig.setDarknessEnabled(!DarknessConfig.ENABLED);
							button.setMessage(Component.literal(darknessButtonText()));
						})
				.bounds(centerX - 100, buttonY + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());

		this.addRenderableWidget(Button.builder(
						Component.literal(noiseButtonText()),
						button -> {
							DarknessConfig.setNoiseEnabled(!DarknessConfig.NOISE_ENABLED);
							button.setMessage(Component.literal(noiseButtonText()));
						})
				.bounds(centerX - 100, buttonY + BUTTON_SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());

		this.addRenderableWidget(Button.builder(
						Component.literal(damageCorruptionButtonText()),
						button -> {
							DarknessConfig.setDamageCorruptionEnabled(!DarknessConfig.ENABLE_DAMAGE_CORRUPTION);
							button.setMessage(Component.literal(damageCorruptionButtonText()));
						})
				.bounds(centerX - 100, buttonY + BUTTON_SPACING * 3, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());

		this.addRenderableWidget(Button.builder(
						Component.literal(memoryCorruptionButtonText()),
						button -> {
							DarknessConfig.setMemoryCorruptionEnabled(!DarknessConfig.ENABLE_MEMORY_CORRUPTION);
							button.setMessage(Component.literal(memoryCorruptionButtonText()));
						})
				.bounds(centerX - 100, buttonY + BUTTON_SPACING * 4, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());

		this.addRenderableWidget(Button.builder(
						Component.literal("Close"),
						button -> this.onClose())
				.bounds(centerX - 100, buttonY + BUTTON_SPACING * 5, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());
	}

	private void refreshUnderlyingScreen() {
		if (returnScreen == null || returnScreen instanceof OutofboundConfigScreen) {
			return;
		}
		if (DarknessConfig.RANDOM_LABELS) {
			RandomLabelClientHandler.randomizeWidgetLabels(returnScreen);
		} else {
			returnScreen.init(this.minecraft, returnScreen.width, returnScreen.height);
		}
	}

	private static String randomLabelsButtonText() {
		return "Random symbols: " + (DarknessConfig.RANDOM_LABELS ? "ON" : "OFF");
	}

	private static String darknessButtonText() {
		return "Darkness: " + (DarknessConfig.ENABLED ? "ON" : "OFF");
	}

	private static String noiseButtonText() {
		return "Screen noise: " + (DarknessConfig.NOISE_ENABLED ? "ON" : "OFF");
	}

	private static String damageCorruptionButtonText() {
		return "Damage corruption: " + (DarknessConfig.ENABLE_DAMAGE_CORRUPTION ? "ON" : "OFF");
	}

	private static String memoryCorruptionButtonText() {
		return "Memory corruption: " + (DarknessConfig.ENABLE_MEMORY_CORRUPTION ? "ON" : "OFF");
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, titleY(this.height, this.font.lineHeight), 0xFFFFFF);
		super.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.returnScreen);
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}
}
