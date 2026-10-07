package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.EventjarMod;

public class RageThen60sPriNachalnomPrizyvieSushchnostiProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		EventjarMod.queueServerWork(2000, () -> {
			if (!entity.level().isClientSide())
				entity.discard();
		});
	}
}
