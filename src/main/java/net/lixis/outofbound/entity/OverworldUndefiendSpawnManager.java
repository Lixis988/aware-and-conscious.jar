package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.init.OutofboundModEntities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class OverworldUndefiendSpawnManager {

	private OverworldUndefiendSpawnManager() {
	}

	public static boolean trySpawn(ServerLevel overworld) {
		return trySpawnForPlayer(overworld, null);
	}

	public static boolean trySpawnForPlayer(ServerLevel overworld, ServerPlayer target) {
		MinecraftServer server = overworld.getServer();
		if (server == null || !MazeConfig.enableOverworldUndefiend) {
			return false;
		}
		if (overworld.dimension() != Level.OVERWORLD) {
			return false;
		}
		if (countManaged(overworld) > 0) {
			return false;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		ServerPlayer anchorPlayer = resolveAnchor(server, target, random);
		if (anchorPlayer == null) {
			return false;
		}

		OverworldUndefiendEntity entity = OutofboundModEntities.OVERWORLD_UNDEFIEND.get().create(overworld);
		if (entity == null) {
			return false;
		}
		entity.refreshDimensions();
		int headroom = Math.max(2, Mth.ceil(entity.getBbHeight()));

		OverworldStalkerSpawnUtil.SpawnCandidate candidate = OverworldStalkerSpawnUtil.findDistantSpawn(
				overworld, anchorPlayer, MazeConfig.overworldUndefiendSpawnChunkDistance, headroom, random);
		if (candidate == null) {
			entity.discard();
			return false;
		}

		entity.moveTo(candidate.position().x, candidate.position().y, candidate.position().z, candidate.yaw(), 0.0F);
		entity.getPersistentData().putBoolean(OverworldUndefiendEntity.MANAGED_TAG, true);
		entity.markSpawned(overworld.getGameTime());
		overworld.addFreshEntity(entity);
		return true;
	}

	@Nullable
	private static ServerPlayer resolveAnchor(MinecraftServer server, @Nullable ServerPlayer target, ThreadLocalRandom random) {
		if (target != null && target.isAlive() && !target.isSpectator()
				&& target.level().dimension() == Level.OVERWORLD) {
			return target;
		}
		List<ServerPlayer> candidates = new ArrayList<>();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
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
		return candidates.get(random.nextInt(candidates.size()));
	}

	private static int countManaged(ServerLevel level) {
		return level.getEntitiesOfClass(OverworldUndefiendEntity.class,
				new AABB(-3.0E7, -2048.0D, -3.0E7, 3.0E7, 2048.0D, 3.0E7),
				mob -> mob.getPersistentData().getBoolean(OverworldUndefiendEntity.MANAGED_TAG) && !mob.isRemoved()).size();
	}
}
