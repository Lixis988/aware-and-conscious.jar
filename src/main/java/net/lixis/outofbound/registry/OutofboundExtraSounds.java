package net.lixis.outofbound.registry;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.init.OutofboundModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class OutofboundExtraSounds {

	public static final DeferredRegister<SoundEvent> REGISTRY =
			DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OutofboundMod.MODID);

	public static final RegistryObject<SoundEvent> AMBIENT_NOISE = register("ambient_noise");
	public static final RegistryObject<SoundEvent> RANDOM_EVENT = register("random_event");
	public static final RegistryObject<SoundEvent> RANDOM_VOICE = register("random_voice");
	public static final RegistryObject<SoundEvent> DISTANT_STEP = register("distant_step");
	public static final RegistryObject<SoundEvent> MAIN_MENU = register("main_menu");

	public static final RegistryObject<SoundEvent> NOISES = register("noises");
	public static final RegistryObject<SoundEvent> NOISE2 = register("noise2");
	public static final RegistryObject<SoundEvent> NOISE3 = register("noise3");
	public static final RegistryObject<SoundEvent> NOISE4 = register("noise4");
	public static final RegistryObject<SoundEvent> NOISE5 = register("noise5");
	public static final RegistryObject<SoundEvent> NOISE6 = register("noise6");
	public static final RegistryObject<SoundEvent> NOISE7 = register("noise7");
	public static final RegistryObject<SoundEvent> NOISE8 = register("noise8");
	public static final RegistryObject<SoundEvent> NOISE9 = register("noise9");
	public static final RegistryObject<SoundEvent> NOISE10 = register("noise10");
	public static final RegistryObject<SoundEvent> NOISE11 = register("noise11");
	public static final RegistryObject<SoundEvent> NOISE12 = register("noise12");
	public static final RegistryObject<SoundEvent> NOISE13 = register("noise13");

	private static final RegistryObject<SoundEvent>[] DIMENSION_NOISE = new RegistryObject[] {
			OutofboundModSounds.NOISE,
			NOISES,
			NOISE2,
			NOISE3,
			NOISE4,
			NOISE5,
			NOISE6,
			NOISE7,
			NOISE8,
			NOISE9,
			NOISE10,
			NOISE11,
			NOISE12,
			NOISE13
	};

	private OutofboundExtraSounds() {
	}

	public static int dimensionNoiseTrackCount() {
		return DIMENSION_NOISE.length;
	}

	public static SoundEvent dimensionNoiseTrack(int slot) {
		int index = Math.floorMod(slot, DIMENSION_NOISE.length);
		return DIMENSION_NOISE[index].get();
	}

	private static RegistryObject<SoundEvent> register(String name) {
		return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(OutofboundMod.MODID, name)));
	}
}
