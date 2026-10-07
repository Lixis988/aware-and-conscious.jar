package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class NotexturemanTickHandler {

	private NotexturemanTickHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!MazeConfig.enableNotextureman) {
			return;
		}

		int interval = Math.max(1, MazeConfig.notexturemanIntervalTicks);
		ThreadLocalRandom random = ThreadLocalRandom.current();

		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			ServerLevel level = player.serverLevel();
			if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
				continue;
			}
			long phase = level.getGameTime() + player.getId();
			if (phase % interval != 0) {
				continue;
			}
			if (random.nextDouble() >= MazeConfig.notexturemanSpawnChance) {
				continue;
			}
			NotexturemanSpawnManager.trySpawn(level, player);
		}
	}
}
