package net.lixis.outofbound.dimension.gen;

import net.lixis.outofbound.dimension.gen.MazeStyle.ColumnType;
import net.lixis.outofbound.dimension.theme.DimensionTheme;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class MazeComposer {

	public enum Blend {
		SINGLE,
		REGION,
		LEVEL,
		INTERLEAVE,
		UNION,
		INTERSECT,
		XOR,
		MAJORITY,
		CHECKER,
		RADIAL
	}

	private final MazeStyle[] styles;
	private final Blend blend;
	private final double regionScale;
	private final long salt;

	private MazeComposer(MazeStyle[] styles, Blend blend, double regionScale, long salt) {
		this.styles = styles;
		this.blend = blend;
		this.regionScale = regionScale;
		this.salt = salt;
	}

	public static MazeComposer forIndex(long index) {
		Random rng = new Random(index * 0x9E3779B97F4A7C15L ^ 0xA5A5A5A5A5A5A5A5L);

		MazeStyle[] all = MazeStyle.values();
		int count = 1 + rng.nextInt(5);
		MazeStyle[] chosen = new MazeStyle[count];
		Set<MazeStyle> used = new HashSet<>();
		for (int i = 0; i < count; i++) {
			MazeStyle pick = all[rng.nextInt(all.length)];
			int guard = 0;
			while (used.contains(pick) && used.size() < all.length && guard++ < 24) {
				pick = all[rng.nextInt(all.length)];
			}
			used.add(pick);
			chosen[i] = pick;
		}

		Blend blend;
		if (count == 1) {
			blend = Blend.SINGLE;
		} else {
			Blend[] blends = Blend.values();
			blend = blends[1 + rng.nextInt(blends.length - 1)];
		}

		double regionScale = 0.0025 + rng.nextDouble() * 0.028;
		return new MazeComposer(chosen, blend, regionScale, rng.nextLong());
	}

	public ColumnType column(long seed, DimensionTheme theme, int x, int z, int level) {
		switch (blend) {
			case LEVEL:
				return styles[Math.floorMod(level, styles.length)].column(seed, theme, x, z, level);
			case REGION: {
				double n = MazeHash.fbm(seed, salt, x * regionScale, z * regionScale, 0);
				int idx = Math.min(styles.length - 1, (int) (n * styles.length));
				return styles[idx].column(seed, theme, x, z, level);
			}
			case INTERLEAVE: {
				int idx = (int) Math.floorMod(
						MazeHash.hash(seed, salt, Math.floorDiv((long) x, 16), Math.floorDiv((long) z, 16), level),
						styles.length);
				return styles[idx].column(seed, theme, x, z, level);
			}
			case CHECKER: {
				long cx = Math.floorDiv(x, Math.max(4, theme.corridorWidth * 2));
				long cz = Math.floorDiv(z, Math.max(4, theme.corridorWidth * 2));
				int idx = (int) Math.floorMod(cx + cz + level, styles.length);
				return styles[idx].column(seed, theme, x, z, level);
			}
			case RADIAL: {
				double dist = Math.sqrt((double) x * x + (double) z * z);
				int band = (int) Math.floor(dist * regionScale * 80.0);
				return styles[Math.floorMod(band, styles.length)].column(seed, theme, x, z, level);
			}
			case UNION:
				return combine(seed, theme, x, z, level, Blend.UNION);
			case INTERSECT:
				return combine(seed, theme, x, z, level, Blend.INTERSECT);
			case XOR:
				return combine(seed, theme, x, z, level, Blend.XOR);
			case MAJORITY:
				return combine(seed, theme, x, z, level, Blend.MAJORITY);
			case SINGLE:
			default:
				return styles[0].column(seed, theme, x, z, level);
		}
	}

	private ColumnType combine(long seed, DimensionTheme theme, int x, int z, int level, Blend mode) {
		int solidCount = 0;
		int pillarCount = 0;
		for (MazeStyle style : styles) {
			ColumnType t = style.column(seed, theme, x, z, level);
			if (t == ColumnType.WALL) {
				solidCount++;
			} else if (t == ColumnType.PILLAR) {
				solidCount++;
				pillarCount++;
			}
		}

		switch (mode) {
			case UNION:
				if (solidCount == 0) {
					return ColumnType.OPEN;
				}
				return pillarCount == solidCount ? ColumnType.PILLAR : ColumnType.WALL;
			case INTERSECT:
				if (solidCount < styles.length) {
					return ColumnType.OPEN;
				}
				return pillarCount == styles.length ? ColumnType.PILLAR : ColumnType.WALL;
			case MAJORITY: {
				int need = (styles.length / 2) + 1;
				if (solidCount < need) {
					return ColumnType.OPEN;
				}
				return pillarCount * 2 >= solidCount ? ColumnType.PILLAR : ColumnType.WALL;
			}
			case XOR:
			default:
				return (solidCount % 2 == 1) ? ColumnType.WALL : ColumnType.OPEN;
		}
	}

	public String describe() {
		return blend + Arrays.toString(styles);
	}
}
