package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerLevel;

import net.lixis9.eventjar.init.EventjarModItems;

public class Redemption2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (Math.random() < (1) / ((float) 200000)) {
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(EventjarModItems.REDEMPTION.get()));
				entityToSpawn.setPickUpDelay(10);
				_level.addFreshEntity(entityToSpawn);
			}
		}
	}
}
