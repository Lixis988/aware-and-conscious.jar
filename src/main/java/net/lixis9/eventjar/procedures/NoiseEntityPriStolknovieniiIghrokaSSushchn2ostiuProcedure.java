package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class NoiseEntityPriStolknovieniiIghrokaSSushchn2ostiuProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!entity.level().isClientSide())
			entity.discard();
		NoiseEntityPriStolknovieniiIghrokaSSushchnostiuProcedure.execute(world, x, y, z);
	}
}
