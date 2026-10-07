package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class EventRAREProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			if (!TickThrottle.due(event.player))
				return;
			execute(event, event.player.level(), event.player);
		}
	}

	public static boolean execute(LevelAccessor world, Entity entity) {
		return execute(null, world, entity);
	}

	private static boolean execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables())).patience >= 10000) {
			if (Math.random() < (TickThrottle.INTERVAL) / ((float) 50000)) {
				if (!world.isClientSide() && world.getServer() != null)
					ServerLifecycleHooks.getCurrentServer().stopServer();
				return true;
			}
			{
				double _setval = 0;
				entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.patience = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
		}
		return false;
	}
}
