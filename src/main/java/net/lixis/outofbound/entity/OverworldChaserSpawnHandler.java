package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.world.FinaleState;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class OverworldChaserSpawnHandler {

	private OverworldChaserSpawnHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (!isAfterSeraphim(server)) {
			return;
		}

		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		if (overworld == null) {
			return;
		}

		ServerPlayer anchor = pickOverworldPlayer(overworld);
		if (anchor == null) {
			return;
		}

		long gameTime = overworld.getGameTime();
		ThreadLocalRandom random = ThreadLocalRandom.current();

		tryRoll(overworld, anchor, gameTime, random,
				MazeConfig.enableOverworldUndefiend,
				MazeConfig.overworldUndefiendIntervalTicks,
				MazeConfig.overworldUndefiendSpawnChance,
				OverworldUndefiendSpawnManager::trySpawnForPlayer);

		tryRoll(overworld, anchor, gameTime, random,
				MazeConfig.enableOverworldTeethman,
				MazeConfig.overworldTeethmanIntervalTicks,
				MazeConfig.overworldTeethmanSpawnChance,
				OverworldTeethmanSpawnManager::trySpawnForPlayer);
	}

	private static void tryRoll(
			ServerLevel overworld,
			ServerPlayer anchor,
			long gameTime,
			ThreadLocalRandom random,
			boolean enabled,
			int intervalTicks,
			double spawnChance,
			BiPredicate<ServerLevel, ServerPlayer> spawner) {
		if (!enabled) {
			return;
		}
		int interval = Math.max(1, intervalTicks);
		if (gameTime % interval != 0L) {
			return;
		}
		if (random.nextDouble() >= spawnChance) {
			return;
		}
		spawner.test(overworld, anchor);
	}

	private static boolean isAfterSeraphim(MinecraftServer server) {
		return WorldInternalConfig.hasBoundedcowCollision(server)
				&& WorldInternalConfig.getFinaleState(server) == FinaleState.DONE;
	}

	private static ServerPlayer pickOverworldPlayer(ServerLevel overworld) {
		List<ServerPlayer> candidates = new ArrayList<>();
		for (ServerPlayer player : overworld.getServer().getPlayerList().getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			if (player.level().dimension() == Level.OVERWORLD) {
				candidates.add(player);
			}
		}
		if (candidates.isEmpty()) {
			return null;
		}
		return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
	}
}
