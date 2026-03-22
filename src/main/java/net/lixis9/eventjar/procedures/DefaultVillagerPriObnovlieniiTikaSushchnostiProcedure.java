package net.mcreator.mimicevent.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

public class DefaultVillagerPriObnovlieniiTikaSushchnostiProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.setCustomName(Component.literal("Default Villager ;3"));
	}
}
