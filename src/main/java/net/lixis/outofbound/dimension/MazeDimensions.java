package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public final class MazeDimensions {

	public static final String PREFIX = "maze_";

	public static final ResourceKey<DimensionType> DIMENSION_TYPE =
			ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(OutofboundMod.MODID, "maze"));

	private MazeDimensions() {
	}

	public static ResourceLocation locationForIndex(long index) {
		return new ResourceLocation(OutofboundMod.MODID, PREFIX + index);
	}

	public static ResourceKey<Level> levelKey(long index) {
		return ResourceKey.create(Registries.DIMENSION, locationForIndex(index));
	}

	public static ResourceKey<LevelStem> stemKey(long index) {
		return ResourceKey.create(Registries.LEVEL_STEM, locationForIndex(index));
	}

	public static long seedForIndex(long index) {
		long h = (index + 0x9E3779B97F4A7C15L) * 0xC2B2AE3D27D4EB4FL;
		h ^= h >>> 29;
		h *= 0x165667B19E3779F9L;
		h ^= h >>> 32;
		return h;
	}

	public static long indexFromLocation(ResourceLocation location) {
		if (location == null
				|| !OutofboundMod.MODID.equals(location.getNamespace())
				|| !location.getPath().startsWith(PREFIX)) {
			return -1L;
		}
		try {
			return Long.parseLong(location.getPath().substring(PREFIX.length()));
		} catch (NumberFormatException e) {
			return -1L;
		}
	}

	public static boolean isMazeDimension(ResourceLocation location) {
		return indexFromLocation(location) >= 0L;
	}

	public static boolean isUndefiendIndex(long index) {
		return index > 0L && index % 10L == 0L;
	}
}
