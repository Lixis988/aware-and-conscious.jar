package net.lixis.outofbound.world;

import net.lixis.outofbound.block.OutofboundBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.ChunkPos;

public final class GlitchGrassPlacer {

	public static final int RANDOM_ATTEMPTS = 32;

	private GlitchGrassPlacer() {
	}

	public static boolean tryReplaceRandomGrass(ServerLevel level, ChunkPos chunkPos, RandomSource random) {
		ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);
		BlockState glitchGrass = OutofboundBlocks.GLITCH_GRASS.get().defaultBlockState();
		int baseX = chunkPos.getMinBlockX();
		int baseZ = chunkPos.getMinBlockZ();

		for (int attempt = 0; attempt < RANDOM_ATTEMPTS; attempt++) {
			int localX = random.nextInt(16);
			int localZ = random.nextInt(16);
			int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, localX, localZ) - 1;
			BlockPos pos = new BlockPos(baseX + localX, y, baseZ + localZ);
			if (!chunk.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
				continue;
			}
			level.setBlock(pos, glitchGrass, Block.UPDATE_CLIENTS);
			return true;
		}
		return false;
	}
}
