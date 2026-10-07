package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.StackCountUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class StackCountCorruptor extends ServerCorruptor {

	@Override
	public float minLevel() {
		return 5.0F;
	}

	@Override
	public int weight() {
		return 4;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		List<Integer> slots = new ArrayList<>();

		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			if (!player.getInventory().getItem(slot).isEmpty()) {
				slots.add(slot);
			}
		}
		if (slots.isEmpty()) {
			return;
		}

		Collections.shuffle(slots, random);
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());
		int corruptCount = Math.min(slots.size(), 2 + random.nextInt(4));

		for (int i = 0; i < corruptCount; i++) {
			int slot = slots.get(i);
			ItemStack original = player.getInventory().getItem(slot);
			ItemStack corrupted = StackCountUtil.withCorruptedCount(original);

			CorruptionSnapshots.scheduleInventoryRestore(player.getUUID(), slot, original.copy(), ttl);
			player.getInventory().setItem(slot, corrupted);
		}

		player.inventoryMenu.broadcastChanges();
	}
}
