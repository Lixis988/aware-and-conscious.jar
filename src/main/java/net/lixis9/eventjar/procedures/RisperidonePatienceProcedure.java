package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RisperidonePatienceProcedure {

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		double _setval = (entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables())).PatienceForRisperidone + 1;
		entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
			capability.PatienceForRisperidone = _setval;

			if (TickThrottle.due(entity)) {
				capability.syncPlayerVariables(entity);
				Risperidone4Procedure.maybeAnnounce(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
			}
		});
	}
}
