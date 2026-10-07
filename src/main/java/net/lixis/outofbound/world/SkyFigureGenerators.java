package net.lixis.outofbound.world;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SkyFigureGenerators {

	private SkyFigureGenerators() {
	}

	public static List<BlockPos> generate(SkyFigureType type, BlockPos anchor, int variantSeed) {
		int size = 3 + Math.floorMod(variantSeed, 4);
		return switch (type) {
			case HOLLOW_CUBE -> hollowCube(anchor, size);
			case RING -> ring(anchor, size + 2);
			case SPHERE_SHELL -> sphereShell(anchor, size + 1);
			case SIERPINSKI_CARPET -> sierpinskiCarpet(anchor, 1 + Math.floorMod(variantSeed, 2));
			case PLUS_CROSS -> plusCross(anchor, size + 3);
			default -> hollowCube(anchor, size);
		};
	}

	private static List<BlockPos> hollowCube(BlockPos anchor, int size) {
		Set<BlockPos> positions = new HashSet<>();
		int half = size / 2;
		for (int dx = -half; dx <= half; dx++) {
			for (int dy = -half; dy <= half; dy++) {
				for (int dz = -half; dz <= half; dz++) {
					boolean face = Math.abs(dx) == half || Math.abs(dy) == half || Math.abs(dz) == half;
					if (face) {
						positions.add(anchor.offset(dx, dy, dz));
					}
				}
			}
		}
		return new ArrayList<>(positions);
	}

	private static List<BlockPos> ring(BlockPos anchor, int radius) {
		List<BlockPos> positions = new ArrayList<>();
		int segments = radius * 8;
		for (int i = 0; i < segments; i++) {
			double angle = (Math.PI * 2.0D * i) / segments;
			int x = Mth.floor(anchor.getX() + Math.cos(angle) * radius);
			int z = Mth.floor(anchor.getZ() + Math.sin(angle) * radius);
			positions.add(new BlockPos(x, anchor.getY(), z));
		}
		return positions;
	}

	private static List<BlockPos> sphereShell(BlockPos anchor, int radius) {
		Set<BlockPos> positions = new HashSet<>();
		int r2 = radius * radius;
		int inner = Math.max(0, radius - 1);
		int inner2 = inner * inner;
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					int dist = dx * dx + dy * dy + dz * dz;
					if (dist <= r2 && dist >= inner2) {
						positions.add(anchor.offset(dx, dy, dz));
					}
				}
			}
		}
		return new ArrayList<>(positions);
	}

	private static List<BlockPos> sierpinskiCarpet(BlockPos anchor, int iterations) {
		Set<BlockPos> positions = new HashSet<>();
		int size = (int) Math.pow(3, iterations + 1);
		int half = size / 2;
		carpetRect(positions, anchor.getX() - half, anchor.getZ() - half, size, iterations);
		List<BlockPos> elevated = new ArrayList<>();
		for (BlockPos pos : positions) {
			elevated.add(new BlockPos(pos.getX(), anchor.getY(), pos.getZ()));
		}
		return elevated;
	}

	private static void carpetRect(Set<BlockPos> out, int originX, int originZ, int size, int depth) {
		if (depth <= 0) {
			for (int x = 0; x < size; x++) {
				for (int z = 0; z < size; z++) {
					out.add(new BlockPos(originX + x, 0, originZ + z));
				}
			}
			return;
		}
		int third = size / 3;
		for (int gx = 0; gx < 3; gx++) {
			for (int gz = 0; gz < 3; gz++) {
				if (gx == 1 && gz == 1) {
					continue;
				}
				carpetRect(out, originX + gx * third, originZ + gz * third, third, depth - 1);
			}
		}
	}

	private static List<BlockPos> plusCross(BlockPos anchor, int arm) {
		List<BlockPos> positions = new ArrayList<>();
		for (int i = -arm; i <= arm; i++) {
			positions.add(anchor.offset(i, 0, 0));
			positions.add(anchor.offset(0, i, 0));
			positions.add(anchor.offset(0, 0, i));
		}
		return positions;
	}
}
