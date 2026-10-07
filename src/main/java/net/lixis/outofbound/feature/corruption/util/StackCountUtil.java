package net.lixis.outofbound.feature.corruption.util;

import net.minecraft.world.item.ItemStack;

public final class StackCountUtil {

	private StackCountUtil() {
	}

	public static int corruptCount(ItemStack stack) {
		int original = stack.getCount();
		int corrupted = SafeCorruptor.safeInt(BitCorruptor.corruptInt(original));
		int max = Math.max(1, stack.getMaxStackSize());
		corrupted = Math.max(1, Math.min(max, corrupted));
		if (corrupted == original) {
			corrupted = original >= max ? 1 : Math.min(max, original + 1 + BitCorruptor.random().nextInt(8));
		}
		return corrupted;
	}

	public static ItemStack withCorruptedCount(ItemStack stack) {
		ItemStack copy = stack.copy();
		copy.setCount(corruptCount(stack));
		return copy;
	}
}
