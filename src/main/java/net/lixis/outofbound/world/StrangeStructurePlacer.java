package net.lixis.outofbound.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.concurrent.ThreadLocalRandom;

public final class StrangeStructurePlacer {

	private static final Block[] PALETTE = {
			Blocks.BLACK_CONCRETE,
			Blocks.GOLD_BLOCK,
			Blocks.CRYING_OBSIDIAN,
			Blocks.OBSIDIAN,
			Blocks.WHITE_CONCRETE,
			Blocks.GLASS
	};

	private StrangeStructurePlacer() {
	}

	public static void tryPlaceNear(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		double angle = random.nextDouble() * Math.PI * 2.0D;
		int distance = 24 + random.nextInt(28);
		int x = player.blockPosition().getX() + (int) Math.round(Math.cos(angle) * distance);
		int z = player.blockPosition().getZ() + (int) Math.round(Math.sin(angle) * distance);
		int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
		BlockPos origin = new BlockPos(x, y, z);

		switch (random.nextInt(7)) {
			case 0 -> placePillar(level, origin, random);
			case 1 -> placeRing(level, origin, random);
			case 2 -> placeHollowCube(level, origin, random);
			case 3 -> placeWallSegment(level, origin, random);
			case 4 -> placeFloatingCluster(level, origin, random);
			case 5 -> placeStairway(level, origin, random);
			default -> placeCross(level, origin, random);
		}
	}

	private static void placePillar(ServerLevel level, BlockPos origin, ThreadLocalRandom random) {
		Block block = pick(random);
		int height = 4 + random.nextInt(10);
		for (int dy = 0; dy < height; dy++) {
			placeIfReplaceable(level, origin.above(dy), block.defaultBlockState());
		}
	}

	private static void placeRing(ServerLevel level, BlockPos origin, ThreadLocalRandom random) {
		Block block = pick(random);
		int radius = 2 + random.nextInt(3);
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
					continue;
				}
				placeIfReplaceable(level, origin.offset(dx, 0, dz), block.defaultBlockState());
				if (random.nextBoolean()) {
					placeIfReplaceable(level, origin.offset(dx, 1, dz), block.defaultBlockState());
				}
			}
		}
	}

	private static void placeHollowCube(ServerLevel level, BlockPos origin, ThreadLocalRandom random) {
		Block block = pick(random);
		int size = 3 + random.nextInt(2);
		for (int dx = 0; dx < size; dx++) {
			for (int dy = 0; dy < size; dy++) {
				for (int dz = 0; dz < size; dz++) {
					boolean edge = dx == 0 || dy == 0 || dz == 0 || dx == size - 1 || dy == size - 1 || dz == size - 1;
					if (!edge) {
						continue;
					}
					placeIfReplaceable(level, origin.offset(dx, dy, dz), block.defaultBlockState());
				}
			}
		}
	}

	private static void placeWallSegment(ServerLevel level, BlockPos origin, ThreadLocalRandom random) {
		Block block = pick(random);
		int length = 5 + random.nextInt(6);
		int height = 3 + random.nextInt(3);
		boolean alongX = random.nextBoolean();
		for (int i = 0; i < length; i++) {
			for (int dy = 0; dy < height; dy++) {
				BlockPos pos = alongX ? origin.offset(i, dy, 0) : origin.offset(0, dy, i);
				placeIfReplaceable(level, pos, block.defaultBlockState());
			}
		}
		if (random.nextBoolean()) {
			placeIfReplaceable(level, origin.offset(alongX ? length / 2 : 0, height, alongX ? 0 : length / 2),
					Blocks.AIR.defaultBlockState());
		}
	}

	private static void placeFloatingCluster(ServerLevel level, BlockPos origin, ThreadLocalRandom random) {
		Block block = pick(random);
		int count = 4 + random.nextInt(6);
		for (int i = 0; i < count; i++) {
			BlockPos pos = origin.offset(
					random.nextInt(5) - 2,
					3 + random.nextInt(6),
					random.nextInt(5) - 2);
			placeIfReplaceable(level, pos, block.defaultBlockState());
		}
	}

	private static void placeStairway(ServerLevel level, BlockPos origin, ThreadLocalRandom random) {
		Block block = pick(random);
		int steps = 5 + random.nextInt(6);
		for (int i = 0; i < steps; i++) {
			placeIfReplaceable(level, origin.offset(i, i, 0), block.defaultBlockState());
			if (random.nextBoolean()) {
				placeIfReplaceable(level, origin.offset(i, i, 1), block.defaultBlockState());
			}
		}
	}

	private static void placeCross(ServerLevel level, BlockPos origin, ThreadLocalRandom random) {
		Block block = pick(random);
		int arm = 2 + random.nextInt(2);
		for (int i = -arm; i <= arm; i++) {
			placeIfReplaceable(level, origin.offset(i, 0, 0), block.defaultBlockState());
			placeIfReplaceable(level, origin.offset(0, 0, i), block.defaultBlockState());
		}
		placeIfReplaceable(level, origin.above(), Blocks.GOLD_BLOCK.defaultBlockState());
	}

	private static Block pick(ThreadLocalRandom random) {
		return PALETTE[random.nextInt(PALETTE.length)];
	}

	private static void placeIfReplaceable(ServerLevel level, BlockPos pos, BlockState state) {
		BlockState existing = level.getBlockState(pos);
		if (existing.isAir() || existing.canBeReplaced()) {
			level.setBlock(pos, state, 3);
		}
	}
}
