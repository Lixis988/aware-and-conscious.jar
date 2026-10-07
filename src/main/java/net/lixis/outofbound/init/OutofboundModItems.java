package net.lixis.outofbound.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.item.Item;

import net.lixis.outofbound.OutofboundMod;

public class OutofboundModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, OutofboundMod.MODID);
	public static final RegistryObject<Item> BOUNDEDCOW_SPAWN_EGG = REGISTRY.register("boundedcow_spawn_egg", () -> new ForgeSpawnEggItem(OutofboundModEntities.BOUNDEDCOW, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> NORMALCOW_SPAWN_EGG = REGISTRY.register("normalcow_spawn_egg", () -> new ForgeSpawnEggItem(OutofboundModEntities.NORMALCOW, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> UNDEFIEND_SPAWN_EGG = REGISTRY.register("undefiend_spawn_egg", () -> new ForgeSpawnEggItem(OutofboundModEntities.UNDEFIEND, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> OVERWORLD_UNDEFIEND_SPAWN_EGG = REGISTRY.register("overworld_undefiend_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundModEntities.OVERWORLD_UNDEFIEND, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> CAVE_UNDEFIEND_SPAWN_EGG = REGISTRY.register("cave_undefiend_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundModEntities.CAVE_UNDEFIEND, -1, -1, new Item.Properties()));
}
