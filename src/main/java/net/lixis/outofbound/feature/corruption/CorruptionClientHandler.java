package net.lixis.outofbound.feature.corruption;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.FeatureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class CorruptionClientHandler {

	private CorruptionClientHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			CorruptionClientState.setLevel(0.0F);
			return;
		}
		FeatureManager.clientTick();
	}

	@SubscribeEvent
	public static void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
		if (!CorruptionClientState.isActive() || CorruptionClientState.getUiScaleX() == 1.0F
				&& CorruptionClientState.getUiScaleY() == 1.0F) {
			return;
		}

		PoseStack poseStack = event.getGuiGraphics().pose();
		var id = event.getOverlay().id();
		if (id.equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
				|| id.equals(VanillaGuiOverlay.FOOD_LEVEL.id())
				|| id.equals(VanillaGuiOverlay.HOTBAR.id())
				|| id.equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
			int width = event.getWindow().getGuiScaledWidth();
			int height = event.getWindow().getGuiScaledHeight();
			poseStack.pushPose();
			poseStack.translate(width * 0.5F, height * 0.5F, 0.0F);
			poseStack.scale(CorruptionClientState.getUiScaleX(), CorruptionClientState.getUiScaleY(), 1.0F);
			poseStack.translate(-width * 0.5F, -height * 0.5F, 0.0F);
		}
	}

	@SubscribeEvent
	public static void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
		if (!CorruptionClientState.isActive()) {
			return;
		}

		var id = event.getOverlay().id();
		if (id.equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
				|| id.equals(VanillaGuiOverlay.FOOD_LEVEL.id())
				|| id.equals(VanillaGuiOverlay.HOTBAR.id())
				|| id.equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
			if (CorruptionClientState.getUiScaleX() != 1.0F || CorruptionClientState.getUiScaleY() != 1.0F) {
				event.getGuiGraphics().pose().popPose();
			}
		}

		if (id.equals(VanillaGuiOverlay.HOTBAR.id()) && CorruptionClientState.hasInventoryCorruption()) {
			drawCorruptedHotbar(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(),
					event.getWindow().getGuiScaledHeight());
		}
	}

	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event) {
		if (!CorruptionClientState.isActive()) {
			return;
		}
		float scale = CorruptionClientState.getHandScale();
		if (scale == 1.0F) {
			return;
		}
		event.getPoseStack().scale(scale, scale, scale);
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		if (!CorruptionClientState.isActive()) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if (stack.isEmpty()) {
			return;
		}

		int slot = findInventorySlot(stack);
		if (slot < 0) {
			return;
		}

		@Nullable String corruptedName = CorruptionClientState.getCorruptedSlotName(slot);
		if (corruptedName == null) {
			return;
		}

		List<Component> tooltip = event.getToolTip();
		if (tooltip.isEmpty()) {
			return;
		}

		String label = corruptedName + " x" + stack.getCount();
		tooltip.set(0, Component.literal(label));
	}

	private static void drawCorruptedHotbar(GuiGraphics graphics, int width, int height) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return;
		}

		int left = width / 2 - 91;
		int top = height - 22;

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			ItemStack fakeIcon = CorruptionClientState.getCorruptedSlotIcon(hotbarSlot);
			if (fakeIcon != null) {
				int x = left + hotbarSlot * 20 + 3;
				int y = top + 3;
				graphics.renderItem(fakeIcon, x, y);
			}
		}
	}

	private static int findInventorySlot(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return -1;
		}
		for (int slot = 0; slot < minecraft.player.getInventory().getContainerSize(); slot++) {
			ItemStack candidate = minecraft.player.getInventory().getItem(slot);
			if (ItemStack.isSameItemSameTags(candidate, stack)) {
				return slot;
			}
		}
		return -1;
	}
}
