package net.lixis.outofbound.dimension;

import net.lixis.outofbound.dimension.gen.MazeShape;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public final class MazeFloors {

	private MazeFloors() {
	}

	public static int[] mazeFloorFeetYs(ServerLevel level) {
		DimensionTheme theme = themeFor(level);
		int stride = theme.ceilingHeight + 1;
		int floors = MazeShape.levelCount(theme);
		int base = level.getMinBuildHeight() + 1;
		int[] ys = new int[floors];
		for (int floor = 0; floor < floors; floor++) {
			ys[floor] = base + floor * stride;
		}
		return ys;
	}

	public static int nearestFloorIndex(ServerLevel level, int feetY) {
		int[] feetYs = mazeFloorFeetYs(level);
		int bestIndex = 0;
		int bestDistance = Integer.MAX_VALUE;
		for (int floor = 0; floor < feetYs.length; floor++) {
			int distance = Math.abs(feetYs[floor] - feetY);
			if (distance < bestDistance) {
				bestDistance = distance;
				bestIndex = floor;
			}
		}
		return bestIndex;
	}

	public static int feetYForFloor(ServerLevel level, int floorIndex) {
		int[] feetYs = mazeFloorFeetYs(level);
		return feetYs[Mth.clamp(floorIndex, 0, feetYs.length - 1)];
	}

	public static int floorFeetYForEntity(ServerLevel level, Entity entity) {
		return feetYForFloor(level, nearestFloorIndex(level, Mth.floor(entity.getY())));
	}

	public static int floorIndexForEntity(ServerLevel level, Entity entity) {
		return nearestFloorIndex(level, Mth.floor(entity.getY()));
	}

	public static boolean sameFloor(ServerLevel level, Entity a, Entity b) {
		return floorIndexForEntity(level, a) == floorIndexForEntity(level, b);
	}

	public static int[] orderFeetYsByHint(int[] feetYs, int hintFeetY) {
		int[] ordered = feetYs.clone();
		for (int i = 0; i < ordered.length - 1; i++) {
			for (int j = i + 1; j < ordered.length; j++) {
				int di = Math.abs(ordered[i] - hintFeetY);
				int dj = Math.abs(ordered[j] - hintFeetY);
				if (dj < di) {
					int swap = ordered[i];
					ordered[i] = ordered[j];
					ordered[j] = swap;
				}
			}
		}
		return ordered;
	}

	private static DimensionTheme themeFor(ServerLevel level) {
		long index = Math.max(0L, MazeDimensions.indexFromLocation(level.dimension().location()));
		return DimensionTheme.forIndex(index);
	}
}
