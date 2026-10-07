package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.RandomItemPool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public final class InventoryCorruptor extends ServerCorruptor {

	@Override
	public float minLevel() {
		return 25.0F;
	}

	@Override
	public int weight() {
		return 2;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int swaps = 2 + random.nextInt(3);
		int size = player.getInventory().getContainerSize();
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());

		for (int i = 0; i < swaps; i++) {
			int slotA = random.nextInt(size);
			int slotB = random.nextInt(size);
			if (slotA == slotB) {
				continue;
			}

			ItemStack stackA = player.getInventory().getItem(slotA);
			ItemStack stackB = player.getInventory().getItem(slotB);

			if (stackA.isEmpty() && stackB.isEmpty()) {
				int fillSlot = random.nextInt(size);
				ItemStack empty = player.getInventory().getItem(fillSlot);
				CorruptionSnapshots.scheduleInventoryRestore(player.getUUID(), fillSlot, empty.copy(), ttl);
				player.getInventory().setItem(fillSlot, RandomItemPool.randomStack(random));
				continue;
			}

			if (stackA.isEmpty()) {
				CorruptionSnapshots.scheduleInventoryRestore(player.getUUID(), slotA, ItemStack.EMPTY, ttl);
				player.getInventory().setItem(slotA, RandomItemPool.randomStack(random));
				continue;
			}
			if (stackB.isEmpty()) {
				CorruptionSnapshots.scheduleInventoryRestore(player.getUUID(), slotB, ItemStack.EMPTY, ttl);
				player.getInventory().setItem(slotB, RandomItemPool.randomStack(random));
				continue;
			}

			CorruptionSnapshots.scheduleInventoryRestore(player.getUUID(), slotA, stackA.copy(), ttl);
			CorruptionSnapshots.scheduleInventoryRestore(player.getUUID(), slotB, stackB.copy(), ttl);

			player.getInventory().setItem(slotA, stackB.copy());
			player.getInventory().setItem(slotB, stackA.copy());
		}

		player.inventoryMenu.broadcastChanges();
	}
}
