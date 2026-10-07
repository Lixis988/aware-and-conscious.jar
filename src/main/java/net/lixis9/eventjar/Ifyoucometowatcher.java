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
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

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

		private static int tickAccumulator;

		@SubscribeEvent
		public static void onServerTick(TickEvent.ServerTickEvent event) {
			if (event.phase != TickEvent.Phase.END) {
				return;
			}

			if (++tickAccumulator % 5 != 0) {
				return;
			}
			checkWatcherDistance(event.getServer());
		}
	}

	private static void checkWatcherDistance(net.minecraft.server.MinecraftServer server) {
		if (server == null) {
			return;
		}

		for (ServerLevel level : server.getAllLevels()) {
			for (Player player : level.players()) {
				AABB nearPlayer = player.getBoundingBox().inflate(3.0);
				for (WatcherEntity watcher : level.getEntitiesOfClass(WatcherEntity.class, nearPlayer, w -> true)) {
					if (watcher.distanceTo(player) > 2.0) {
						continue;
					}
					double x = watcher.getX();
					double y = watcher.getY();
					double z = watcher.getZ();
					watcher.discard();

					Entity scavenger = EventjarModEntities.SCAVENGER.get().spawn(
							level,
							BlockPos.containing(x, y, z),
							MobSpawnType.MOB_SUMMONED);

					if (scavenger != null) {
						scavenger.setYRot(level.getRandom().nextFloat() * 360F);
					}
				}
			}
		}
	}
}
