package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;

import net.lixis9.eventjar.init.EventjarModItems;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RasperidoneSpawnProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (!(entity instanceof Player player) || player.level().isClientSide()) {
			return;
		}
		Inventory inv = player.getInventory();

		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (inv.getItem(i).is(EventjarModItems.RISPERIDONE.get())) {
				return;
			}
		}
		ItemStack stack = new ItemStack(EventjarModItems.RISPERIDONE.get());
		stack.setCount(1);

		if (inv.getItem(0).isEmpty()) {
			inv.setItem(0, stack);
			return;
		}
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (inv.getItem(i).isEmpty()) {
				inv.setItem(i, stack);
				return;
			}
		}

	}
}
