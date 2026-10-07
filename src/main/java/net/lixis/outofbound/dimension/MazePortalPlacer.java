package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.gen.MazeShape;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public final class MazePortalPlacer {

	private static final ResourceLocation PORTAL_ID = new ResourceLocation(OutofboundMod.MODID, "dimension_portal");
	private static final int ENTRY_X = 1;
	private static final int ENTRY_Z = 1;

	private MazePortalPlacer() {
	}

	public static void ensurePortal(ServerLevel level) {
		if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
			return;
		}

		Block portalBlock = ForgeRegistries.BLOCKS.getValue(PORTAL_ID);
		if (portalBlock == null) {
			return;
		}

		BlockState portal = portalBlock.defaultBlockState();
		placeSpawnCluster(level, portalBlock, portal);
		OverworldPortalPlacer.tryPlaceInMazeLevel(level);
		HeartDecorPlacer.ensureDecor(level);
	}

	private static void placeSpawnCluster(ServerLevel level, Block portalBlock, BlockState portal) {
		long index = MazeDimensions.indexFromLocation(level.dimension().location());
		DimensionTheme theme = DimensionTheme.forIndex(index);
		int levelStride = theme.ceilingHeight + 1;
		int mazeLevels = MazeShape.levelCount(theme);
		int max = Math.max(1, MazeConfig.spawnPortalCount);
		int radius = Math.max(4, MazeConfig.spawnPortalRadius);
		int placed = 0;

		for (int mazeLevel = 0; mazeLevel < mazeLevels && placed < max; mazeLevel++) {
			int feetY = level.getMinBuildHeight() + 1 + mazeLevel * levelStride;
			if (tryPlacePortal(level, portalBlock, portal, feetY, ENTRY_X, ENTRY_Z)) {
				placed++;
			}
		}

		for (int ring = 4; ring <= radius && placed < max; ring += 4) {
			for (int[] dir : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
				if (placed >= max) {
					break;
				}
				int mazeLevel = placed % mazeLevels;
				int feetY = level.getMinBuildHeight() + 1 + mazeLevel * levelStride;
				if (tryPlacePortal(level, portalBlock, portal, feetY, ENTRY_X + dir[0] * ring, ENTRY_Z + dir[1] * ring)) {
					placed++;
				}
			}
		}

		for (int fallbackRadius = 2; placed < max && fallbackRadius <= radius + 4; fallbackRadius++) {
			for (int dx = -fallbackRadius; dx <= fallbackRadius; dx++) {
				for (int dz = -fallbackRadius; dz <= fallbackRadius; dz++) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != fallbackRadius) {
						continue;
					}
					if (placed >= max) {
						return;
					}
					int mazeLevel = placed % mazeLevels;
					int feetY = level.getMinBuildHeight() + 1 + mazeLevel * levelStride;
					if (tryPlacePortal(level, portalBlock, portal, feetY, ENTRY_X + dx, ENTRY_Z + dz)) {
						placed++;
					}
				}
			}
		}
	}

	private static boolean tryPlacePortal(ServerLevel level, Block portalBlock, BlockState portal, int feetY, int x, int z) {
		BlockPos feet = new BlockPos(x, feetY, z);
		BlockPos floor = feet.below();
		if (level.getBlockState(floor).is(portalBlock)) {
			return true;
		}
		if (!level.getBlockState(floor).isSolidRender(level, floor)) {
			return false;
		}
		if (!level.getBlockState(feet).isAir() || !level.getBlockState(feet.above()).isAir()) {
			return false;
		}
		level.setBlock(floor, portal, 3);
		return true;
	}
}
