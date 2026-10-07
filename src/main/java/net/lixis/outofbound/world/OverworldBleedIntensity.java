package net.lixis.outofbound.world;

import net.lixis.outofbound.dimension.MazeConfig;

public final class OverworldBleedIntensity {

	private OverworldBleedIntensity() {
	}

	public static int effectiveDepth(long deepestMazeIndex) {
		if (deepestMazeIndex <= 0L) {
			return 0;
		}
		return (int) Math.min(deepestMazeIndex, MazeConfig.bleedMaxDepthForScaling);
	}

	public static double newChunkChance(long deepestMazeIndex) {
		int depth = effectiveDepth(deepestMazeIndex);
		double scaled = MazeConfig.bleedNewChunkChance + depth * MazeConfig.bleedPerDimensionChanceBonus;
		return Math.min(MazeConfig.bleedNewChunkChanceCap, scaled);
	}

	public static int ticksPerChunk(long deepestMazeIndex) {
		int depth = effectiveDepth(deepestMazeIndex);
		double factor = 1.0D + depth * MazeConfig.bleedPerDimensionSpeedBonus;
		return Math.max(MazeConfig.bleedMinTicksPerChunk, (int) (MazeConfig.bleedTicksPerChunk / factor));
	}

	public static int chunksPerTick(long deepestMazeIndex) {
		int depth = effectiveDepth(deepestMazeIndex);
		int extra = depth / Math.max(1, MazeConfig.bleedDimensionsPerExtraChunk);
		return Math.min(8, MazeConfig.bleedChunksPerTick + extra);
	}
}
