package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

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

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Lixis9SpawnProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			if (!TickThrottle.due(event.player))
				return;
			execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z) {
		execute(null, world, x, y, z);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z) {
		if (Math.random() < (TickThrottle.INTERVAL) / ((float) 300000)) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("find me"), false);
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = EventjarModEntities.LIXIS_9.get().spawn(_level, BlockPos.containing(x, y, z + 40), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			TimerEventProcedure.execute(world, x, y, z);
			EventjarMod.queueServerWork(1200, () -> {
				if (!world.getEntitiesOfClass(Lixis9Entity.class, AABB.ofSize(new Vec3(x, y, z), 1000, 1000, 1000), e -> true).isEmpty()) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = EventjarModEntities.RAGE_THEN_60S.get().spawn(_level, BlockPos.containing(x, y, z + 40), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				}
			});
		}
	}
}
