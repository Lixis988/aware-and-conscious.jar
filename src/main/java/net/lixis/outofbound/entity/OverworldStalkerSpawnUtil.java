package net.lixis.outofbound.entity;

import net.lixis.outofbound.util.PlayerLookUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public final class OverworldStalkerSpawnUtil {

	private static final int DEFAULT_ATTEMPTS = 32;
	private static final int DISTANCE_JITTER = 6;
	private static final int VERTICAL_SCAN = 8;
	private static final double FOV_MIN_DOT = 0.55D;
	private static final double TOO_CLOSE_BLOCKS = 24.0D;
	private static final int RELOCATE_LIFESPAN_EXTENSION = 1200;

	private OverworldStalkerSpawnUtil() {
	}

	public static int effectiveChunkDistance(ServerLevel level, int configuredChunks) {
		int configured = Math.max(3, configuredChunks);
		MinecraftServer server = level.getServer();
		if (server == null) {
			return configured;
		}
		int simulation = Math.max(4, server.getPlayerList().getSimulationDistance());
		return Math.min(configured, Math.max(3, simulation - 1));
	}

	public static double effectiveMaxDistanceBlocks(ServerLevel level, int configuredChunks) {
		return effectiveChunkDistance(level, configuredChunks) * 16.0D + DISTANCE_JITTER;
	}

	@Nullable
	public static SpawnCandidate findDistantSpawn(
			ServerLevel level,
			ServerPlayer player,
			int configuredChunkDistance,
			int requiredHeadroom,
			ThreadLocalRandom random) {
		return findDistantSpawn(level, player, configuredChunkDistance, requiredHeadroom, random, false);
	}

	@Nullable
	public static SpawnCandidate findDistantSpawn(
			ServerLevel level,
			ServerPlayer player,
			int configuredChunkDistance,
			int requiredHeadroom,
			ThreadLocalRandom random,
			boolean requireOutsideFov) {
		int chunkDistance = effectiveChunkDistance(level, configuredChunkDistance);
		double baseDistance = chunkDistance * 16.0D;
		int headroom = Math.max(2, requiredHeadroom);

		for (int attempt = 0; attempt < DEFAULT_ATTEMPTS; attempt++) {
			double angle = random.nextDouble() * Math.PI * 2.0D;
			double distance = baseDistance + random.nextInt(-DISTANCE_JITTER, DISTANCE_JITTER + 1);
			distance = Math.max(24.0D, distance);
			double x = player.getX() + Math.cos(angle) * distance;
			double z = player.getZ() + Math.sin(angle) * distance;
			int blockX = Mth.floor(x);
			int blockZ = Mth.floor(z);
			if (!level.hasChunk(blockX >> 4, blockZ >> 4)) {
				continue;
			}

			SpawnCandidate candidate = resolveColumn(level, player, blockX, blockZ, headroom);
			if (candidate != null && passesFovFilter(player, candidate, requireOutsideFov)) {
				return candidate;
			}
		}

		for (int attempt = 0; attempt < 20; attempt++) {
			double angle = random.nextDouble() * Math.PI * 2.0D;
			double distance = 28.0D + random.nextDouble(0.0D, 20.0D);
			int blockX = Mth.floor(player.getX() + Math.cos(angle) * distance);
			int blockZ = Mth.floor(player.getZ() + Math.sin(angle) * distance);
			if (!level.hasChunk(blockX >> 4, blockZ >> 4)) {
				continue;
			}
			SpawnCandidate candidate = resolveColumn(level, player, blockX, blockZ, headroom);
			if (candidate != null && passesFovFilter(player, candidate, requireOutsideFov)) {
				return candidate;
			}
		}
		return null;
	}

	private static boolean passesFovFilter(ServerPlayer player, SpawnCandidate candidate, boolean requireOutsideFov) {
		if (!requireOutsideFov) {
			return true;
		}
		Vec3 lookTarget = candidate.position().add(0.0D, 1.0D, 0.0D);
		return !PlayerLookUtil.isPositionInFov(player, lookTarget, FOV_MIN_DOT);
	}

	@Nullable
	private static SpawnCandidate resolveColumn(
			ServerLevel level,
			ServerPlayer player,
			int blockX,
			int blockZ,
			int requiredHeadroom) {
		int playerY = player.getBlockY();
		for (int dy = 0; dy <= VERTICAL_SCAN; dy++) {
			for (int sign = 0; sign < 2; sign++) {
				int y = playerY + (sign == 0 ? -dy : dy);
				if (dy == 0 && sign == 1) {
					continue;
				}
				BlockPos feet = new BlockPos(blockX, y, blockZ);
				BlockPos ground = feet.below();
				if (isValidSpawnSpace(level, ground, feet, requiredHeadroom)) {
					float yaw = yawToward(player, feet);
					return new SpawnCandidate(new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D), yaw);
				}
			}
		}

		int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ);
		BlockPos ground = new BlockPos(blockX, surfaceY, blockZ);
		BlockPos feet = ground.above();
		if (isValidSpawnSpace(level, ground, feet, requiredHeadroom)) {
			float yaw = yawToward(player, feet);
			return new SpawnCandidate(new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D), yaw);
		}
		return null;
	}

	public static boolean isValidSpawnSpace(ServerLevel level, BlockPos ground, BlockPos feet, BlockPos head) {
		return isValidSpawnSpace(level, ground, feet, 2);
	}

	public static boolean isValidSpawnSpace(ServerLevel level, BlockPos ground, BlockPos feet, int requiredHeadroom) {
		BlockState floor = level.getBlockState(ground);
		if (!floor.isSolidRender(level, ground)) {
			return false;
		}
		int headroom = Math.max(1, requiredHeadroom);
		for (int offset = 0; offset < headroom; offset++) {
			if (!level.getBlockState(feet.above(offset)).isAir()) {
				return false;
			}
		}
		return true;
	}

	public static boolean shouldRelocateDormant(
			ServerLevel level,
			Entity stalker,
			ServerPlayer player,
			int configuredChunkDistance) {
		double distance = stalker.distanceTo(player);
		double maxRing = effectiveMaxDistanceBlocks(level, configuredChunkDistance);
		if (distance > maxRing) {
			return true;
		}
		if (distance < TOO_CLOSE_BLOCKS
				&& !PlayerLookUtil.isPositionInFov(player, stalker.position().add(0.0D, stalker.getBbHeight() * 0.5D, 0.0D), FOV_MIN_DOT)) {
			return true;
		}
		return !isWithinSimulation(level, stalker, player);
	}

	public static boolean isWithinSimulation(ServerLevel level, Entity entity, ServerPlayer player) {
		if (!level.hasChunkAt(entity.blockPosition())) {
			return false;
		}
		MinecraftServer server = level.getServer();
		if (server == null) {
			return false;
		}
		int range = server.getPlayerList().getSimulationDistance();
		int dx = Math.abs(entity.chunkPosition().x - player.chunkPosition().x);
		int dz = Math.abs(entity.chunkPosition().z - player.chunkPosition().z);
		return dx <= range && dz <= range;
	}

	public static int relocateLifespanExtensionTicks() {
		return RELOCATE_LIFESPAN_EXTENSION;
	}

	public static float yawToward(ServerPlayer player, BlockPos spawnPos) {
		return yawToward(player, spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
	}

	public static float yawToward(ServerPlayer player, double x, double y, double z) {
		Vec3 delta = player.position().subtract(x, y, z);
		return (float) (Mth.atan2(-delta.x, delta.z) * (180.0D / Math.PI));
	}

	public record SpawnCandidate(Vec3 position, float yaw) {
	}
}
