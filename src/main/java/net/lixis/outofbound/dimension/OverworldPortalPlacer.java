package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.gen.MazeShape;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.registries.ForgeRegistries;

public final class OverworldPortalPlacer {

	private static final ResourceLocation PORTAL_ID = new ResourceLocation(OutofboundMod.MODID, "overworld_portal");

	private OverworldPortalPlacer() {
	}

	public static void tryPlaceInMazeLevel(ServerLevel level) {
		if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
			return;
		}

		Block portalBlock = ForgeRegistries.BLOCKS.getValue(PORTAL_ID);
		if (portalBlock == null) {
			return;
		}

		long index = MazeDimensions.indexFromLocation(level.dimension().location());
		DimensionTheme theme = DimensionTheme.forIndex(index);
		int levelStride = theme.ceilingHeight + 1;
		int feetY = level.getMinBuildHeight() + 1;
		int max = Math.max(1, MazeConfig.spawnOverworldPortalCount);

		int placed = 0;
		int[][] offsets = {{5, 1}, {-4, 1}, {1, 5}, {1, -4}};
		for (int[] offset : offsets) {
			if (placed >= max) {
				break;
			}
			for (int mazeLevel = 0; mazeLevel < MazeShape.levelCount(theme) && placed < max; mazeLevel++) {
				int y = feetY + mazeLevel * levelStride;
				if (tryPlacePortal(level, portalBlock, y, Mth.floor(MazeSafeSpawn.DEFAULT_X) + offset[0],
						Mth.floor(MazeSafeSpawn.DEFAULT_Z) + offset[1])) {
					placed++;
					break;
				}
			}
		}
	}

	public static void tryPlaceInOverworldChunk(ServerLevel overworld, ChunkAccess chunk, long dimensionIndex) {
		Block portalBlock = ForgeRegistries.BLOCKS.getValue(PORTAL_ID);
		if (portalBlock == null) {
			return;
		}

		DimensionTheme theme = DimensionTheme.forIndex(dimensionIndex);
		ChunkPos chunkPos = chunk.getPos();
		int wx = chunkPos.getMinBlockX() + 8;
		int wz = chunkPos.getMinBlockZ() + 8;
		int feetY = MazeShape.MIN_Y + 1;
		int levelStride = theme.ceilingHeight + 1;

		for (int mazeLevel = 0; mazeLevel < MazeShape.levelCount(theme); mazeLevel++) {
			int y = feetY + mazeLevel * levelStride;
			if (tryPlacePortal(overworld, portalBlock, y, wx, wz)) {
				return;
			}
		}
	}

	private static boolean tryPlacePortal(ServerLevel level, Block portalBlock, int feetY, int x, int z) {
		BlockState portal = portalBlock.defaultBlockState();
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
