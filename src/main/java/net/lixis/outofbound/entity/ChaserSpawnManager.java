package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.dimension.MazeFloors;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.init.OutofboundModEntities;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis.outofbound.util.PlayerLookUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class ChaserSpawnManager {

	private static final double FOV_MIN_DOT = 0.55D;
	private static final int RELOCATE_INTERVAL = 20;
	private static final String MANAGED_TAG = "outofbound_chaser_managed";
	private static final int[] NEIGHBOR_OFFSETS = {0, 1, -1, 2, -2};

	private ChaserSpawnManager() {
	}

	@Nullable
	public static EntityType<? extends Mob> chaserTypeForIndex(long index) {
		if (index < 0L) {
			return null;
		}
		long h = MazeDimensions.seedForIndex(index);
		int pick = (int) Math.floorMod(h >>> 16, 5L);
		return switch (pick) {
			case 0 -> OutofboundModEntities.UNDEFIEND.get();
			case 1 -> OutofboundExtraEntities.ENTITY1.get();
			case 2 -> OutofboundExtraEntities.ENTITY2.get();
			case 3 -> OutofboundExtraEntities.ENTITY3.get();
			default -> OutofboundExtraEntities.TEETHMAN.get();
		};
	}

	public static void trySpawnForPlayer(ServerLevel level, ServerPlayer player) {
		if (!MazeConfig.enableMazeChasers) {
			return;
		}
		if (!player.isAlive() || player.isSpectator()) {
			return;
		}
		if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
			return;
		}

		long index = MazeDimensions.indexFromLocation(level.dimension().location());
		EntityType<? extends Mob> type = chaserTypeForIndex(index);
		if (type == null) {
			return;
		}

		if (hasManagedChaserNearby(level, player)) {
			return;
		}

		spawnManagedChaserNearPlayer(level, player, type, false);
	}

	private static boolean hasManagedChaserNearby(ServerLevel level, ServerPlayer player) {
		return !level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(160.0D),
				entity -> entity instanceof ChaserEntity
						&& entity.getPersistentData().getBoolean(MANAGED_TAG)
						&& !entity.isRemoved()).isEmpty();
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
				if (!(entity instanceof Mob mob) || !(entity instanceof ChaserEntity)) {
					continue;
				}
				if (!mob.getPersistentData().getBoolean(MANAGED_TAG) || !mob.isAlive() || mob.isRemoved()) {
					continue;
				}

				ServerPlayer player = findNearestPlayer(level, mob);
				if (player == null) {
					continue;
				}

				if (shouldRelocate(level, mob, player)) {
					relocateChaser(mob, player);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void relocateChaser(Mob chaser, ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		EntityType<? extends Mob> type = (EntityType<? extends Mob>) chaser.getType();
		chaser.discard();
		spawnManagedChaserNearPlayer(level, player, type, false);
	}

	private static boolean shouldRelocate(ServerLevel level, Mob chaser, ServerPlayer player) {
		double maxDistance = maxSpawnDistance(level);
		double minDistance = minSpawnDistance(level);

		double distance = chaser.distanceTo(player);
		if (distance > maxDistance) {
			return true;
		}

		if (!isTickingNearPlayer(level, chaser, player)) {
			return true;
		}

		if (!MazeFloors.sameFloor(level, chaser, player)) {
			return true;
		}

		if (distance > minDistance && !ChaserRevealHandler.shouldReveal(player, chaser) && isStuck(chaser)) {
			return true;
		}

		double chaseRange = chaser.getAttributeValue(Attributes.FOLLOW_RANGE);
		if (chaseRange <= 0.0D) {
			chaseRange = 128.0D;
		}
		if (distance > chaseRange) {
			return true;
		}

		return false;
	}

	private static boolean isStuck(Mob chaser) {
		var tag = chaser.getPersistentData();
		double lastX = tag.getDouble("outofbound_chaser_lx");
		double lastY = tag.getDouble("outofbound_chaser_ly");
		double lastZ = tag.getDouble("outofbound_chaser_lz");
		double moved = chaser.position().distanceTo(new Vec3(lastX, lastY, lastZ));

		tag.putDouble("outofbound_chaser_lx", chaser.getX());
		tag.putDouble("outofbound_chaser_ly", chaser.getY());
		tag.putDouble("outofbound_chaser_lz", chaser.getZ());

		if (moved >= 0.35D) {
			tag.putInt("outofbound_chaser_stuck", 0);
			return false;
		}

		int stuckTicks = tag.getInt("outofbound_chaser_stuck") + RELOCATE_INTERVAL;
		tag.putInt("outofbound_chaser_stuck", stuckTicks);
		return stuckTicks >= 160;
	}

	private static boolean isTickingNearPlayer(ServerLevel level, Mob chaser, ServerPlayer player) {
		if (!level.hasChunkAt(chaser.blockPosition())) {
			return false;
		}

		int range = level.getServer().getPlayerList().getSimulationDistance();
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

	private static boolean spawnManagedChaserNearPlayer(
			ServerLevel level,
			ServerPlayer player,
			EntityType<? extends Mob> type,
			boolean revealOnSpawn) {

		if (!UndefiendSpawnBlocker.isAllowedDimension(level)) {
			return false;
		}

		Mob entity = type.create(level);
		if (entity == null) {
			return false;
		}

		int requiredHeadroom = Math.max(1, Mth.ceil(entity.getBbHeight()));
		Optional<Vec3> spawn = findDistantHiddenSpawnPosition(level, player, requiredHeadroom);
		if (spawn.isEmpty()) {
			spawn = findDistantFallbackSpawnPosition(level, player, requiredHeadroom);
		}
		if (spawn.isEmpty()) {
			entity.discard();
			return false;
		}

		Vec3 pos = spawn.get();
		entity.moveTo(pos.x, pos.y, pos.z, player.getYRot() + 180.0F, 0.0F);
		entity.getPersistentData().putBoolean(MANAGED_TAG, true);
		entity.getPersistentData().putInt("outofbound_chaser_stuck", 0);
		if (revealOnSpawn) {
			ChaserRevealHandler.markRevealed(entity);
		} else {
			ChaserRevealHandler.markHidden(entity);
		}
		level.addFreshEntity(entity);
		return true;
	}

	private static double minSpawnDistance(ServerLevel level) {
		double max = maxSpawnDistance(level);
		double configured = MazeConfig.mazeChaserMinDistance;
		return Math.min(configured, Math.max(16.0D, max - 8.0D));
	}

	private static double maxSpawnDistance(ServerLevel level) {
		double configured = Math.max(MazeConfig.mazeChaserMinDistance + 1.0D, MazeConfig.mazeChaserMaxDistance);
		MinecraftServer server = level.getServer();
		if (server == null) {
			return configured;
		}
		int simulation = Math.max(4, server.getPlayerList().getSimulationDistance());
		double simCap = Math.max(24.0D, (simulation - 1) * 16.0D);
		return Math.min(configured, simCap);
	}

	private static Optional<Vec3> findDistantHiddenSpawnPosition(ServerLevel level, ServerPlayer player, int requiredHeadroom) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Vec3 eye = player.getEyePosition(1.0F);
		float baseYaw = player.getYRot();
		float basePitch = player.getXRot();
		int playerFloorFeetY = MazeFloors.floorFeetYForEntity(level, player);
		double minDistance = minSpawnDistance(level);
		double maxDistance = maxSpawnDistance(level);
		int totalAttempts = 96;

		for (int attempt = 0; attempt < totalAttempts; attempt++) {
			double distance = random.nextDouble(minDistance, maxDistance);
			float yawOffset = random.nextBoolean()
					? random.nextFloat(100.0F, 160.0F) * (random.nextBoolean() ? 1.0F : -1.0F)
					: random.nextFloat(-160.0F, -100.0F);
			float yaw = baseYaw + yawOffset;
			float pitch = Mth.clamp(basePitch + random.nextFloat(-12.0F, 12.0F), -30.0F, 30.0F);
			Vec3 direction = Vec3.directionFromRotation(pitch, yaw);
			Vec3 candidate = eye.add(direction.scale(distance));

			boolean requireBlockedLos = attempt < totalAttempts / 2;
			Optional<Vec3> spawn = resolveSpawnNear(level, player, candidate.x, candidate.z, playerFloorFeetY,
					requiredHeadroom, true, requireBlockedLos);
			if (spawn.isPresent()) {
				return spawn;
			}
		}

		return Optional.empty();
	}

	private static Optional<Vec3> findDistantFallbackSpawnPosition(ServerLevel level, ServerPlayer player, int requiredHeadroom) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int playerFloorFeetY = MazeFloors.floorFeetYForEntity(level, player);
		double minDistance = minSpawnDistance(level);
		double maxDistance = maxSpawnDistance(level);

		for (int attempt = 0; attempt < 96; attempt++) {
			double angle = random.nextDouble(0.0D, Math.PI * 2.0D);
			double distance = random.nextDouble(minDistance, maxDistance);
			double x = player.getX() + Math.cos(angle) * distance;
			double z = player.getZ() + Math.sin(angle) * distance;

			Optional<Vec3> spawn = resolveSpawnNear(level, player, x, z, playerFloorFeetY, requiredHeadroom, false, false);
			if (spawn.isPresent()) {
				return spawn;
			}
		}

		return Optional.empty();
	}

	private static Optional<Vec3> resolveSpawnNear(
			ServerLevel level,
			ServerPlayer player,
			double candidateX,
			double candidateZ,
			int floorFeetY,
			int requiredHeadroom,
			boolean requireHidden,
			boolean requireBlockedLos) {

		int baseX = Mth.floor(candidateX);
		int baseZ = Mth.floor(candidateZ);

		for (int dx : NEIGHBOR_OFFSETS) {
			for (int dz : NEIGHBOR_OFFSETS) {
				Optional<Vec3> spawn = resolveSpawnAt(
						level, player, baseX + dx + 0.5D, baseZ + dz + 0.5D,
						floorFeetY, requiredHeadroom, requireHidden, requireBlockedLos);
				if (spawn.isPresent()) {
					return spawn;
				}
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
			boolean requireHidden,
			boolean requireBlockedLos) {

		BlockPos feet = BlockPos.containing(candidateX, floorFeetY, candidateZ);
		if (!level.hasChunk(feet.getX() >> 4, feet.getZ() >> 4)) {
			return Optional.empty();
		}
		level.getChunk(feet.getX() >> 4, feet.getZ() >> 4);

		if (!isValidMazeSpawnColumn(level, feet, requiredHeadroom)) {
			return Optional.empty();
		}

		Vec3 spawn = new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D);
		double distSq = player.distanceToSqr(spawn);
		double minDistance = minSpawnDistance(level);
		if (distSq < minDistance * minDistance) {
			return Optional.empty();
		}

		if (requireHidden) {
			Vec3 lookTarget = spawn.add(0.0D, 1.0D, 0.0D);
			if (PlayerLookUtil.isPositionInFov(player, lookTarget, FOV_MIN_DOT)) {
				return Optional.empty();
			}
			if (requireBlockedLos && hasClearLine(level, player, lookTarget)) {
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
