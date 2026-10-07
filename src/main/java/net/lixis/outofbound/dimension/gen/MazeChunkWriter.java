package net.lixis.outofbound.dimension.gen;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public final class MazeChunkWriter {

	private MazeChunkWriter() {
	}

	public static void fillChunk(ChunkAccess chunk, long dimensionIndex) {
		fillChunk(chunk, dimensionIndex, false);
	}

	public static void fillOverworldChunk(ChunkAccess chunk, long dimensionIndex) {
		fillChunk(chunk, dimensionIndex, true);
	}

	private static void fillChunk(ChunkAccess chunk, long dimensionIndex, boolean overworldBleed) {
		if (overworldBleed && chunk instanceof LevelChunk levelChunk) {

			Level level = levelChunk.getLevel();
			boolean previousCapture = level.captureBlockSnapshots;
			level.captureBlockSnapshots = true;
			try {
				writeBlocks(chunk, dimensionIndex, true);
			} finally {
				level.captureBlockSnapshots = previousCapture;
				level.capturedBlockSnapshots.clear();
			}
			return;
		}
		writeBlocks(chunk, dimensionIndex, overworldBleed);
	}

	private static void writeBlocks(ChunkAccess chunk, long dimensionIndex, boolean overworldBleed) {
		DimensionTheme theme = DimensionTheme.forIndex(dimensionIndex);
		long seed = MazeDimensions.seedForIndex(dimensionIndex);

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		ChunkPos chunkPos = chunk.getPos();
		int minBlockX = chunkPos.getMinBlockX();
		int minBlockZ = chunkPos.getMinBlockZ();

		int minY = MazeShape.MIN_Y;
		int mazeTop = minY + MazeShape.totalHeight(theme);
		int loopMinY = overworldBleed ? chunk.getMinBuildHeight() : minY;
		int loopMaxY = overworldBleed ? chunk.getMaxBuildHeight() : minY + roundedGenDepth(theme);

		Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
		Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				int wx = minBlockX + dx;
				int wz = minBlockZ + dz;
				for (int y = loopMinY; y < loopMaxY; y++) {
					BlockState state = blockStateAt(theme, seed, wx, y, wz, mazeTop, overworldBleed);
					if (state == null) {
						continue;
					}
					pos.set(wx, y, wz);
					chunk.setBlockState(pos, state, false);
					oceanFloor.update(dx, y, dz, state);
					worldSurface.update(dx, y, dz, state);
				}
			}
		}
	}

	private static BlockState blockStateAt(DimensionTheme theme, long seed, int x, int y, int z, int mazeTop,
			boolean overworldBleed) {
		if (overworldBleed) {
			if (y < MazeShape.MIN_Y) {
				return theme.pillar;
			}
			if (y >= mazeTop) {
				return Blocks.AIR.defaultBlockState();
			}
		}

		BlockState state = MazeShape.blockAt(theme, seed, x, y, z);
		if (overworldBleed) {
			if (state == null) {
				return Blocks.AIR.defaultBlockState();
			}

			if (state.getBlock() instanceof CarvedPumpkinBlock) {
				return Blocks.PUMPKIN.defaultBlockState();
			}
			return state;
		}
		return state;
	}

	private static int roundedGenDepth(DimensionTheme theme) {
		int h = MazeShape.totalHeight(theme);
		int rounded = ((h + 15) / 16) * 16;
		return Math.max(16, rounded);
	}
}
