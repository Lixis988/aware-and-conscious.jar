package net.lixis.outofbound.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lixis.outofbound.EvilNotexturemanResponsePacket;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class EvilNotexturemanScreen extends Screen {

	private static final ResourceLocation EYE_TEXTURE =
			new ResourceLocation(OutofboundMod.MODID, "textures/entities/eye_anim.png");

	private static final int BUTTON_WIDTH = 120;
	private static final int BUTTON_HEIGHT = 20;
	private static final float EYE_SCREEN_FRACTION = 0.45F;
	private static final int VIRTUAL_FRAME_WIDTH = 100;

	private final int entityId;
	private boolean responded;
	private final long openedAtMs = System.currentTimeMillis();

	public EvilNotexturemanScreen(int entityId) {
		super(Component.literal("..."));
		this.entityId = entityId;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int buttonY = this.height / 2 + Math.round(this.height * EYE_SCREEN_FRACTION * 0.5F) + 16;
		this.addRenderableWidget(Button.builder(Component.literal("YES"), button -> respond())
				.bounds(centerX - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());
	}

	private void respond() {
		if (responded) {
			return;
		}
		responded = true;
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.SERVER.noArg(), new EvilNotexturemanResponsePacket(entityId));
		this.minecraft.setScreen(null);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		int size = Math.max(64, Math.round(this.height * EYE_SCREEN_FRACTION));
		int x = (this.width - size) / 2;
		int y = this.height / 2 - size / 2 - 10;

		long elapsed = System.currentTimeMillis() - openedAtMs;
		int frame = EyeAnimFrames.frameForTime(elapsed);
		int textureWidth = EyeAnimFrames.COUNT * VIRTUAL_FRAME_WIDTH;
		int uOffset = frame * VIRTUAL_FRAME_WIDTH;

		guiGraphics.blit(EYE_TEXTURE, x, y, uOffset, 0, size, size, textureWidth, VIRTUAL_FRAME_WIDTH);

		super.render(guiGraphics, mouseX, mouseY, partialTick);
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
			return;
		}
		super.onClose();
	}
}
