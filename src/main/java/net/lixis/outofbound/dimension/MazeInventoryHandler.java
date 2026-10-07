package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class MazeInventoryHandler {

	private static final String SAVED_INVENTORY_KEY = "outofbound_maze_saved_inv";

	private MazeInventoryHandler() {
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onLivingDeath(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (!isMazeDimension(player.level().dimension().location())) {
			return;
		}
		saveInventory(player);
	}

	@SubscribeEvent
	public static void onLivingDrops(LivingDropsEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (!isMazeDimension(player.level().dimension().location())) {
			return;
		}
		event.getDrops().clear();
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		if (!event.isWasDeath()) {
			return;
		}
		if (!(event.getOriginal() instanceof ServerPlayer original)) {
			return;
		}
		if (!(event.getEntity() instanceof ServerPlayer respawned)) {
			return;
		}
		if (!isMazeDimension(original.level().dimension().location())) {
			return;
		}

		restoreInventory(original, respawned);
	}

	private static boolean isMazeDimension(ResourceLocation dimensionId) {
		return MazeDimensions.isMazeDimension(dimensionId);
	}

	private static void saveInventory(ServerPlayer player) {
		ListTag saved = new ListTag();
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (stack.isEmpty()) {
				continue;
			}
			CompoundTag itemTag = new CompoundTag();
			itemTag.putInt("Slot", slot);
			stack.save(itemTag);
			saved.add(itemTag);
		}
		player.getPersistentData().put(SAVED_INVENTORY_KEY, saved);
	}

	private static void restoreInventory(ServerPlayer original, ServerPlayer respawned) {
		CompoundTag data = original.getPersistentData();
		if (!data.contains(SAVED_INVENTORY_KEY)) {
			return;
		}

		ListTag saved = data.getList(SAVED_INVENTORY_KEY, ListTag.TAG_COMPOUND);
		for (int i = 0; i < saved.size(); i++) {
			CompoundTag itemTag = saved.getCompound(i);
			int slot = itemTag.getInt("Slot");
			if (slot < 0 || slot >= respawned.getInventory().getContainerSize()) {
				respawned.getInventory().add(ItemStack.of(itemTag));
			} else {
				respawned.getInventory().setItem(slot, ItemStack.of(itemTag));
			}
		}

		data.remove(SAVED_INVENTORY_KEY);
	}
}
