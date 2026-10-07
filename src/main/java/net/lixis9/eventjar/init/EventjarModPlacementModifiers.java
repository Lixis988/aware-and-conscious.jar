package net.lixis9.eventjar.init;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.worldgen.AacSignsEnabledPlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class EventjarModPlacementModifiers {

	public static final DeferredRegister<PlacementModifierType<?>> REGISTRY =
			DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, EventjarMod.MODID);

	public static final RegistryObject<PlacementModifierType<AacSignsEnabledPlacement>> AAC_SIGNS_ENABLED =
			REGISTRY.register("aac_signs_enabled", () -> () -> AacSignsEnabledPlacement.CODEC);

	private EventjarModPlacementModifiers() {
	}
}
