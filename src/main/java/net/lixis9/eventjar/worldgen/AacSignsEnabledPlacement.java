package net.lixis9.eventjar.worldgen;

import com.mojang.serialization.Codec;
import net.lixis9.eventjar.AacConfig;
import net.lixis9.eventjar.init.EventjarModPlacementModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public final class AacSignsEnabledPlacement extends PlacementModifier {

	public static final AacSignsEnabledPlacement INSTANCE = new AacSignsEnabledPlacement();
	public static final Codec<AacSignsEnabledPlacement> CODEC = Codec.unit(() -> INSTANCE);

	private AacSignsEnabledPlacement() {
	}

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		if (!AacConfig.ENABLE_SIGNS) {
			return Stream.empty();
		}
		double mult = AacConfig.STRUCTURE_SPAWN_MULTIPLIER;
		if (mult <= 0.0D) {
			return Stream.empty();
		}
		if (mult < 1.0D && random.nextDouble() >= mult) {
			return Stream.empty();
		}
		return Stream.of(pos);
	}

	@Override
	public PlacementModifierType<?> type() {
		return EventjarModPlacementModifiers.AAC_SIGNS_ENABLED.get();
	}
}
