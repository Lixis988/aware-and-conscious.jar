package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionClientState;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.util.BitCorruptor;
import net.lixis.outofbound.feature.corruption.util.RandomItemPool;
import net.lixis.outofbound.feature.corruption.util.StackCountUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class DataCorruptor extends ClientCorruptor {

	@Override
	public float minLevel() {
		return 5.0F;
	}

	@Override
	public int weight() {
		return 3;
	}

	@Override
	protected void runClient(Minecraft minecraft, CorruptionRunContext ctx) {
		if (minecraft.player == null) {
			return;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		List<Integer> slots = new ArrayList<>();

		for (int slot = 0; slot < 9; slot++) {
			if (!minecraft.player.getInventory().getItem(slot).isEmpty()) {
				slots.add(slot);
			}
		}

		int inventorySize = minecraft.player.getInventory().getContainerSize();
		for (int extra = 0; extra < 5; extra++) {
			int slot = random.nextInt(inventorySize);
			if (!minecraft.player.getInventory().getItem(slot).isEmpty() && !slots.contains(slot)) {
				slots.add(slot);
			}
		}

		if (slots.isEmpty()) {
			return;
		}

		Collections.shuffle(slots, random);
		int corruptCount = Math.min(slots.size(), 3 + random.nextInt(4));
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());

		for (int i = 0; i < corruptCount; i++) {
			int slot = slots.get(i);
			ItemStack stack = minecraft.player.getInventory().getItem(slot);
			String corruptedName = BitCorruptor.corruptText(stack.getHoverName().getString());
			ItemStack corruptedStack = StackCountUtil.withCorruptedCount(stack);
			ItemStack fakeIcon = RandomItemPool.randomStack(random);

			CorruptionClientState.applySlotCorruption(
					minecraft,
					slot,
					corruptedStack,
					corruptedName,
					fakeIcon,
					ttl);
		}
	}
}
