package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class OverworldStalkerRelocateHandler {

	private static final int RELOCATE_INTERVAL = 40;

	private OverworldStalkerRelocateHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		MinecraftServer server = event.getServer();
		if (server.getTickCount() % RELOCATE_INTERVAL != 0) {
			return;
		}

		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		if (overworld == null) {
			return;
		}

		tickManaged(overworld, OverworldUndefiendEntity.class, OverworldUndefiendEntity.MANAGED_TAG,
				MazeConfig.enableOverworldUndefiend, MazeConfig.overworldUndefiendSpawnChunkDistance, true);
		tickManaged(overworld, OverworldTeethmanEntity.class, OverworldTeethmanEntity.MANAGED_TAG,
				MazeConfig.enableOverworldTeethman, MazeConfig.overworldTeethmanSpawnChunkDistance, false);
	}

	private static <T extends Entity & OverworldStalker> void tickManaged(
			ServerLevel overworld,
			Class<T> type,
			String managedTag,
			boolean enabled,
			int chunkDistance,
			boolean undefiend) {
		if (!enabled) {
			return;
		}

		AABB worldBox = new AABB(-3.0E7, -2048.0D, -3.0E7, 3.0E7, 2048.0D, 3.0E7);
		for (T stalker : overworld.getEntitiesOfClass(type, worldBox,
				entity -> entity.getPersistentData().getBoolean(managedTag)
						&& !entity.isRemoved()
						&& !entity.isActivated())) {
			ServerPlayer player = findNearestOverworldPlayer(overworld, stalker);
			if (player == null) {
				continue;
			}
			if (!OverworldStalkerSpawnUtil.shouldRelocateDormant(overworld, stalker, player, chunkDistance)) {
				continue;
			}
			relocate(overworld, stalker, player, chunkDistance, undefiend);
		}
	}

	private static void relocate(
			ServerLevel overworld,
			Entity stalker,
			ServerPlayer player,
			int chunkDistance,
			boolean undefiend) {
		int headroom = Math.max(2, Mth.ceil(stalker.getBbHeight()));
		OverworldStalkerSpawnUtil.SpawnCandidate candidate = OverworldStalkerSpawnUtil.findDistantSpawn(
				overworld,
				player,
				chunkDistance,
				headroom,
				ThreadLocalRandom.current(),
				true);
		if (candidate == null) {
			return;
		}

		stalker.moveTo(
				candidate.position().x,
				candidate.position().y,
				candidate.position().z,
				candidate.yaw(),
				0.0F);
		stalker.setYHeadRot(candidate.yaw());
		stalker.setYBodyRot(candidate.yaw());

		int extension = OverworldStalkerSpawnUtil.relocateLifespanExtensionTicks();
		long gameTime = overworld.getGameTime();
		if (undefiend && stalker instanceof OverworldUndefiendEntity undefiendEntity) {
			undefiendEntity.extendDormantLifespan(gameTime, extension);
		} else if (!undefiend && stalker instanceof OverworldTeethmanEntity teethmanEntity) {
			teethmanEntity.extendDormantLifespan(gameTime, extension);
		}
	}

	@Nullable
	private static ServerPlayer findNearestOverworldPlayer(ServerLevel overworld, Entity stalker) {
		ServerPlayer nearest = null;
		double nearestSq = Double.MAX_VALUE;
		for (ServerPlayer player : overworld.getServer().getPlayerList().getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			if (player.level().dimension() != Level.OVERWORLD) {
				continue;
			}
			double distSq = stalker.distanceToSqr(player);
			if (distSq < nearestSq) {
				nearestSq = distSq;
				nearest = player;
			}
		}
		return nearest;
	}
}
