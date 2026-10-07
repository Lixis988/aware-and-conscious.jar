package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.AacConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "eventjar")
public class SpawnWatcherTickSubscriber {
	private static final long TICKS_BETWEEN_SPAWNS = 20L * 90L;
	private static final Map<ResourceKey<Level>, Long> LAST_SPAWN_TICK = new ConcurrentHashMap<>();

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		Player player = event.player;

		Player driver = serverWorld.getRandomPlayer();
		if (driver == null || driver != player) {
			if (player.tickCount % 20 == 0) {
				SpawnWatcherProcedure.cullExtras(serverWorld, player);
			}
			return;
		}

		if (AacConfig.ENTITY_SPAWN_MULTIPLIER <= 0.0D) {
			if (player.tickCount % 20 == 0) {
				SpawnWatcherProcedure.cullExtras(serverWorld, player);
			}
			return;
		}

		ResourceKey<Level> dim = serverWorld.dimension();
		long currentTick = serverWorld.getGameTime();
		long lastSpawnTick = LAST_SPAWN_TICK.getOrDefault(dim, 0L);
		long interval = AacConfig.scaleEntityInterval(TICKS_BETWEEN_SPAWNS);

		if (currentTick - lastSpawnTick < interval) {
			if (player.tickCount % 20 == 0) {
				SpawnWatcherProcedure.cullExtras(serverWorld, player);
			}
			return;
		}

		LAST_SPAWN_TICK.put(dim, currentTick);
		SpawnWatcherProcedure.execute(serverWorld, player);
	}
}
