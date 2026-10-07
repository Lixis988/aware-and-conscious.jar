package net.lixis.outofbound.dimension.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.server.level.WorldGenRegion;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MazeChunkGenerator extends ChunkGenerator {

	public static final Codec<MazeChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BiomeSource.CODEC.fieldOf("biome_source").forGetter(g -> g.biomeSource),
			Codec.LONG.fieldOf("seed").forGetter(g -> g.seed),
			Codec.LONG.fieldOf("dimension_index").forGetter(g -> g.dimensionIndex)
	).apply(instance, MazeChunkGenerator::new));

	private final BiomeSource biomeSource;
	private final long seed;
	private final long dimensionIndex;
	private final DimensionTheme theme;

	public MazeChunkGenerator(BiomeSource biomeSource, long seed, long dimensionIndex) {
		super(biomeSource);
		this.biomeSource = biomeSource;
		this.seed = seed;
		this.dimensionIndex = dimensionIndex;
		this.theme = DimensionTheme.forIndex(dimensionIndex);
	}

	public long getDimensionIndex() {
		return dimensionIndex;
	}

	public DimensionTheme getTheme() {
		return theme;
	}

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public int getMinY() {
		return MazeShape.MIN_Y;
	}

	@Override
	public int getGenDepth() {

		int h = MazeShape.totalHeight(theme);
		int rounded = ((h + 15) / 16) * 16;
		return Math.max(16, rounded);
	}

	@Override
	public int getSeaLevel() {
		return MazeShape.MIN_Y;
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState,
			StructureManager structureManager, ChunkAccess chunk) {
		return CompletableFuture.supplyAsync(() -> {
			MazeChunkWriter.fillChunk(chunk, dimensionIndex);
			return chunk;
		}, executor);
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {

		return getMinY() + 1;
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
		int depth = getGenDepth();
		BlockState[] column = new BlockState[depth];
		for (int i = 0; i < depth; i++) {
			BlockState state = MazeShape.blockAt(theme, seed, x, getMinY() + i, z);
			column[i] = state == null ? Blocks.AIR.defaultBlockState() : state;
		}
		return new NoiseColumn(getMinY(), column);
	}

	@Override
	public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager,
			StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {

	}

	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState,
			ChunkAccess chunk) {

	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion region) {

	}

	@Override
	public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
		info.add("Maze dimension #" + dimensionIndex + " (" + theme.name + (theme.chaotic ? ", chaotic" : "") + ")");
		info.add("style=" + theme.composer.describe());
		info.add(String.format("corridor=%d ceiling=%d levels=%d openness=%.2f",
				theme.corridorWidth, theme.ceilingHeight, MazeShape.levelCount(theme), theme.openness));
	}
}
