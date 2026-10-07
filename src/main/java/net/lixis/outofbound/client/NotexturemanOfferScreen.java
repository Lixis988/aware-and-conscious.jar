package net.lixis.outofbound.client;

import net.lixis.outofbound.NotexturemanResponsePacket;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class NotexturemanOfferScreen extends Screen {

	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_GAP = 12;

	private final int entityId;
	private final int level;
	private boolean responded;

	public NotexturemanOfferScreen(int entityId, int level) {
		super(Component.literal("Notextureman"));
		this.entityId = entityId;
		this.level = level;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int buttonY = this.height / 2 + 10;
		int totalWidth = BUTTON_WIDTH * 2 + BUTTON_GAP;
		int leftX = centerX - totalWidth / 2;

		this.addRenderableWidget(Button.builder(Component.literal("YES"), button -> respond(true))
				.bounds(leftX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());

		this.addRenderableWidget(Button.builder(Component.literal("NO"), button -> respond(false))
				.bounds(leftX + BUTTON_WIDTH + BUTTON_GAP, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());
	}

	private void respond(boolean accept) {
		if (responded) {
			return;
		}
		responded = true;
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.SERVER.noArg(), new NotexturemanResponsePacket(entityId, accept));
		this.minecraft.setScreen(null);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		String prompt = "Do you want to teleport to level " + level + "?";
		int textX = this.width / 2 - this.font.width(prompt) / 2;
		int textY = this.height / 2 - 20;
		guiGraphics.drawString(this.font, prompt, textX, textY, 0xFFFFFF, true);
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void onClose() {
		if (!responded) {
			respond(false);
			return;
		}
		super.onClose();
	}
}
