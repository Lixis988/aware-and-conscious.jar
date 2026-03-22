
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mimicevent.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.item.Item;

import net.mcreator.mimicevent.MimiceventMod;

public class MimiceventModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MimiceventMod.MODID);
	public static final RegistryObject<Item> DEFAULT_VILLAGER_SPAWN_EGG = REGISTRY.register("default_villager_spawn_egg", () -> new ForgeSpawnEggItem(MimiceventModEntities.DEFAULT_VILLAGER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> SCARY_DEFAULT_VILLAGER_SPAWN_EGG = REGISTRY.register("scary_default_villager_spawn_egg", () -> new ForgeSpawnEggItem(MimiceventModEntities.SCARY_DEFAULT_VILLAGER, -1, -1, new Item.Properties()));
	// Start of user code block custom items
	// End of user code block custom items
}
