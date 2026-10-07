package net.lixis.outofbound.dimension.gen;

import net.lixis.outofbound.dimension.theme.DimensionTheme;

public enum MazeStyle {

	GRID_ROOMS {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			return gridColumn(seed, theme, x, z, level, theme.corridorWidth + 1, theme.openness, true);
		}
	},

	DENSE_LABYRINTH {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			return gridColumn(seed, theme, x, z, level, 3, theme.openness * 0.55F, false);
		}
	},

	PILLAR_HALL {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int gap = theme.corridorWidth + 3;
			int lx = Math.floorMod(x, gap);
			int lz = Math.floorMod(z, gap);
			if (lx < 2 && lz < 2) {
				return ColumnType.PILLAR;
			}
			return ColumnType.OPEN;
		}
	},

	BACKROOMS {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int pitch = theme.corridorWidth + 1;
			int lx = Math.floorMod(x, pitch);
			int lz = Math.floorMod(z, pitch);
			long cx = Math.floorDiv(x, pitch);
			long cz = Math.floorDiv(z, pitch);
			boolean wx = lx == pitch - 1;
			boolean wz = lz == pitch - 1;
			float wallBias = theme.wallBias * 0.5F;
			if (wx && wz) {
				return MazeHash.hashFloat(seed, 0x51, cx, cz, level) < 0.4F ? ColumnType.PILLAR : ColumnType.OPEN;
			}
			if (wx) {
				return MazeHash.hashFloat(seed, 0x52, cx, cz, level) < wallBias ? ColumnType.WALL : ColumnType.OPEN;
			}
			if (wz) {
				return MazeHash.hashFloat(seed, 0x53, cx, cz, level) < wallBias ? ColumnType.WALL : ColumnType.OPEN;
			}
			return ColumnType.OPEN;
		}
	},

	CAVERN {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			double scale = 0.07 + theme.corridorWidth * 0.004;
			double n = MazeHash.fbm(seed, 0x61, x * scale, z * scale, level);
			double threshold = 0.34 + theme.wallBias * 0.20;
			return n < threshold ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	CONCENTRIC {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int pitch = theme.corridorWidth + 1;
			int region = pitch * 8;
			long rx = Math.floorDiv(x, region);
			long rz = Math.floorDiv(z, region);
			double dx = Math.floorMod(x, region) - region / 2.0;
			double dz = Math.floorMod(z, region) - region / 2.0;
			double dist = Math.sqrt(dx * dx + dz * dz);
			long ringIndex = Math.round(dist / pitch);
			boolean onRing = Math.round(dist) % pitch == 0;
			if (!onRing) {
				return ColumnType.OPEN;
			}

			long angleBucket = (long) (Math.floor((Math.atan2(dz, dx) + Math.PI) / (Math.PI / 4.0)));
			if (MazeHash.hashFloat(seed, 0x71, rx, rz, ringIndex, angleBucket) < 0.30F) {
				return ColumnType.OPEN;
			}
			return ColumnType.WALL;
		}
	},

	NOISE_CHAOS {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			double scale = 0.05 + theme.corridorWidth * 0.003;
			double n = MazeHash.fbm(seed, 0x81, x * scale, z * scale, level);
			double bandwidth = 0.05 + (1.0F - theme.openness) * 0.05;
			return Math.abs(n - 0.5) < bandwidth ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	DIAGONAL {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int pitch = theme.corridorWidth + 1;
			long row = Math.floorDiv(z, pitch);
			int shift = (int) (Math.floorMod(row, 2L) * (pitch / 2));
			return gridColumn(seed, theme, x + shift, z, level, pitch, theme.openness, false);
		}
	},

	SIERPINSKI {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int scale = Math.max(1, theme.corridorWidth - 1);
			long ax = Math.floorDiv(x, scale);
			long az = Math.floorDiv(z, scale);
			return sierpinskiSolid(ax, az) ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	MANDELBROT {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int region = (theme.corridorWidth + 1) * 16;
			double a = (Math.floorMod(x, region) / (double) region) * 3.0 - 2.1;
			double b = (Math.floorMod(z, region) / (double) region) * 2.6 - 1.3;
			int max = 26;
			int iters = mandelbrotIterations(a, b, max);
			if (iters >= max) {
				return ColumnType.WALL;
			}
			return (iters % 5 == 0) ? ColumnType.PILLAR : ColumnType.OPEN;
		}
	},

	TRIG_INTERFERENCE {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			double v = Math.sin(x * theme.freqA)
					+ Math.sin(z * theme.freqB)
					+ Math.sin((x + z) * theme.freqC)
					+ Math.sin((x - z) * theme.freqA * 0.5);
			double threshold = 1.1 + theme.wallBias;
			return v > threshold ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	VORONOI {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int cell = theme.corridorWidth + 2;
			double edge = 0.6 + theme.wallBias * 1.4;
			return voronoiEdge(seed, x, z, cell, level, edge) ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	RIPPLE {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			double dist = Math.sqrt((double) x * x + (double) z * z);
			double v = Math.sin(dist * theme.featureScale + level * 0.7);
			double threshold = 0.55 + theme.wallBias * 0.35;
			return v > threshold ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	QUADTREE_BSP {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int base = theme.corridorWidth + 1;
			for (int k = 0; k < 4; k++) {
				int size = base << k;
				boolean onSplit = Math.floorMod(x, size) == 0 || Math.floorMod(z, size) == 0;
				if (onSplit && MazeHash.hashFloat(seed, 0x91 + k,
						Math.floorDiv((long) x, size), Math.floorDiv((long) z, size), level) < 0.6F) {
					return ColumnType.WALL;
				}
			}
			return ColumnType.OPEN;
		}
	},

	HONEYCOMB {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int cell = theme.corridorWidth + 2;
			int row = Math.floorDiv(z, cell);
			int shift = Math.floorMod(row, 2) * (cell / 2);
			int lx = Math.floorMod(x + shift, cell);
			int lz = Math.floorMod(z, cell);
			boolean edge = lx == 0 || lz == 0 || lx + lz == cell - 1;
			if (!edge) {
				return ColumnType.OPEN;
			}
			if (lx == 0 && lz == 0) {
				return ColumnType.PILLAR;
			}
			return MazeHash.hashFloat(seed, 0xB1, Math.floorDiv(x + shift, cell), Math.floorDiv(z, cell), level)
					< theme.wallBias + 0.25F ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	SPIRAL_ARMS {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			double angle = Math.atan2(z, x);
			double radius = Math.sqrt((double) x * x + (double) z * z);
			double arms = 3.0 + theme.corridorWidth * 0.35;
			double v = Math.sin(angle * arms - radius * theme.featureScale);
			return v > (0.35 + theme.wallBias * 0.4) ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	STRIPE_RUNS {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int pitch = theme.corridorWidth + 1;
			boolean vertical = MazeHash.hashFloat(seed, 0xB2, Math.floorDiv(x, pitch * 8L),
					Math.floorDiv(z, pitch * 8L), level) < 0.5F;
			int along = vertical ? z : x;
			int across = vertical ? x : z;
			if (Math.floorMod(across, pitch) != pitch - 1) {
				return ColumnType.OPEN;
			}
			long run = Math.floorDiv(along, pitch);
			return MazeHash.hashFloat(seed, 0xB3, run, vertical ? 1 : 0, level) < theme.openness
					? ColumnType.OPEN : ColumnType.WALL;
		}
	},

	SCATTER_PILLARS {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int gap = Math.max(3, theme.corridorWidth + 1);
			if (Math.floorMod(x, gap) == 0 && Math.floorMod(z, gap) == 0) {
				return ColumnType.PILLAR;
			}
			double n = MazeHash.fbm(seed, 0xB4, x * 0.09, z * 0.09, level);
			return n < 0.18 + theme.wallBias * 0.12 ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	CROSSHATCH {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int pitch = theme.corridorWidth + 2;
			boolean a = Math.floorMod(x + z, pitch) == 0;
			boolean b = Math.floorMod(x - z, pitch) == 0;
			if (a && b) {
				return ColumnType.PILLAR;
			}
			if (a || b) {
				return MazeHash.hashFloat(seed, 0xB5, x, z, level) < theme.wallBias + 0.2F
						? ColumnType.WALL : ColumnType.OPEN;
			}
			return ColumnType.OPEN;
		}
	},

	CELLULAR {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int live = 0;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dz == 0) {
						continue;
					}
					if (MazeHash.hashFloat(seed, 0xB6, x + dx, z + dz, level) < theme.wallBias) {
						live++;
					}
				}
			}
			boolean self = MazeHash.hashFloat(seed, 0xB7, x, z, level) < theme.wallBias;
			if (self && live >= 2) {
				return live >= 5 ? ColumnType.PILLAR : ColumnType.WALL;
			}
			return live >= 6 ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	WORMHOLE {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			double scale = 0.04 + theme.corridorWidth * 0.003;
			double n1 = MazeHash.fbm(seed, 0xB8, x * scale, z * scale, level);
			double n2 = MazeHash.fbm(seed, 0xB9, x * scale * 1.7, z * scale * 1.7, level + 3);
			double tunnel = Math.abs(n1 - 0.5) + Math.abs(n2 - 0.5);
			double openBand = 0.18 + theme.openness * 0.18;
			return tunnel < openBand ? ColumnType.OPEN : ColumnType.WALL;
		}
	},

	RADIAL_SPOKES {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int region = (theme.corridorWidth + 1) * 12;
			double dx = Math.floorMod(x, region) - region / 2.0;
			double dz = Math.floorMod(z, region) - region / 2.0;
			double dist = Math.sqrt(dx * dx + dz * dz);
			if (dist < theme.corridorWidth) {
				return ColumnType.OPEN;
			}
			double angle = (Math.atan2(dz, dx) + Math.PI) / (Math.PI * 2.0);
			int spokes = 5 + theme.corridorWidth;
			double spokePos = angle * spokes;
			double spokeFrac = Math.abs(spokePos - Math.rint(spokePos));
			if (spokeFrac < 0.08) {
				return ColumnType.OPEN;
			}
			boolean ring = Math.floorMod((int) Math.round(dist), theme.corridorWidth + 1) == 0;
			return ring ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	CHECKERBOARD {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int cell = Math.max(2, theme.corridorWidth);
			long cx = Math.floorDiv(x, cell);
			long cz = Math.floorDiv(z, cell);
			boolean dark = ((cx + cz + level) & 1L) == 0L;
			int lx = Math.floorMod(x, cell);
			int lz = Math.floorMod(z, cell);
			boolean border = lx == 0 || lz == 0;
			if (dark && border) {
				return (lx == 0 && lz == 0) ? ColumnType.PILLAR : ColumnType.WALL;
			}
			if (!dark && MazeHash.hashFloat(seed, 0xBA, cx, cz, level) < theme.roomChance) {
				return ColumnType.OPEN;
			}
			return dark ? ColumnType.WALL : ColumnType.OPEN;
		}
	},

	BRANCHING {
		@Override
		public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
			int pitch = theme.corridorWidth + 1;
			long cx = Math.floorDiv(x, pitch);
			long cz = Math.floorDiv(z, pitch);
			int lx = Math.floorMod(x, pitch);
			int lz = Math.floorMod(z, pitch);
			boolean corridor = lx == pitch / 2 || lz == pitch / 2;
			if (!corridor) {
				return ColumnType.WALL;
			}
			if (lx == pitch / 2 && lz == pitch / 2) {
				return ColumnType.OPEN;
			}
			boolean horizontal = lz == pitch / 2;
			long from = horizontal ? cx : cz;
			float open = theme.openness * 0.85F + 0.1F;
			if (MazeHash.hashFloat(seed, 0xBB, from, horizontal ? 0 : 1, level) < open) {
				return ColumnType.OPEN;
			}
			return ColumnType.WALL;
		}
	};

	public enum ColumnType {
		OPEN, WALL, PILLAR
	}

	public abstract ColumnType column(long seed, DimensionTheme theme, int x, int z, int level);

	public static MazeStyle forIndex(long index) {
		MazeStyle[] values = values();
		int idx = (int) Math.floorMod(MazeHash.hash(index, 0x5747, index), values.length);
		return values[idx];
	}

	private static final long EDGE_SALT = 0x1111111111111111L;
	private static final long ROOM_SALT = 0x2222222222222222L;
	private static final int ROOM_GRID = 4;

	protected static ColumnType gridColumn(long seed, DimensionTheme theme, int x, int z, int level,
			int pitch, float openness, boolean rooms) {
		int lx = Math.floorMod(x, pitch);
		int lz = Math.floorMod(z, pitch);
		long cx = Math.floorDiv(x, pitch);
		long cz = Math.floorDiv(z, pitch);

		boolean wallX = lx == pitch - 1;
		boolean wallZ = lz == pitch - 1;

		if (wallX && wallZ) {
			return ColumnType.PILLAR;
		}
		if (!wallX && !wallZ) {
			return ColumnType.OPEN;
		}
		if (wallX) {
			return edgeOpen(seed, theme, cx, cz, cx + 1, cz, level, openness, rooms) ? ColumnType.OPEN : ColumnType.WALL;
		}
		return edgeOpen(seed, theme, cx, cz, cx, cz + 1, level, openness, rooms) ? ColumnType.OPEN : ColumnType.WALL;
	}

	private static boolean edgeOpen(long seed, DimensionTheme theme, long ax, long az, long bx, long bz,
			int level, float openness, boolean rooms) {
		if (rooms && isRoomCell(seed, theme, ax, az, level) && isRoomCell(seed, theme, bx, bz, level)) {
			return true;
		}
		return MazeHash.hashFloat(seed, EDGE_SALT, ax, az, bx + 0x9E3779B9L, bz, level) < openness;
	}

	private static boolean isRoomCell(long seed, DimensionTheme theme, long cx, long cz, int level) {
		long rgx = Math.floorDiv(cx, ROOM_GRID);
		long rgz = Math.floorDiv(cz, ROOM_GRID);
		return MazeHash.hashFloat(seed, ROOM_SALT, rgx, rgz, level) < theme.roomChance;
	}

	private static boolean sierpinskiSolid(long x, long z) {
		long ax = Math.abs(x);
		long az = Math.abs(z);
		for (int i = 0; i < 13 && (ax > 0 || az > 0); i++) {
			if (ax % 3 == 1 && az % 3 == 1) {
				return false;
			}
			ax /= 3;
			az /= 3;
		}
		return true;
	}

	private static int mandelbrotIterations(double a, double b, int max) {
		double zr = 0.0;
		double zi = 0.0;
		int i = 0;
		while (i < max && zr * zr + zi * zi <= 4.0) {
			double t = zr * zr - zi * zi + a;
			zi = 2.0 * zr * zi + b;
			zr = t;
			i++;
		}
		return i;
	}

	private static boolean voronoiEdge(long seed, int x, int z, int cell, int level, double edgeWidth) {
		long gx = Math.floorDiv((long) x, cell);
		long gz = Math.floorDiv((long) z, cell);
		double d1 = Double.MAX_VALUE;
		double d2 = Double.MAX_VALUE;
		for (int dgx = -1; dgx <= 1; dgx++) {
			for (int dgz = -1; dgz <= 1; dgz++) {
				long cxp = gx + dgx;
				long czp = gz + dgz;
				double fx = (cxp + MazeHash.hashFloat(seed, 0xA1, cxp, czp, level)) * cell;
				double fz = (czp + MazeHash.hashFloat(seed, 0xA2, cxp, czp, level)) * cell;
				double dx = x - fx;
				double dz = z - fz;
				double d = Math.sqrt(dx * dx + dz * dz);
				if (d < d1) {
					d2 = d1;
					d1 = d;
				} else if (d < d2) {
					d2 = d;
				}
			}
		}
		return (d2 - d1) < edgeWidth;
	}
}
