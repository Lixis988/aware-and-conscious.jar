package net.lixis9.eventjar.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.lixis9.eventjar.world.inventory.ConfigMenu;
import net.lixis9.eventjar.ConfigManager;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class ConfigScreen extends AbstractContainerScreen<ConfigMenu> {
	private final static HashMap<String, Object> guistate = ConfigMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_text_distortion;
	Button button_item_renamer;

	public ConfigScreen(ConfigMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("eventjar:textures/screens/config.png");

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
	}

	@Override
	public void init() {
		super.init();
		
		// Кнопка для переключения искажения текста
		String textDistortionText = ConfigManager.isTextDistortionEnabled() ? 
			"Text Distortion: ON" : "Text Distortion: OFF";
		button_text_distortion = Button.builder(Component.literal(textDistortionText), e -> {
			ConfigManager.toggleTextDistortion();
			// Обновляем текст кнопки
			String newText = ConfigManager.isTextDistortionEnabled() ? 
				"Text Distortion: ON" : "Text Distortion: OFF";
			button_text_distortion.setMessage(Component.literal(newText));
		}).bounds(this.leftPos + 6, this.topPos + 11, 103, 20).build();
		guistate.put("button:button_text_distortion", button_text_distortion);
		this.addRenderableWidget(button_text_distortion);
		
		// Кнопка для переключения переименования предметов
		String itemRenamerText = ConfigManager.isItemRenamerEnabled() ? 
			"Item Renamer: ON" : "Item Renamer: OFF";
		button_item_renamer = Button.builder(Component.literal(itemRenamerText), e -> {
			ConfigManager.toggleItemRenamer();
			// Обновляем текст кнопки
			String newText = ConfigManager.isItemRenamerEnabled() ? 
				"Item Renamer: ON" : "Item Renamer: OFF";
			button_item_renamer.setMessage(Component.literal(newText));
		}).bounds(this.leftPos + 6, this.topPos + 36, 87, 20).build();
		guistate.put("button:button_item_renamer", button_item_renamer);
		this.addRenderableWidget(button_item_renamer);
	}
}
