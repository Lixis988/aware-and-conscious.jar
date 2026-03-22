package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Entity;

public class ErrundefinePriGibieliOtEtoiSushchnostiDrughoiProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!entity.level().isClientSide())
			entity.discard();
	}
}
