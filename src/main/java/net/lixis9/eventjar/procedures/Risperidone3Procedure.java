package net.lixis9.eventjar.procedures;

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

public class Risperidone3Procedure {
	public static boolean execute(Entity entity) {
		return execute(null, entity);
	}

	private static boolean execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return false;
		return (entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables())).PatienceForRisperidone >= 7200;
	}
}
