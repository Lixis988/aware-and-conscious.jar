
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mimicevent.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mimicevent.MimiceventMod;

public class MimiceventModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MimiceventMod.MODID);
	public static final RegistryObject<SoundEvent> MIMICATTACK = REGISTRY.register("mimicattack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("mimicevent", "mimicattack")));
}
