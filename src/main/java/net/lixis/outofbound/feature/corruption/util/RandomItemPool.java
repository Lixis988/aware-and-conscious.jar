package net.lixis.outofbound.feature.corruption.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomItemPool {

	private static List<Item> pool;

	private RandomItemPool() {
	}

	public static ItemStack randomStack(ThreadLocalRandom random) {
		List<Item> items = getPool();
		Item item = items.get(random.nextInt(items.size()));
		int maxStack = Math.max(1, item.getMaxStackSize());
		int count = 1 + random.nextInt(Math.min(64, maxStack));
		return new ItemStack(item, count);
	}

	private static List<Item> getPool() {
		if (pool != null) {
			return pool;
		}
		List<Item> items = new ArrayList<>();
		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (item == null || item == Items.AIR || item.getMaxStackSize() <= 0) {
				continue;
			}
			String namespace = ForgeRegistries.ITEMS.getKey(item).getNamespace();
			if (!"minecraft".equals(namespace) && !"outofbound".equals(namespace)) {
				continue;
			}
			items.add(item);
			if (items.size() >= 200) {
				break;
			}
		}
		pool = items;
		return pool;
	}
}
