package net.lixis.outofbound.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.lixis.outofbound.Authorship;
import net.lixis.outofbound.OutofboundMod;

public class OutofboundModSounds {

	@SuppressWarnings("unused")
	private static final String OWNERSHIP = Authorship.NOTICE;

	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OutofboundMod.MODID);
	public static final RegistryObject<SoundEvent> NOISE = REGISTRY.register("noise", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("outofbound", "noise")));
	public static final RegistryObject<SoundEvent> STEP1 = REGISTRY.register("step1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("outofbound", "step1")));
	public static final RegistryObject<SoundEvent> STEP2 = REGISTRY.register("step2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("outofbound", "step2")));
	public static final RegistryObject<SoundEvent> CHASE = REGISTRY.register("chase", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("outofbound", "chase")));
}
