package net.lixis9.eventjar.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.lixis9.eventjar.world.inventory.Test19Menu;
import net.lixis9.eventjar.network.Test19ButtonMessage;
import net.lixis9.eventjar.EventjarMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class Test19Screen extends AbstractContainerScreen<Test19Menu> {
	private final static HashMap<String, Object> guistate = Test19Menu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_yes;
	Button button_no;

	public Test19Screen(Test19Menu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("eventjar:textures/screens/test_19.png");

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
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.test_19.label_i_think_other_people"), 6, 16, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.test_19.label_shouldnt_know_too_much_about_me"), 6, 34, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		button_yes = Button.builder(Component.translatable("gui.eventjar.test_19.button_yes"), e -> {
			if (true) {
				EventjarMod.PACKET_HANDLER.sendToServer(new Test19ButtonMessage(0, x, y, z));
				Test19ButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 6, this.topPos + 133, 40, 20).build();
		guistate.put("button:button_yes", button_yes);
		this.addRenderableWidget(button_yes);
		button_no = Button.builder(Component.translatable("gui.eventjar.test_19.button_no"), e -> {
			if (true) {
				EventjarMod.PACKET_HANDLER.sendToServer(new Test19ButtonMessage(1, x, y, z));
				Test19ButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 132, this.topPos + 133, 35, 20).build();
		guistate.put("button:button_no", button_no);
		this.addRenderableWidget(button_no);
	}
}
