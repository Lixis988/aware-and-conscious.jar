package net.lixis9.eventjar.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.lixis9.eventjar.potion.TexturesBrokeMobEffect;
import net.lixis9.eventjar.potion.OverdoseMobEffect;
import net.lixis9.eventjar.potion.ConscienceMobEffect;
import net.lixis9.eventjar.potion.BrokenScreanEffectMobEffect;
import net.lixis9.eventjar.EventjarMod;

public class EventjarModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EventjarMod.MODID);
	public static final RegistryObject<MobEffect> CONSCIENCE = REGISTRY.register("conscience", () -> new ConscienceMobEffect());
	public static final RegistryObject<MobEffect> TEXTURES_BROKE = REGISTRY.register("textures_broke", () -> new TexturesBrokeMobEffect());
	public static final RegistryObject<MobEffect> BROKEN_SCREAN_EFFECT = REGISTRY.register("broken_screan_effect", () -> new BrokenScreanEffectMobEffect());
	public static final RegistryObject<MobEffect> OVERDOSE = REGISTRY.register("overdose", () -> new OverdoseMobEffect());
}
