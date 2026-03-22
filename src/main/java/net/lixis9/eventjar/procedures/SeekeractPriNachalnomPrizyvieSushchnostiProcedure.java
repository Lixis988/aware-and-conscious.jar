package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.EventjarMod;

public class SeekeractPriNachalnomPrizyvieSushchnostiProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		TimerEventProcedure.execute(world, x, y, z);
		EventjarMod.queueServerWork(1200, () -> {
			if (!entity.level().isClientSide())
				entity.discard();
		});
	}
}
