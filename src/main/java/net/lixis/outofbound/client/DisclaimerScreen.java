package net.lixis.outofbound.client;

import net.lixis9.eventjar.client.TitleScreenBackground;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class DisclaimerScreen extends Screen {

	private static final Component TITLE = Component.literal("WARNING");
	private static final String BODY =
			"WARNING: This material contains footage or descriptions that may be disturbing or distressing. "
					+ "It is not recommended for viewing by minors, pregnant women, or individuals with a sensitive constitution. "
					+ "View at your own risk.";

	public DisclaimerScreen() {
		super(TITLE);
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int buttonY = this.height - 48;
		int gap = 8;
		int buttonWidth = 100;

		this.addRenderableWidget(Button.builder(Component.literal("Quit"), button -> {
			Minecraft.getInstance().stop();
		}).bounds(centerX - buttonWidth - gap / 2, buttonY, buttonWidth, 20).build());

		this.addRenderableWidget(Button.builder(Component.literal("Play"), button -> {
			DisclaimerClientHandler.markAccepted();
			Minecraft.getInstance().setScreen(new TitleScreen());
		}).bounds(centerX + gap / 2, buttonY, buttonWidth, 20).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		TitleScreenBackground.render(graphics, this.width, this.height, false);
		graphics.fill(0, 0, this.width, this.height, 0x99000000);

		graphics.drawCenteredString(this.font, TITLE, this.width / 2, 40, 0xFFFF4444);

		int maxWidth = Math.min(420, this.width - 48);
		List<FormattedCharSequence> lines = this.font.split(Component.literal(BODY), maxWidth);
		int textY = this.height / 2 - (lines.size() * this.font.lineHeight) / 2;
		for (FormattedCharSequence line : lines) {
			graphics.drawCenteredString(this.font, line, this.width / 2, textY, 0xFFFFFFFF);
			textY += this.font.lineHeight + 2;
		}

		super.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
