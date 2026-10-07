package net.lixis9.eventjar.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;

import net.lixis9.eventjar.entity.NoiseEntityEntity;

public class NoiseProcedure3Procedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		if (!world.getEntitiesOfClass(NoiseEntityEntity.class, AABB.ofSize(new Vec3(x, y, z), 4, 4, 4), e -> true).isEmpty()) {
			return true;
		}
		return false;
	}
}
