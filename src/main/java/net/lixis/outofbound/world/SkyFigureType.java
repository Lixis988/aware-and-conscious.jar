package net.lixis.outofbound.world;

import java.util.concurrent.ThreadLocalRandom;

public enum SkyFigureType {
	HOLLOW_CUBE(true),
	RING(true),
	SPHERE_SHELL(true),
	SIERPINSKI_CARPET(true),
	PLUS_CROSS(true),
	KOCH_STAR(false),
	SIERPINSKI_TETRAHEDRON(false),
	SPIRAL_TOWER(false),
	WIRE_CUBE(false);

	private final boolean blockBased;

	SkyFigureType(boolean blockBased) {
		this.blockBased = blockBased;
	}

	public boolean isBlockBased() {
		return blockBased;
	}

	public static SkyFigureType random(ThreadLocalRandom random) {
		SkyFigureType[] values = values();
		return values[random.nextInt(values.length)];
	}
}
