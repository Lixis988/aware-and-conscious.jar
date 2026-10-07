package net.lixis.outofbound.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.lixis.outofbound.entity.BoundedcowSpawnManager;
import net.lixis.outofbound.init.OutofboundModEntities;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;

public class NormalcowPriStolknovieniiIghrokaSSushchnostiuProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (!(world instanceof ServerLevel level)) {
			return;
		}

		MinecraftServer server = level.getServer();
		if (WorldInternalConfig.hasBoundedcowCollision(server)) {
			BoundedcowSpawnManager.removeAll(server);
			return;
		}
		if (BoundedcowSpawnManager.hasAny(server)) {
			BoundedcowSpawnManager.keepAtMostOne(server);
			return;
		}

		Entity entityToSpawn = OutofboundModEntities.BOUNDEDCOW.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
		if (entityToSpawn != null) {
			entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
		}
	}
}
