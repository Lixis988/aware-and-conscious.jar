package net.lixis9.eventjar.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.lixis9.eventjar.world.inventory.AreyouwillingtoshareyourpersonalnformationwithusMenu;
import net.lixis9.eventjar.network.AreyouwillingtoshareyourpersonalnformationwithusButtonMessage;
import net.lixis9.eventjar.EventjarMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class AreyouwillingtoshareyourpersonalnformationwithusScreen extends AbstractContainerScreen<AreyouwillingtoshareyourpersonalnformationwithusMenu> {
	private final static HashMap<String, Object> guistate = AreyouwillingtoshareyourpersonalnformationwithusMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_kill_yourself;

	public AreyouwillingtoshareyourpersonalnformationwithusScreen(AreyouwillingtoshareyourpersonalnformationwithusMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("eventjar:textures/screens/areyouwillingtoshareyourpersonalnformationwithus.png");

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
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.areyouwillingtoshareyourpersonalnformationwithus.label_an_xia_que_ding_ji_biao_shi_nin_tong_yi_fen_fa_he_chu_shou_nin_zai_wan_ci_mo_zu_shi_shou_ji_de_ge_ren_zi_xun"), 7,
				9, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.areyouwillingtoshareyourpersonalnformationwithus.label_agree_to_the_distribution_and_sa"), 7, 24, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.areyouwillingtoshareyourpersonalnformationwithus.label_by_clicking_ok_you_agree_to"), 7, 39, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.areyouwillingtoshareyourpersonalnformationwithus.label_information_collected_while_play"), 7, 53, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.areyouwillingtoshareyourpersonalnformationwithus.label_laying_this_mod"), 7, 69, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.eventjar.areyouwillingtoshareyourpersonalnformationwithus.label_click_the_button_to_confirm"), 12, 112, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		button_kill_yourself = Button.builder(Component.translatable("gui.eventjar.areyouwillingtoshareyourpersonalnformationwithus.button_kill_yourself"), e -> {
			if (true) {
				EventjarMod.PACKET_HANDLER.sendToServer(new AreyouwillingtoshareyourpersonalnformationwithusButtonMessage(0, x, y, z));
				AreyouwillingtoshareyourpersonalnformationwithusButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 42, this.topPos + 133, 93, 20).build();
		guistate.put("button:button_kill_yourself", button_kill_yourself);
		this.addRenderableWidget(button_kill_yourself);
	}
}
