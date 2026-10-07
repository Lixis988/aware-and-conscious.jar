package net.lixis.outofbound.dimension;

import net.lixis.outofbound.dimension.gen.MazeHash;
import net.lixis.outofbound.dimension.gen.MazeShape;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.lixis.outofbound.entity.HeartDecorEntity;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

public final class HeartDecorPlacer {

	private static final long DECOR_SALT = 0x4445434F520000L;
	private static final int COUNT = 4;

	private HeartDecorPlacer() {
	}

	public static void ensureDecor(ServerLevel level) {
		if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
			return;
		}
		long index = MazeDimensions.indexFromLocation(level.dimension().location());
		DimensionTheme theme = DimensionTheme.forIndex(index);
		int levels = MazeShape.levelCount(theme);
		int stride = theme.ceilingHeight + 1;

		AABB nearSpawn = new AABB(
				MazeSafeSpawn.DEFAULT_X - 24, level.getMinBuildHeight(), MazeSafeSpawn.DEFAULT_Z - 24,
				MazeSafeSpawn.DEFAULT_X + 24, level.getMaxBuildHeight(), MazeSafeSpawn.DEFAULT_Z + 24);
		if (level.getEntitiesOfClass(HeartDecorEntity.class, nearSpawn, e -> !e.isRemoved()).size() >= COUNT) {
			return;
		}

		for (int i = 0; i < COUNT; i++) {
			long h = MazeHash.hash(index, DECOR_SALT, i, 1);
			int mazeLevel = (int) Math.floorMod(h, levels);
			int ring = 3 + (int) Math.floorMod(h >>> 8, 10);
			double angle = ((h >>> 16) & 0xFFFF) / 65535.0D * Math.PI * 2.0D;
			int x = (int) Math.round(MazeSafeSpawn.DEFAULT_X + Math.cos(angle) * ring);
			int z = (int) Math.round(MazeSafeSpawn.DEFAULT_Z + Math.sin(angle) * ring);
			int feetY = level.getMinBuildHeight() + 1 + mazeLevel * stride;
			tryPlace(level, x, feetY, z);
		}
	}

	private static boolean tryPlace(ServerLevel level, int x, int feetY, int z) {
		BlockPos feet = new BlockPos(x, feetY, z);
		BlockPos floor = feet.below();
		if (!level.isLoaded(feet)) {
			return false;
		}
		if (!level.getBlockState(floor).isSolidRender(level, floor)) {
			return false;
		}
		if (!level.getBlockState(feet).isAir()) {
			return false;
		}
		AABB box = new AABB(feet).inflate(1.5D);
		if (!level.getEntitiesOfClass(HeartDecorEntity.class, box).isEmpty()) {
			return false;
		}

		HeartDecorEntity heart = OutofboundExtraEntities.HEART_DECOR.get().create(level);
		if (heart == null) {
			return false;
		}
		heart.moveTo(x + 0.5D, feetY + 0.15D, z + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
		heart.getPersistentData().putBoolean(HeartDecorEntity.MANAGED_TAG, true);
		level.addFreshEntity(heart);
		return true;
	}
}
