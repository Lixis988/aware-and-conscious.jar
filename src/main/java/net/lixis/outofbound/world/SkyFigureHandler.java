package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class SkyFigureHandler {

	private SkyFigureHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!MazeConfig.enableSkyFigures) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (server == null || !WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}

		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		if (overworld == null) {
			return;
		}

		SkyFigureData data = SkyFigureData.get(overworld);
		data.tickCleanup(overworld);

		long tick = overworld.getGameTime();
		int interval = PostEyeWeirdness.scaledInterval(server, Math.max(1, MazeConfig.skyFigureIntervalTicks));
		if (tick % interval != 0) {
			return;
		}
		if (ThreadLocalRandom.current().nextDouble() >= PostEyeWeirdness.scaledChance(server, MazeConfig.skyFigureSpawnChance)) {
			return;
		}

		SkyFigureSpawner.trySpawn(overworld);
	}
}
