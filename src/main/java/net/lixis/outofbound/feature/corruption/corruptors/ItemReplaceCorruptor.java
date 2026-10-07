package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.RandomItemPool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class ItemReplaceCorruptor extends ServerCorruptor {

	@Override
	public float minLevel() {
		return 10.0F;
	}

	@Override
	public int weight() {
		return 4;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());
		int replacements = 1 + random.nextInt(ctx.level() >= 50.0F ? 3 : 2);

		for (int i = 0; i < replacements; i++) {
			int slot = pickSlot(player, random);
			if (slot < 0) {
				continue;
			}

			ItemStack original = player.getInventory().getItem(slot);
			CorruptionSnapshots.scheduleInventoryRestore(player.getUUID(), slot, original.copy(), ttl);
			player.getInventory().setItem(slot, RandomItemPool.randomStack(random));
		}

		player.inventoryMenu.broadcastChanges();
	}

	private static int pickSlot(ServerPlayer player, ThreadLocalRandom random) {
		List<Integer> hotbar = new ArrayList<>();
		List<Integer> other = new ArrayList<>();

		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			if (!player.getInventory().getItem(slot).isEmpty()) {
				if (slot < 9) {
					hotbar.add(slot);
				} else {
					other.add(slot);
				}
			}
		}

		if (!hotbar.isEmpty() && (other.isEmpty() || random.nextBoolean())) {
			return hotbar.get(random.nextInt(hotbar.size()));
		}
		if (!other.isEmpty()) {
			return other.get(random.nextInt(other.size()));
		}
		if (!hotbar.isEmpty()) {
			return hotbar.get(random.nextInt(hotbar.size()));
		}
		return -1;
	}
}
