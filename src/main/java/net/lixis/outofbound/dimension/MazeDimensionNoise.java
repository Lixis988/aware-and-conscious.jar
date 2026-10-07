package net.lixis.outofbound.dimension;

import net.lixis.outofbound.registry.OutofboundExtraSounds;
import net.minecraft.sounds.SoundEvent;

public final class MazeDimensionNoise {

	private MazeDimensionNoise() {
	}

	public static int trackCount() {
		return OutofboundExtraSounds.dimensionNoiseTrackCount();
	}

	public static SoundEvent soundForIndex(long mazeIndex) {
		int slot = (int) Math.floorMod(MazeDimensions.seedForIndex(mazeIndex), trackCount());
		return OutofboundExtraSounds.dimensionNoiseTrack(slot);
	}

	public static SoundEvent defaultSound() {
		return OutofboundExtraSounds.dimensionNoiseTrack(0);
	}
}
