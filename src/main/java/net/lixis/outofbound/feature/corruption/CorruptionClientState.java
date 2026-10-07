package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.TestShaderHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class CorruptionClientState {

	private static float corruptionLevel;
	private static boolean active;

	private static float uiScaleX = 1.0F;
	private static float uiScaleY = 1.0F;
	private static float handScale = 1.0F;
	private static int uiBurstTicks;

	private static int renderCorruptTicks;

	private final static Map<Integer, String> corruptedSlotNames = new HashMap<>();
	private final static Map<Integer, ItemStack> corruptedSlotIcons = new HashMap<>();
	private final static Map<Integer, ItemStack> inventoryRestoreStacks = new HashMap<>();
	private static int inventoryBurstTicks;

	private CorruptionClientState() {
	}

	public static void setLevel(float level) {
		corruptionLevel = Math.max(0.0F, Math.min(100.0F, level));
		active = corruptionLevel > 0.0F;
		CorruptionRenderGate.update(corruptionLevel);
		if (active) {
			renderCorruptTicks = Math.max(renderCorruptTicks, 40 + (int) (corruptionLevel * 0.6F));
			updateCorruptionShader();
		} else {
			resetTransient();
		}
	}

	public static float getLevel() {
		return corruptionLevel;
	}

	public static boolean isActive() {
		return active;
	}

	public static float getRenderIntensity() {
		return corruptionLevel / 100.0F;
	}

	public static void extendRenderCorruption(int ticks) {
		renderCorruptTicks = Math.max(renderCorruptTicks, ticks);
	}

	public static float toggle() {
		float next = active ? 0.0F : 50.0F;
		setLevel(next);
		return next;
	}

	public static void setUiScale(float x, float y, int ticks) {
		uiScaleX = x;
		uiScaleY = y;
		uiBurstTicks = ticks;
		renderCorruptTicks = Math.max(renderCorruptTicks, ticks * 10);
	}

	public static float getUiScaleX() {
		return uiBurstTicks > 0 ? uiScaleX : 1.0F;
	}

	public static float getUiScaleY() {
		return uiBurstTicks > 0 ? uiScaleY : 1.0F;
	}

	public static void setHandScale(float scale, int ticks) {
		handScale = scale;
		uiBurstTicks = Math.max(uiBurstTicks, ticks);
		renderCorruptTicks = Math.max(renderCorruptTicks, ticks * 10);
	}

	public static float getHandScale() {
		return uiBurstTicks > 0 ? handScale : 1.0F;
	}

	public static void applySlotCorruption(Minecraft minecraft, int slot, ItemStack corruptedStack,
			@Nullable String corruptedName, @Nullable ItemStack fakeIcon, int ticks) {
		if (minecraft.player == null) {
			return;
		}
		inventoryRestoreStacks.putIfAbsent(slot, minecraft.player.getInventory().getItem(slot).copy());
		minecraft.player.getInventory().setItem(slot, corruptedStack.copy());

		if (corruptedName != null) {
			corruptedSlotNames.put(slot, corruptedName);
		} else {
			corruptedSlotNames.remove(slot);
		}
		if (fakeIcon != null && !fakeIcon.isEmpty()) {
			corruptedSlotIcons.put(slot, fakeIcon.copy());
		} else {
			corruptedSlotIcons.remove(slot);
		}
		inventoryBurstTicks = Math.max(inventoryBurstTicks, ticks);
	}

	public static boolean hasInventoryCorruption() {
		return inventoryBurstTicks > 0
				&& (!corruptedSlotNames.isEmpty() || !corruptedSlotIcons.isEmpty() || !inventoryRestoreStacks.isEmpty());
	}

	@Nullable
	public static String getCorruptedSlotName(int slot) {
		return inventoryBurstTicks > 0 ? corruptedSlotNames.get(slot) : null;
	}

	@Nullable
	public static ItemStack getCorruptedSlotIcon(int slot) {
		ItemStack icon = corruptedSlotIcons.get(slot);
		return inventoryBurstTicks > 0 && icon != null ? icon : null;
	}

	public static void clientTick() {
		if (uiBurstTicks > 0) {
			uiBurstTicks--;
		}
		if (renderCorruptTicks > 0) {
			renderCorruptTicks--;
		}
		if (active) {
			updateCorruptionShader();
		}
		if (inventoryBurstTicks > 0) {
			inventoryBurstTicks--;
			if (inventoryBurstTicks <= 0) {
				restoreInventoryStacks(Minecraft.getInstance());
				corruptedSlotNames.clear();
				corruptedSlotIcons.clear();
			}
		}
		if (uiBurstTicks <= 0) {
			uiScaleX = 1.0F;
			uiScaleY = 1.0F;
			handScale = 1.0F;
		}
	}

	public static void resetTransient() {
		restoreInventoryStacks(Minecraft.getInstance());
		uiScaleX = 1.0F;
		uiScaleY = 1.0F;
		handScale = 1.0F;
		uiBurstTicks = 0;
		renderCorruptTicks = 0;
		corruptedSlotNames.clear();
		corruptedSlotIcons.clear();
		inventoryRestoreStacks.clear();
		inventoryBurstTicks = 0;
		CorruptionScheduler.reset();
		CorruptionRenderGate.clear();
		TestShaderHandler.setCorruption(null);
	}

	private static void restoreInventoryStacks(@Nullable Minecraft minecraft) {
		if (minecraft == null || minecraft.player == null) {
			inventoryRestoreStacks.clear();
			return;
		}
		for (Map.Entry<Integer, ItemStack> entry : inventoryRestoreStacks.entrySet()) {
			minecraft.player.getInventory().setItem(entry.getKey(), entry.getValue().copy());
		}
		inventoryRestoreStacks.clear();
	}

	private static void updateCorruptionShader() {
		TestShaderHandler.setCorruption(null);
	}
}
