/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.lixis9.eventjar as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.lixis9.eventjar;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.entity.WatcherEntity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Ifyoucometowatcher {
	public Ifyoucometowatcher() {
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		new Ifyoucometowatcher();
	}

	@Mod.EventBusSubscriber
	private static class ForgeBusEvents {
		@SubscribeEvent
		public static void serverLoad(ServerStartingEvent event) {
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientLoad(FMLClientSetupEvent event) {
		}
		
		@SubscribeEvent
		public static void onServerTick(TickEvent.ServerTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				checkWatcherDistance(event.getServer());
			}
		}
	}
	
	private static void checkWatcherDistance(net.minecraft.server.MinecraftServer server) {
		if (server == null) return;
		
		// Получаем все серверные уровни
		for (ServerLevel level : server.getAllLevels()) {
			// Ищем всех WatcherEntity в мире
			level.getEntitiesOfClass(WatcherEntity.class, 
				net.minecraft.world.phys.AABB.ofSize(
					net.minecraft.world.phys.Vec3.atCenterOf(level.getSharedSpawnPos()), 
					200, 200, 200
				), // Ищем в большом радиусе вокруг спавна
				watcher -> true
			).forEach(watcher -> {
				// Ищем ближайшего игрока к этой сущности
				net.minecraft.world.entity.player.Player nearestPlayer = level.getNearestPlayer(
					watcher.getX(), watcher.getY(), watcher.getZ(), 2.0, false
				);
				
				if (nearestPlayer != null) {
					// Проверяем расстояние до игрока (1-2 блока)
					double distance = watcher.distanceTo(nearestPlayer);
					if (distance <= 2.0) {
						// Сохраняем позицию watcher
						double x = watcher.getX();
						double y = watcher.getY();
						double z = watcher.getZ();
						
						// Удаляем watcher
						watcher.discard();
						
						// Спавним scavenger на том же месте
						Entity scavenger = EventjarModEntities.SCAVENGER.get().spawn(
							level, 
							BlockPos.containing(x, y, z), 
							MobSpawnType.MOB_SUMMONED
						);
						
						if (scavenger != null) {
							scavenger.setYRot(level.getRandom().nextFloat() * 360F);
						}
					}
				}
			});
		}
	}
}
