package net.lixis.outofbound.entity;

import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public final class SkyPainterSpawnManager {

	private static final int MIN_HEIGHT_ABOVE_TERRAIN = 55;
	private static final int MAX_HEIGHT_ABOVE_TERRAIN = 85;
	private static final double EXISTING_CHECK_RADIUS = 20.0D;
	private static final double HORIZONTAL_OFFSET = 24.0D;

	private SkyPainterSpawnManager() {
	}

	public static boolean trySpawnForPlayer(ServerLevel level, ServerPlayer player) {
		if (!player.isAlive() || player.isSpectator()) {
			return false;
		}
		if (hasPainterNear(level, player)) {
			return false;
		}

		double angle = level.random.nextDouble() * Math.PI * 2.0D;
		int spawnX = Mth.floor(player.getX() + Math.cos(angle) * HORIZONTAL_OFFSET);
		int spawnZ = Mth.floor(player.getZ() + Math.sin(angle) * HORIZONTAL_OFFSET);
		if (!level.hasChunk(spawnX >> 4, spawnZ >> 4)) {
			return false;
		}

		int terrain = level.getHeight(Heightmap.Types.MOTION_BLOCKING, spawnX, spawnZ);
		int minY = terrain + MIN_HEIGHT_ABOVE_TERRAIN;
		int maxY = Math.min(level.getMaxBuildHeight() - 10, terrain + MAX_HEIGHT_ABOVE_TERRAIN);
		int spawnY = maxY <= minY ? minY : minY + level.random.nextInt(maxY - minY);

		SkyPainterEntity entity = OutofboundExtraEntities.SKY_PAINTER.get().create(level);
		if (entity == null) {
			return false;
		}
		entity.moveTo(spawnX + 0.5D, spawnY, spawnZ + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
		entity.setTarget(player.getUUID());
		level.addFreshEntity(entity);
		return true;
	}

	private static boolean hasPainterNear(ServerLevel level, ServerPlayer player) {
		AABB box = player.getBoundingBox().inflate(EXISTING_CHECK_RADIUS);
		return !level.getEntitiesOfClass(SkyPainterEntity.class, box, e -> !e.isRemoved()).isEmpty();
	}
}
