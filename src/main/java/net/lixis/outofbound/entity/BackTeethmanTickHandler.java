package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class BackTeethmanTickHandler {

	private static final int SPAWN_INTERVAL_TICKS = 15_000;
	private static final double SPAWN_CHANCE = 0.5D;

	private BackTeethmanTickHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (server.getTickCount() % SPAWN_INTERVAL_TICKS != 0) {
			return;
		}
		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}
		if (ThreadLocalRandom.current().nextDouble() >= SPAWN_CHANCE) {
			return;
		}

		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		if (overworld == null) {
			return;
		}

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.level() == overworld) {
				BackTeethmanSpawnManager.trySpawnForPlayer(overworld, player);
			}
		}
	}
}
