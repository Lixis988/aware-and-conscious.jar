package net.lixis9.eventjar.procedures;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;

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
		if (entity == null)
			return;
		{
			final int _slotid = 0;
			final ItemStack _setstack = new ItemStack(EventjarModItems.RISPERIDONE.get()).copy();
			_setstack.setCount(1);
			entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
				if (capability instanceof IItemHandlerModifiable _modHandlerEntSetSlot)
					_modHandlerEntSetSlot.setStackInSlot(_slotid, _setstack);
			});
		}
	}
}
