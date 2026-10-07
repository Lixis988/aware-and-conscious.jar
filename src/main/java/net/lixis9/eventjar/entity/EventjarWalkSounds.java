package net.lixis9.eventjar.entity;

import java.util.Set;

import net.lixis.outofbound.init.OutofboundModSounds;
import net.lixis9.eventjar.EventjarMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class EventjarWalkSounds {

	private static final Set<String> AUXILIARY_PATHS = Set.of(
			"invisible_weird_entity",
			"invisible_dist",
			"noise_entity",
			"player",
			"eyes",
			"eye",
			"eyesindark",
			"seekeract",
			"entitywhowatchyou",
			"entity_000125",
			"errundefine"
	);

	private EventjarWalkSounds() {
	}

	public static boolean shouldReplace(Entity entity) {
		ResourceLocation key = entity.getType().builtInRegistryHolder().key().location();
		if (!EventjarMod.MODID.equals(key.getNamespace())) {
			return false;
		}
		return !AUXILIARY_PATHS.contains(key.getPath());
	}

	public static void play(LivingEntity entity, BlockPos pos, BlockState state) {
		int stepIndex = entity.tickCount;
		SoundEvent step = (stepIndex & 1) == 0
				? OutofboundModSounds.STEP1.get()
				: OutofboundModSounds.STEP2.get();
		entity.playSound(step, 1.0F, 1.0F);
	}
}
