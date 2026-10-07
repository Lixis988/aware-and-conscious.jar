package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.world.FinaleState;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class SkyPainterSpawnHandler {

	private static final int SPAWN_INTERVAL_TICKS = 200;
	private static final double SPAWN_CHANCE = 0.6D;
	private static final int MAX_ACTIVE = 20;

	private SkyPainterSpawnHandler() {
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
		if (!isAfterSeraphim(server)) {
			return;
		}

		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		if (overworld == null || !overworld.isNight()) {
			return;
		}

		if (countActive(overworld) >= MAX_ACTIVE) {
			return;
		}
		if (ThreadLocalRandom.current().nextDouble() >= SPAWN_CHANCE) {
			return;
		}

		ServerPlayer anchor = pickOverworldPlayer(overworld);
		if (anchor == null) {
			return;
		}
		SkyPainterSpawnManager.trySpawnForPlayer(overworld, anchor);
	}

	private static boolean isAfterSeraphim(MinecraftServer server) {
		return WorldInternalConfig.hasBoundedcowCollision(server)
				&& WorldInternalConfig.getFinaleState(server) == FinaleState.DONE;
	}

	private static int countActive(ServerLevel level) {
		return level.getEntities(EntityTypeTest.forClass(SkyPainterEntity.class), e -> !e.isRemoved()).size();
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
