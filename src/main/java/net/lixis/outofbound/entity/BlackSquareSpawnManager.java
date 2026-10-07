package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazeFloors;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis.outofbound.util.PlayerLookUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class BlackSquareSpawnManager {

	private static final int DIMENSION_INTERVAL = 4;
	private static final double FOV_MIN_DOT = 0.55D;
	private static final double MIN_SPAWN_DISTANCE = 14.0D;
	private static final double MAX_SPAWN_DISTANCE = 40.0D;
	private static final int RELOCATE_INTERVAL = 20;
	private static final double RELOCATE_DISTANCE = 48.0D;

	private BlackSquareSpawnManager() {
	}

	public static boolean eligibleForIndex(long index) {
		if (index <= 0L) {
			return false;
		}
		long h = MazeDimensions.seedForIndex(index);
		return Math.floorMod(h >>> 32, DIMENSION_INTERVAL) == 0L;
	}

	public static void trySpawnForPlayer(ServerLevel level, ServerPlayer player) {
		if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
			return;
		}

		long index = MazeDimensions.indexFromLocation(level.dimension().location());
		if (!eligibleForIndex(index)) {
			return;
		}

		MazeBlackSquareSpawnData spawnData = MazeBlackSquareSpawnData.get(level);
		if (spawnData.hasSpawned()) {
			return;
		}
		if (!level.getEntitiesOfClass(BlackSquareEntity.class, player.getBoundingBox().inflate(160.0D),
				entity -> !entity.isRemoved()).isEmpty()) {
			return;
		}

		if (spawnNearPlayer(level, player)) {
			spawnData.markSpawned();
		}
	}

	public static void tickRelocations(MinecraftServer server) {
		if (server.getTickCount() % RELOCATE_INTERVAL != 0) {
			return;
		}

		for (ServerLevel level : server.getAllLevels()) {
			if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
				continue;
			}

			for (Entity entity : level.getAllEntities()) {
				if (!(entity instanceof BlackSquareEntity square) || !square.isAlive() || square.isRemoved()) {
					continue;
				}
				if (!square.getPersistentData().getBoolean(BlackSquareEntity.MANAGED_TAG)) {
					continue;
				}

				ServerPlayer player = findNearestPlayer(level, square);
				if (player == null) {
					continue;
				}

				if (shouldRelocate(level, square, player)) {
					relocateSquare(square, player);
				}
			}
		}
	}

	private static void relocateSquare(BlackSquareEntity square, ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		square.discard();
		spawnNearPlayer(level, player);
	}

	private static boolean shouldRelocate(ServerLevel level, Mob chaser, ServerPlayer player) {
		double chaseRange = chaser.getAttributeValue(Attributes.FOLLOW_RANGE);
		if (chaseRange <= 0.0D) {
			chaseRange = 128.0D;
		}

		double distance = chaser.distanceTo(player);
		if (distance > chaseRange) {
			return true;
		}

		if (!isTickingNearPlayer(level, chaser, player)) {
			return true;
		}

		if (distance > RELOCATE_DISTANCE) {
			return true;
		}

		if (!MazeFloors.sameFloor(level, chaser, player)) {
			return true;
		}

		if (distance > MIN_SPAWN_DISTANCE && isStuck(chaser)) {
			return true;
		}

		return false;
	}

	private static boolean isStuck(Mob chaser) {
		var tag = chaser.getPersistentData();
		double lastX = tag.getDouble("outofbound_black_square_lx");
		double lastY = tag.getDouble("outofbound_black_square_ly");
		double lastZ = tag.getDouble("outofbound_black_square_lz");
		double moved = chaser.position().distanceTo(new Vec3(lastX, lastY, lastZ));

		tag.putDouble("outofbound_black_square_lx", chaser.getX());
		tag.putDouble("outofbound_black_square_ly", chaser.getY());
		tag.putDouble("outofbound_black_square_lz", chaser.getZ());

		if (moved >= 0.35D) {
			tag.putInt("outofbound_black_square_stuck", 0);
			return false;
		}

		int stuckTicks = tag.getInt("outofbound_black_square_stuck") + RELOCATE_INTERVAL;
		tag.putInt("outofbound_black_square_stuck", stuckTicks);
		return stuckTicks >= 160;
	}

	private static boolean isTickingNearPlayer(ServerLevel level, Mob chaser, ServerPlayer player) {
		if (!level.hasChunkAt(chaser.blockPosition())) {
			return false;
		}

		int range = level.getServer().getPlayerList().getViewDistance();
		int dx = Math.abs(chaser.chunkPosition().x - player.chunkPosition().x);
		int dz = Math.abs(chaser.chunkPosition().z - player.chunkPosition().z);
		return dx <= range && dz <= range;
	}

	@Nullable
	private static ServerPlayer findNearestPlayer(ServerLevel level, Mob chaser) {
		ServerPlayer nearest = null;
		double nearestSq = Double.MAX_VALUE;

		for (ServerPlayer player : level.players()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			double distSq = chaser.distanceToSqr(player);
			if (distSq < nearestSq) {
				nearestSq = distSq;
				nearest = player;
			}
		}

		return nearest;
	}

	private static boolean spawnNearPlayer(ServerLevel level, ServerPlayer player) {
		BlackSquareEntity entity = OutofboundExtraEntities.BLACK_SQUARE.get().create(level);
		if (entity == null) {
			return false;
		}

		int requiredHeadroom = Math.max(1, Mth.ceil(entity.getBbHeight()));
		Optional<Vec3> spawn = findHiddenFovSpawnPosition(level, player, requiredHeadroom);
		if (spawn.isEmpty()) {
			spawn = findFallbackSpawnPosition(level, player, requiredHeadroom);
		}
		if (spawn.isEmpty()) {
			entity.discard();
			return false;
		}

		Vec3 pos = spawn.get();
		entity.moveTo(pos.x, pos.y, pos.z, player.getYRot() + 180.0F, 0.0F);
		entity.getPersistentData().putBoolean(BlackSquareEntity.MANAGED_TAG, true);
		entity.getPersistentData().putInt("outofbound_black_square_stuck", 0);
		level.addFreshEntity(entity);
		return true;
	}

	private static Optional<Vec3> findHiddenFovSpawnPosition(ServerLevel level, ServerPlayer player, int requiredHeadroom) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Vec3 eye = player.getEyePosition(1.0F);
		float baseYaw = player.getYRot();
		float basePitch = player.getXRot();
		int playerFloorFeetY = MazeFloors.floorFeetYForEntity(level, player);

		for (int attempt = 0; attempt < 96; attempt++) {
			double distance = random.nextDouble(MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE);
			float yaw = baseYaw + random.nextFloat(-32.0F, 32.0F);
			float pitch = basePitch + random.nextFloat(-8.0F, 8.0F);
			Vec3 direction = Vec3.directionFromRotation(pitch, yaw);
			Vec3 candidate = eye.add(direction.scale(distance));

			Optional<Vec3> spawn = resolveSpawnAt(level, player, candidate.x, candidate.z, playerFloorFeetY, requiredHeadroom, true);
			if (spawn.isPresent()) {
				return spawn;
			}
		}

		return Optional.empty();
	}

	private static Optional<Vec3> findFallbackSpawnPosition(ServerLevel level, ServerPlayer player, int requiredHeadroom) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int playerFloorFeetY = MazeFloors.floorFeetYForEntity(level, player);

		for (int attempt = 0; attempt < 64; attempt++) {
			double angle = random.nextDouble(0.0D, Math.PI * 2.0D);
			double distance = random.nextDouble(MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE);
			double x = player.getX() + Math.cos(angle) * distance;
			double z = player.getZ() + Math.sin(angle) * distance;

			Optional<Vec3> spawn = resolveSpawnAt(level, player, x, z, playerFloorFeetY, requiredHeadroom, false);
			if (spawn.isPresent()) {
				return spawn;
			}
		}

		return Optional.empty();
	}

	private static Optional<Vec3> resolveSpawnAt(
			ServerLevel level,
			ServerPlayer player,
			double candidateX,
			double candidateZ,
			int floorFeetY,
			int requiredHeadroom,
			boolean requireHidden) {

		BlockPos feet = BlockPos.containing(candidateX, floorFeetY, candidateZ);
		level.getChunk(feet.getX() >> 4, feet.getZ() >> 4);

		if (!isValidMazeSpawnColumn(level, feet, requiredHeadroom)) {
			return Optional.empty();
		}

		Vec3 spawn = new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D);
		if (requireHidden) {
			Vec3 lookTarget = spawn.add(0.0D, 0.5D, 0.0D);
			if (PlayerLookUtil.isPositionInFov(player, lookTarget, FOV_MIN_DOT)) {
				return Optional.empty();
			}
			if (hasClearLine(level, player, lookTarget)) {
				return Optional.empty();
			}
		}

		return Optional.of(spawn);
	}

	private static boolean isValidMazeSpawnColumn(ServerLevel level, BlockPos feet, int requiredHeadroom) {
		BlockPos floor = feet.below();
		if (!level.getBlockState(floor).isSolidRender(level, floor)) {
			return false;
		}
		for (int i = 0; i < requiredHeadroom; i++) {
			BlockPos check = feet.above(i);
			if (!level.getBlockState(check).isAir()) {
				return false;
			}
		}
		return true;
	}

	private static boolean hasClearLine(Level level, ServerPlayer player, Vec3 target) {
		BlockHitResult hit = level.clip(new ClipContext(
				player.getEyePosition(1.0F),
				target,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				player));
		return hit.getType() == HitResult.Type.MISS || hit.getLocation().distanceToSqr(target) < 4.0D;
	}
}
