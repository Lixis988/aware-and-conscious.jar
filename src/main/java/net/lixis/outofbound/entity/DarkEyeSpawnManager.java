package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazeFloors;
import net.lixis.outofbound.dimension.gen.MazeStyle;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis.outofbound.util.DarknessUtil;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class DarkEyeSpawnManager {

	private static final double MIN_SPAWN_DISTANCE_SQ = 4.0D * 4.0D;
	private static final double MAX_SPAWN_DISTANCE_SQ = 24.0D * 24.0D;
	private static final int MAX_EYES_PER_PLAYER = 5;

	private static final float EYE_WIDTH = 0.85F;
	private static final float EYE_HEIGHT = 0.85F;

	private DarkEyeSpawnManager() {
	}

	public static boolean isEyeSpawnDimension(MinecraftServer server, ServerLevel level) {
		if (MazeDimensions.isMazeDimension(level.dimension().location())) {
			return true;
		}
		return level.dimension() == Level.OVERWORLD && WorldInternalConfig.hasBoundedcowCollision(server);
	}

	public static void trySpawnForPlayer(ServerLevel level, ServerPlayer player) {
		MinecraftServer server = level.getServer();
		if (!isEyeSpawnDimension(server, level)) {
			return;
		}

		long nearbyEyes = level.getEntitiesOfClass(
				DarkEyeEntity.class,
				player.getBoundingBox().inflate(48.0D),
				eye -> eye.getPersistentData().getBoolean(DarkEyeEntity.MANAGED_TAG) && !eye.isRemoved()
		).size();
		if (nearbyEyes >= MAX_EYES_PER_PLAYER) {
			return;
		}

		Optional<SpawnCandidate> spawn = findDarkSpawnPosition(level, player);
		if (spawn.isEmpty()) {
			return;
		}

		DarkEyeEntity eye = OutofboundExtraEntities.DARK_EYE.get().create(level);
		if (eye == null) {
			return;
		}

		SpawnCandidate candidate = spawn.get();
		eye.moveTo(candidate.position().x, candidate.position().y, candidate.position().z, 0.0F, 0.0F);
		eye.getPersistentData().putBoolean(DarkEyeEntity.MANAGED_TAG, true);
		eye.getPersistentData().putInt(DarkEyeEntity.FLOOR_TAG, candidate.floorIndex());
		level.addFreshEntity(eye);
	}

	public static void tickDespawns(ServerLevel level) {
		MinecraftServer server = level.getServer();
		for (Entity entity : level.getAllEntities()) {
			if (!(entity instanceof DarkEyeEntity eye)) {
				continue;
			}
			if (!eye.getPersistentData().getBoolean(DarkEyeEntity.MANAGED_TAG) || eye.isRemoved()) {
				continue;
			}

			if (!isEyeSpawnDimension(server, level)) {
				eye.discard();
				continue;
			}

			BlockPos lightCheck = BlockPos.containing(eye.getX(), eye.getY(), eye.getZ());
			if (!DarknessUtil.isDarkEnough(level, lightCheck)) {
				eye.discard();
				continue;
			}

			if (!hasEyeClearance(level, eye.position())) {
				eye.discard();
				continue;
			}

			ServerPlayer nearest = findNearestPlayer(level, eye);
			if (nearest == null || eye.distanceTo(nearest) > 64.0D) {
				eye.discard();
				continue;
			}

			if (MazeDimensions.isMazeDimension(level.dimension().location())) {
				int eyeFloor = eye.getPersistentData().getInt(DarkEyeEntity.FLOOR_TAG);
				int playerFloor = MazeFloors.floorIndexForEntity(level, nearest);
				if (eyeFloor >= 0 && eyeFloor != playerFloor) {
					eye.discard();
				}
			}
		}
	}

	private static Optional<SpawnCandidate> findDarkSpawnPosition(ServerLevel level, ServerPlayer player) {
		if (MazeDimensions.isMazeDimension(level.dimension().location())) {
			return findMazeDarkSpawn(level, player);
		}
		return findOverworldDarkSpawn(level, player);
	}

	private static Optional<SpawnCandidate> findMazeDarkSpawn(ServerLevel level, ServerPlayer player) {
		int floorIndex = MazeFloors.floorIndexForEntity(level, player);
		BlockPos origin = player.blockPosition();
		ThreadLocalRandom random = ThreadLocalRandom.current();

		for (int attempt = 0; attempt < 128; attempt++) {
			int dx = random.nextInt(-24, 25);
			int dz = random.nextInt(-24, 25);
			double distSq = dx * (double) dx + dz * (double) dz;
			if (distSq < MIN_SPAWN_DISTANCE_SQ || distSq > MAX_SPAWN_DISTANCE_SQ) {
				continue;
			}

			Optional<SpawnCandidate> spawn = tryMazeSpawnAt(level, origin.getX() + dx, origin.getZ() + dz, floorIndex);
			if (spawn.isPresent()) {
				return spawn;
			}
		}

		List<BlockPos> ringCandidates = new ArrayList<>();
		for (int ring = 4; ring <= 24; ring++) {
			for (int dx = -ring; dx <= ring; dx++) {
				for (int dz = -ring; dz <= ring; dz++) {
					if (Math.abs(dx) != ring && Math.abs(dz) != ring) {
						continue;
					}
					double distSq = dx * (double) dx + dz * (double) dz;
					if (distSq < MIN_SPAWN_DISTANCE_SQ || distSq > MAX_SPAWN_DISTANCE_SQ) {
						continue;
					}
					ringCandidates.add(new BlockPos(origin.getX() + dx, 0, origin.getZ() + dz));
				}
			}
		}
		Collections.shuffle(ringCandidates, random);
		for (BlockPos candidate : ringCandidates) {
			Optional<SpawnCandidate> spawn = tryMazeSpawnAt(level, candidate.getX(), candidate.getZ(), floorIndex);
			if (spawn.isPresent()) {
				return spawn;
			}
		}

		return Optional.empty();
	}

	private static Optional<SpawnCandidate> tryMazeSpawnAt(
			ServerLevel level,
			int blockX,
			int blockZ,
			int floorIndex) {

		if (!isOpenMazeColumn(level, blockX, blockZ, floorIndex)) {
			return Optional.empty();
		}

		DimensionTheme theme = themeFor(level);
		int feetY = MazeFloors.feetYForFloor(level, floorIndex);
		BlockPos feet = new BlockPos(blockX, feetY, blockZ);
		if (!level.hasChunkAt(feet)) {
			return Optional.empty();
		}
		level.getChunk(feet.getX() >> 4, feet.getZ() >> 4);

		if (!isSolidFloor(level, feet.below()) || !level.getBlockState(feet).isAir()) {
			return Optional.empty();
		}

		int topAirY = feetY + theme.ceilingHeight - 2;
		if (topAirY < feetY || !level.getBlockState(new BlockPos(blockX, topAirY, blockZ)).isAir()) {
			return Optional.empty();
		}

		double centerY = (feetY + topAirY + 1.0D) * 0.5D;
		Vec3 center = new Vec3(blockX + 0.5D, centerY, blockZ + 0.5D);
		return finalizeSpawn(level, center, floorIndex);
	}

	private static Optional<SpawnCandidate> findOverworldDarkSpawn(ServerLevel level, ServerPlayer player) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		BlockPos origin = player.blockPosition();

		for (int attempt = 0; attempt < 128; attempt++) {
			int dx = random.nextInt(-24, 25);
			int dz = random.nextInt(-24, 25);
			double distSq = dx * (double) dx + dz * (double) dz;
			if (distSq < MIN_SPAWN_DISTANCE_SQ || distSq > MAX_SPAWN_DISTANCE_SQ) {
				continue;
			}

			BlockPos surface = level.getHeightmapPos(
					Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
					new BlockPos(origin.getX() + dx, 0, origin.getZ() + dz));
			BlockPos feet = surface.above();
			if (!level.getBlockState(feet).isAir()) {
				continue;
			}

			double centerY = feet.getY() + 1.35D;
			Vec3 center = new Vec3(feet.getX() + 0.5D, centerY, feet.getZ() + 0.5D);
			Optional<SpawnCandidate> spawn = finalizeSpawn(level, center, -1);
			if (spawn.isPresent()) {
				return spawn;
			}
		}

		return Optional.empty();
	}

	private static Optional<SpawnCandidate> finalizeSpawn(ServerLevel level, Vec3 center, int floorIndex) {
		if (!DarknessUtil.isDarkEnough(level, BlockPos.containing(center))) {
			return Optional.empty();
		}
		if (!hasEyeClearance(level, center)) {
			return Optional.empty();
		}
		return Optional.of(new SpawnCandidate(center, floorIndex));
	}

	private static boolean hasEyeClearance(ServerLevel level, Vec3 center) {
		double halfW = EYE_WIDTH * 0.5D;
		double halfH = EYE_HEIGHT * 0.5D;
		AABB box = new AABB(
				center.x - halfW, center.y - halfH, center.z - halfW,
				center.x + halfW, center.y + halfH, center.z + halfW
		);
		BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
		BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
		for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
			if (!level.getBlockState(pos).isAir()) {
				return false;
			}
		}
		return true;
	}

	private static boolean isOpenMazeColumn(ServerLevel level, int x, int z, int floorIndex) {
		DimensionTheme theme = themeFor(level);
		long index = Math.max(0L, MazeDimensions.indexFromLocation(level.dimension().location()));
		long seed = MazeDimensions.seedForIndex(index);
		return theme.composer.column(seed, theme, x, z, floorIndex) == MazeStyle.ColumnType.OPEN;
	}

	private static boolean isSolidFloor(ServerLevel level, BlockPos floor) {
		BlockState state = level.getBlockState(floor);
		return !state.isAir() && state.isSolidRender(level, floor);
	}

	private static DimensionTheme themeFor(ServerLevel level) {
		long index = Math.max(0L, MazeDimensions.indexFromLocation(level.dimension().location()));
		return DimensionTheme.forIndex(index);
	}

	private static ServerPlayer findNearestPlayer(ServerLevel level, DarkEyeEntity eye) {
		ServerPlayer nearest = null;
		double nearestSq = Double.MAX_VALUE;

		for (ServerPlayer player : level.players()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			double distSq = eye.distanceToSqr(player);
			if (distSq < nearestSq) {
				nearestSq = distSq;
				nearest = player;
			}
		}

		return nearest;
	}

	private record SpawnCandidate(Vec3 position, int floorIndex) {
	}
}
