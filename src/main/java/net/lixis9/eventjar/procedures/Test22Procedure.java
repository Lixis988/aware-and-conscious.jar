package net.lixis9.eventjar.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.entity.Lixis9Entity;
import net.lixis9.eventjar.EventjarMod;

public class Test22Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EventjarModEntities.LIXIS_9.get().spawn(_level, BlockPos.containing(x, y, z + 40), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
		TimerEventProcedure.execute(world, x, y, z);
		EventjarMod.queueServerWork(1200, () -> {
			if (!world.getEntitiesOfClass(Lixis9Entity.class, AABB.ofSize(new Vec3(x, y, (z + 40)), 1000, 1000, 1000), e -> true).isEmpty()) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = EventjarModEntities.RAGE_THEN_60S.get().spawn(_level, BlockPos.containing(x, y, z + 40), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("find me"), false);
			}
		});
	}
}
