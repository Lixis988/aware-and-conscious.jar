package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class UnknownSpawnHandler {

	private static final int SPAWN_INTERVAL_TICKS = 10_000;
	private static final double SPAWN_CHANCE = 0.40D;

	private UnknownSpawnHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		ServerLevel overworld = event.getServer().getLevel(Level.OVERWORLD);
		if (overworld == null) {
			return;
		}

		if (overworld.getGameTime() % SPAWN_INTERVAL_TICKS != 0L) {
			return;
		}

		if (ThreadLocalRandom.current().nextDouble() >= SPAWN_CHANCE) {
			return;
		}

		UnknownSpawnManager.trySpawn(overworld);
	}
}
