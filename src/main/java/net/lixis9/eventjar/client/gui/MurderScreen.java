package net.lixis9.eventjar.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.lixis9.eventjar.world.inventory.MurderMenu;
import net.lixis9.eventjar.network.MurderButtonMessage;
import net.lixis9.eventjar.EventjarMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class MurderScreen extends AbstractContainerScreen<MurderMenu> {
	private final static HashMap<String, Object> guistate = MurderMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_kill;
	Button button_help;
	Button button_torment;

	public MurderScreen(MurderMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 112;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("eventjar:textures/screens/murder.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		guiGraphics.blit(new ResourceLocation("eventjar:textures/screens/images.png"), this.leftPos + -101, this.topPos + -5, 0, 0, 332, 152, 332, 152);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.murder.label_a_tormented_entity_asks_for_help"), -25, -3, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.murder.label_what_will_you_do"), 14, 14, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		button_kill = Button.builder(Component.translatable("gui.eventjar.murder.button_kill"), e -> {
			if (true) {
				EventjarMod.PACKET_HANDLER.sendToServer(new MurderButtonMessage(0, x, y, z));
				MurderButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + -156, this.topPos + 179, 51, 20).build();
		guistate.put("button:button_kill", button_kill);
		this.addRenderableWidget(button_kill);
		button_help = Button.builder(Component.translatable("gui.eventjar.murder.button_help"), e -> {
			if (true) {
				EventjarMod.PACKET_HANDLER.sendToServer(new MurderButtonMessage(1, x, y, z));
				MurderButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 39, this.topPos + 124, 46, 20).build();
		guistate.put("button:button_help", button_help);
		this.addRenderableWidget(button_help);
		button_torment = Button.builder(Component.translatable("gui.eventjar.murder.button_torment"), e -> {
			if (true) {
				EventjarMod.PACKET_HANDLER.sendToServer(new MurderButtonMessage(2, x, y, z));
				MurderButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		}).bounds(this.leftPos + 207, this.topPos + 182, 61, 20).build();
		guistate.put("button:button_torment", button_torment);
		this.addRenderableWidget(button_torment);
	}
}
