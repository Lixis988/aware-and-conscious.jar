package net.lixis9.eventjar.procedures;

import javax.annotation.Nullable;

import net.lixis9.eventjar.TickThrottle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.lixis9.eventjar.network.EventjarModVariables;

@Mod.EventBusSubscriber
public class ChunkRndProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			if (!TickThrottle.due(event.player))
				return;
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;

		Level world = entity.getCommandSenderWorld();

		if (entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables()).reputation > 60
				&& Math.random() < (TickThrottle.INTERVAL / 100_000.0)) {
			ChunkDellProcedure.execute(world);
		}
	}
}
