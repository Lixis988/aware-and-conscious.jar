package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazeFloors;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public final class NotexturemanSpawnManager {

	private static final int MIN_SPAWN_DISTANCE = 16;
	private static final int MAX_SPAWN_DISTANCE = 28;
	private static final int POSITION_ATTEMPTS = 40;
	private static final int[] NEIGHBOR_OFFSETS = {0, 1, -1, 2, -2};

	private NotexturemanSpawnManager() {
	}

	public static void trySpawn(ServerLevel level, ServerPlayer player) {
		if (!MazeConfig.enableNotextureman) {
			return;
		}
		if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
			return;
		}
		if (!player.isAlive() || player.isSpectator()) {
			return;
		}
		if (hasManagedNearby(level, player)) {
			return;
		}

		Vec3 spawnPos = findSpawnPosition(level, player);
		if (spawnPos == null) {
			return;
		}

		NotexturemanEntity entity = OutofboundExtraEntities.NOTEXTUREMAN.get().create(level);
		if (entity == null) {
			return;
		}

		entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot() + 180.0F, 0.0F);
		entity.getPersistentData().putBoolean(NotexturemanEntity.MANAGED_TAG, true);
		entity.setTargetPlayer(player.getUUID());
		level.addFreshEntity(entity);
	}

	private static boolean hasManagedNearby(ServerLevel level, ServerPlayer player) {
		AABB box = player.getBoundingBox().inflate(64.0D);
		return !level.getEntitiesOfClass(NotexturemanEntity.class, box,
				mob -> mob.getPersistentData().getBoolean(NotexturemanEntity.MANAGED_TAG) && !mob.isRemoved()).isEmpty();
	}

	@Nullable
	private static Vec3 findSpawnPosition(ServerLevel level, ServerPlayer player) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int feetY = MazeFloors.floorFeetYForEntity(level, player);

		for (int attempt = 0; attempt < POSITION_ATTEMPTS; attempt++) {
			double angle = random.nextDouble() * Math.PI * 2.0D;
			double distance = random.nextDouble(MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE + 1.0D);
			int blockX = Mth.floor(player.getX() + Math.cos(angle) * distance);
			int blockZ = Mth.floor(player.getZ() + Math.sin(angle) * distance);

			Vec3 found = tryColumnNeighborhood(level, blockX, blockZ, feetY);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	@Nullable
	private static Vec3 tryColumnNeighborhood(ServerLevel level, int blockX, int blockZ, int feetY) {
		for (int dx : NEIGHBOR_OFFSETS) {
			for (int dz : NEIGHBOR_OFFSETS) {
				int x = blockX + dx;
				int z = blockZ + dz;
				if (!level.hasChunk(x >> 4, z >> 4)) {
					continue;
				}

				BlockPos ground = new BlockPos(x, feetY - 1, z);
				BlockPos feet = new BlockPos(x, feetY, z);
				BlockPos head = feet.above();
				if (!isValidSpawnSpace(level, ground, feet, head)) {
					continue;
				}

				return new Vec3(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D);
			}
		}
		return null;
	}

	private static boolean isValidSpawnSpace(ServerLevel level, BlockPos ground, BlockPos feet, BlockPos head) {
		BlockState floor = level.getBlockState(ground);
		if (!floor.isSolidRender(level, ground)) {
			return false;
		}
		return level.getBlockState(feet).isAir() && level.getBlockState(head).isAir();
	}
}
