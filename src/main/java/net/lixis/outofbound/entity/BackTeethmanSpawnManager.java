package net.lixis.outofbound.entity;

import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class BackTeethmanSpawnManager {

	private static final double BEHIND_DISTANCE = 4.5D;
	private static final double EXISTING_CHECK_RADIUS = 16.0D;
	private static final int VERTICAL_SCAN = 4;

	private BackTeethmanSpawnManager() {
	}

	public static void trySpawnForPlayer(ServerLevel overworld, ServerPlayer player) {
		if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
			return;
		}
		if (hasBackTeethmanNear(overworld, player)) {
			return;
		}

		SpawnPosition position = computeBehindPosition(overworld, player);
		if (position == null) {
			return;
		}

		BackTeethmanEntity entity = OutofboundExtraEntities.BACK_TEETHMAN.get().create(overworld);
		if (entity == null) {
			return;
		}

		entity.moveTo(position.x(), position.y(), position.z(), position.yaw(), 0.0F);
		entity.setTarget(player.getUUID());
		overworld.addFreshEntity(entity);
	}

	private static boolean hasBackTeethmanNear(ServerLevel level, ServerPlayer player) {
		AABB box = player.getBoundingBox().inflate(EXISTING_CHECK_RADIUS, EXISTING_CHECK_RADIUS, EXISTING_CHECK_RADIUS);
		return !level.getEntitiesOfClass(BackTeethmanEntity.class, box, mob -> !mob.isRemoved()).isEmpty();
	}

	private static SpawnPosition computeBehindPosition(ServerLevel level, ServerPlayer player) {
		float yawRad = player.getYRot() * ((float) Math.PI / 180.0F);
		double behindX = player.getX() + Math.sin(yawRad) * BEHIND_DISTANCE;
		double behindZ = player.getZ() - Math.cos(yawRad) * BEHIND_DISTANCE;
		int blockX = Mth.floor(behindX);
		int blockZ = Mth.floor(behindZ);

		if (!level.hasChunk(blockX >> 4, blockZ >> 4)) {
			return null;
		}

		int playerY = player.getBlockY();
		for (int dy = 0; dy <= VERTICAL_SCAN; dy++) {
			for (int sign = 0; sign < 2; sign++) {
				int y = playerY + (sign == 0 ? -dy : dy);
				if (dy == 0 && sign == 1) {
					continue;
				}
				BlockPos feet = new BlockPos(blockX, y, blockZ);
				BlockPos ground = feet.below();
				BlockPos head = feet.above();
				if (OverworldStalkerSpawnUtil.isValidSpawnSpace(level, ground, feet, head)) {
					double x = feet.getX() + 0.5D;
					double z = feet.getZ() + 0.5D;
					float yaw = OverworldStalkerSpawnUtil.yawToward(player, x, feet.getY(), z);
					return new SpawnPosition(x, feet.getY(), z, yaw);
				}
			}
		}

		double x = blockX + 0.5D;
		double y = player.getY();
		double z = blockZ + 0.5D;
		BlockPos feet = BlockPos.containing(x, y, z);
		BlockPos head = feet.above();
		BlockState feetState = level.getBlockState(feet);
		BlockState headState = level.getBlockState(head);
		if (!feetState.isAir() || !headState.isAir()) {
			y = player.getEyeY() - 1.0D;
		}
		float yaw = OverworldStalkerSpawnUtil.yawToward(player, x, y, z);
		return new SpawnPosition(x, y, z, yaw);
	}

	private record SpawnPosition(double x, double y, double z, float yaw) {
	}
}
