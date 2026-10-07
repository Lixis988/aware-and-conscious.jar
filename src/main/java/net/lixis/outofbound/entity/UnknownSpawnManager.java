package net.lixis.outofbound.entity;

import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class UnknownSpawnManager {

	private static final int POSITION_ATTEMPTS = 12;
	private static final int DISTANCE_JITTER = 8;
	private static final int CHUNK_DISTANCE = 8;

	private UnknownSpawnManager() {
	}

	public static boolean trySpawn(ServerLevel overworld) {
		return trySpawnForPlayer(overworld, null);
	}

	public static boolean trySpawnForPlayer(ServerLevel overworld, @Nullable ServerPlayer target) {
		MinecraftServer server = overworld.getServer();
		if (server == null) {
			return false;
		}
		if (overworld.dimension() != Level.OVERWORLD) {
			return false;
		}
		if (countManaged(overworld) > 0) {
			return false;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		ServerPlayer anchorPlayer = target;
		if (anchorPlayer == null || !anchorPlayer.isAlive() || anchorPlayer.isSpectator()
				|| anchorPlayer.level().dimension() != Level.OVERWORLD) {
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
				return false;
			}
			anchorPlayer = candidates.get(random.nextInt(candidates.size()));
		}

		SpawnCandidate candidate = findSpawnPosition(overworld, anchorPlayer, random);
		if (candidate == null) {
			return false;
		}

		UnknownEntity entity = OutofboundExtraEntities.UNKNOWN.get().create(overworld);
		if (entity == null) {
			return false;
		}

		entity.moveTo(candidate.position().x, candidate.position().y, candidate.position().z, candidate.yaw(), 0.0F);
		entity.getPersistentData().putBoolean(UnknownEntity.MANAGED_TAG, true);
		overworld.addFreshEntity(entity);
		return true;
	}

	private static int countManaged(ServerLevel level) {
		return level.getEntitiesOfClass(UnknownEntity.class,
				new AABB(-3.0E7, -2048.0D, -3.0E7, 3.0E7, 2048.0D, 3.0E7),
				mob -> mob.getPersistentData().getBoolean(UnknownEntity.MANAGED_TAG) && !mob.isRemoved()).size();
	}

	@Nullable
	private static SpawnCandidate findSpawnPosition(ServerLevel level, ServerPlayer player, ThreadLocalRandom random) {
		double baseDistance = CHUNK_DISTANCE * 16.0D;

		for (int attempt = 0; attempt < POSITION_ATTEMPTS; attempt++) {
			double angle = random.nextDouble() * Math.PI * 2.0D;
			double distance = baseDistance + random.nextInt(-DISTANCE_JITTER, DISTANCE_JITTER + 1);
			double x = player.getX() + Math.cos(angle) * distance;
			double z = player.getZ() + Math.sin(angle) * distance;
			int blockX = Mth.floor(x);
			int blockZ = Mth.floor(z);
			if (!level.hasChunk(blockX >> 4, blockZ >> 4)) {
				continue;
			}

			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ);
			BlockPos ground = new BlockPos(blockX, y, blockZ);
			BlockPos feet = ground.above();
			BlockPos head = feet.above();
			BlockPos top = head.above();
			if (!isValidSpawnSpace(level, ground, feet, head, top)) {
				continue;
			}

			float yaw = yawToward(player, feet);
			return new SpawnCandidate(new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D), yaw);
		}
		return null;
	}

	private static boolean isValidSpawnSpace(ServerLevel level, BlockPos ground, BlockPos feet, BlockPos head, BlockPos top) {
		BlockState floor = level.getBlockState(ground);
		if (!floor.isSolidRender(level, ground)) {
			return false;
		}
		return level.getBlockState(feet).isAir()
				&& level.getBlockState(head).isAir()
				&& level.getBlockState(top).isAir();
	}

	private static float yawToward(ServerPlayer player, BlockPos spawnPos) {
		Vec3 delta = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
		return (float) (Mth.atan2(-delta.x, delta.z) * (180.0D / Math.PI));
	}

	private record SpawnCandidate(Vec3 position, float yaw) {
	}
}
