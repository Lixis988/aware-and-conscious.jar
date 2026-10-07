package net.lixis9.eventjar.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.lixis9.eventjar.EventjarMod;

public class EventjarModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EventjarMod.MODID);
	public static final RegistryObject<SimpleParticleType> MEET_1 = REGISTRY.register("meet_1", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> MEET_2 = REGISTRY.register("meet_2", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MEET_3 = REGISTRY.register("meet_3", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MEET_4 = REGISTRY.register("meet_4", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MEET_5 = REGISTRY.register("meet_5", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MEET_6 = REGISTRY.register("meet_6", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> BLOOD_RAIN = REGISTRY.register("blood_rain", () -> new SimpleParticleType(false));
}
