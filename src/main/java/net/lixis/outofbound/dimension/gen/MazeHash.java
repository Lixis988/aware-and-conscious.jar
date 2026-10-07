package net.lixis.outofbound.dimension.gen;

public final class MazeHash {

	private MazeHash() {
	}

	public static long mix(long h) {
		h ^= h >>> 33;
		h *= 0xff51afd7ed558ccdL;
		h ^= h >>> 33;
		h *= 0xc4ceb9fe1a85ec53L;
		h ^= h >>> 33;
		return h;
	}

	public static long hash(long seed, long salt, long... vals) {
		long h = mix(seed + salt + 0x9E3779B97F4A7C15L);
		for (long v : vals) {
			h = mix(h ^ mix(v + 0x9E3779B97F4A7C15L));
		}
		return h;
	}

	public static float hashFloat(long seed, long salt, long... vals) {
		return (float) ((hash(seed, salt, vals) >>> 40) * 0x1.0p-24);
	}

	private static double lattice(long seed, long salt, long xi, long zi, int level) {
		return (hash(seed, salt, xi, zi, level) >>> 40) * 0x1.0p-24;
	}

	private static double smooth(double t) {
		return t * t * (3.0 - 2.0 * t);
	}

	private static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	public static double valueNoise(long seed, long salt, double x, double z, int level) {
		long x0 = (long) Math.floor(x);
		long z0 = (long) Math.floor(z);
		double fx = x - x0;
		double fz = z - z0;

		double v00 = lattice(seed, salt, x0, z0, level);
		double v10 = lattice(seed, salt, x0 + 1, z0, level);
		double v01 = lattice(seed, salt, x0, z0 + 1, level);
		double v11 = lattice(seed, salt, x0 + 1, z0 + 1, level);

		double sx = smooth(fx);
		double sz = smooth(fz);
		return lerp(lerp(v00, v10, sx), lerp(v01, v11, sx), sz);
	}

	public static double fbm(long seed, long salt, double x, double z, int level) {
		double sum = 0.0;
		double amp = 0.5;
		double freq = 1.0;
		double norm = 0.0;
		for (int octave = 0; octave < 3; octave++) {
			sum += amp * valueNoise(seed, salt + octave * 0x55, x * freq, z * freq, level);
			norm += amp;
			amp *= 0.5;
			freq *= 2.0;
		}
		return sum / norm;
	}
}
