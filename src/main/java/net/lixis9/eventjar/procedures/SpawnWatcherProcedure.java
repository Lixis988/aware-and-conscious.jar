package net.lixis9.eventjar.procedures;

import net.lixis.outofbound.entity.OverworldStalkerSpawnUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.lixis9.eventjar.entity.WatcherEntity;
import net.lixis9.eventjar.init.EventjarModEntities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpawnWatcherProcedure {

	private static final double MIN_DISTANCE = 48.0;
	private static final double MAX_DISTANCE = 80.0;
	private static final int MAX_ATTEMPTS = 12;

	public static void execute(LevelAccessor world, Player player) {
		if (player == null || !(world instanceof ServerLevel serverWorld)) {
			return;
		}

		List<WatcherEntity> existing = collectLoadedWatchers(serverWorld);
		if (!existing.isEmpty()) {
			enforceSingleWatcher(existing, player);
			return;
		}

		EntityType<?> type = EventjarModEntities.WATCHER.get();
		if (type == null) {
			return;
		}

		for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
			double dist = MIN_DISTANCE + serverWorld.random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
			double angle = serverWorld.random.nextDouble() * Math.PI * 2.0;
			int blockX = Mth.floor(player.getX() + Math.cos(angle) * dist);
			int blockZ = Mth.floor(player.getZ() + Math.sin(angle) * dist);

			int topY = serverWorld.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ);
			int minY = serverWorld.getMinBuildHeight();
			int maxY = serverWorld.getMaxBuildHeight();
			if (topY < minY) {
				topY = minY;
			}
			if (topY > maxY) {
				topY = maxY;
			}

			BlockPos ground = new BlockPos(blockX, topY, blockZ);
			BlockPos feet = ground.above();
			if (!serverWorld.isLoaded(feet)) {
				continue;
			}
			if (isLeafOrLog(serverWorld.getBlockState(feet)) || isLeafOrLog(serverWorld.getBlockState(ground))) {
				continue;
			}
			if (!OverworldStalkerSpawnUtil.isValidSpawnSpace(serverWorld, ground, feet, 2)) {
				continue;
			}

			type.spawn(serverWorld, ItemStack.EMPTY, player, feet, MobSpawnType.EVENT, true, false);
			return;
		}
	}

	private static boolean isLeafOrLog(BlockState state) {
		return state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS);
	}

	private static List<WatcherEntity> collectLoadedWatchers(ServerLevel serverWorld) {
		List<WatcherEntity> result = new ArrayList<>();
		for (var entity : serverWorld.getAllEntities()) {
			if (entity instanceof WatcherEntity watcher && watcher.isAlive()) {
				result.add(watcher);
			}
		}
		return result;
	}

	private static void enforceSingleWatcher(List<WatcherEntity> existing, Player player) {
		WatcherEntity keep = existing.stream()
				.max(Comparator.comparingDouble(w -> w.distanceToSqr(player)))
				.orElse(null);
		for (WatcherEntity watcher : existing) {
			if (watcher != keep) {
				watcher.discard();
			}
		}
	}

	public static void cullExtras(ServerLevel serverWorld, Player player) {
		List<WatcherEntity> existing = collectLoadedWatchers(serverWorld);
		if (existing.size() > 1) {
			enforceSingleWatcher(existing, player);
		}
	}
}
