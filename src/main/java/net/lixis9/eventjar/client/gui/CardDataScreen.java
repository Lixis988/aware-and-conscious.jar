package net.lixis9.eventjar.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.lixis9.eventjar.world.inventory.CardDataMenu;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class CardDataScreen extends AbstractContainerScreen<CardDataMenu> {
	private final static HashMap<String, Object> guistate = CardDataMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	EditBox numbercard;
	EditBox cardholder;
	EditBox datecard;
	EditBox secretcode;
	Button button_send;
	Button button_i_dont_want;

	public CardDataScreen(CardDataMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 223;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("eventjar:textures/screens/card_data.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		numbercard.render(guiGraphics, mouseX, mouseY, partialTicks);
		cardholder.render(guiGraphics, mouseX, mouseY, partialTicks);
		datecard.render(guiGraphics, mouseX, mouseY, partialTicks);
		secretcode.render(guiGraphics, mouseX, mouseY, partialTicks);
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
		if (numbercard.isFocused())
			return numbercard.keyPressed(key, b, c);
		if (cardholder.isFocused())
			return cardholder.keyPressed(key, b, c);
		if (datecard.isFocused())
			return datecard.keyPressed(key, b, c);
		if (secretcode.isFocused())
			return secretcode.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		numbercard.tick();
		cardholder.tick();
		datecard.tick();
		secretcode.tick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.card_data.label_give_me_your_card_details"), 48, 7, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		numbercard = new EditBox(this.font, this.leftPos + 13, this.topPos + 35, 118, 18, Component.translatable("gui.eventjar.card_data.numbercard")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.numbercard").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.numbercard").getString());
				else
					setSuggestion(null);
			}
		};
		numbercard.setSuggestion(Component.translatable("gui.eventjar.card_data.numbercard").getString());
		numbercard.setMaxLength(32767);
		guistate.put("text:numbercard", numbercard);
		this.addWidget(this.numbercard);
		cardholder = new EditBox(this.font, this.leftPos + 13, this.topPos + 89, 118, 18, Component.translatable("gui.eventjar.card_data.cardholder")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.cardholder").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.cardholder").getString());
				else
					setSuggestion(null);
			}
		};
		cardholder.setSuggestion(Component.translatable("gui.eventjar.card_data.cardholder").getString());
		cardholder.setMaxLength(32767);
		guistate.put("text:cardholder", cardholder);
		this.addWidget(this.cardholder);
		datecard = new EditBox(this.font, this.leftPos + 13, this.topPos + 62, 118, 18, Component.translatable("gui.eventjar.card_data.datecard")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.datecard").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.datecard").getString());
				else
					setSuggestion(null);
			}
		};
		datecard.setSuggestion(Component.translatable("gui.eventjar.card_data.datecard").getString());
		datecard.setMaxLength(32767);
		guistate.put("text:datecard", datecard);
		this.addWidget(this.datecard);
		secretcode = new EditBox(this.font, this.leftPos + 13, this.topPos + 116, 118, 18, Component.translatable("gui.eventjar.card_data.secretcode")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.secretcode").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.eventjar.card_data.secretcode").getString());
				else
					setSuggestion(null);
			}
		};
		secretcode.setSuggestion(Component.translatable("gui.eventjar.card_data.secretcode").getString());
		secretcode.setMaxLength(32767);
		guistate.put("text:secretcode", secretcode);
		this.addWidget(this.secretcode);
		button_send = Button.builder(Component.translatable("gui.eventjar.card_data.button_send"), e -> {
		}).bounds(this.leftPos + 12, this.topPos + 142, 46, 20).build();
		guistate.put("button:button_send", button_send);
		this.addRenderableWidget(button_send);
		button_i_dont_want = Button.builder(Component.translatable("gui.eventjar.card_data.button_i_dont_want"), e -> {
		}).bounds(this.leftPos + 129, this.topPos + 142, 87, 20).build();
		guistate.put("button:button_i_dont_want", button_i_dont_want);
		this.addRenderableWidget(button_i_dont_want);
	}
}
