package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class InvisibleWeirdEntityPriNachalnomPrizyvieSushchnostiProcedure {
	public static void execute(double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Mob _entity)
			_entity.getNavigation().moveTo((x + Mth.nextInt(RandomSource.create(), 1, 80)), (y + Mth.nextInt(RandomSource.create(), 1, 80)), (z + Mth.nextInt(RandomSource.create(), 1, 80)), 5);
	}
}
