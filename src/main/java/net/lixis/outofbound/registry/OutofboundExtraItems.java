package net.lixis.outofbound.registry;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.item.MiscEntitySpawnEggItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class OutofboundExtraItems {

	public static final DeferredRegister<Item> REGISTRY =
			DeferredRegister.create(ForgeRegistries.ITEMS, OutofboundMod.MODID);

	public static final RegistryObject<Item> ENTITY1_SPAWN_EGG = REGISTRY.register("entity1_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundExtraEntities.ENTITY1, -3407872, -10092544, new Item.Properties()));

	public static final RegistryObject<Item> ENTITY2_SPAWN_EGG = REGISTRY.register("entity2_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundExtraEntities.ENTITY2, -16777216, -6750208, new Item.Properties()));

	public static final RegistryObject<Item> ENTITY3_SPAWN_EGG = REGISTRY.register("entity3_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundExtraEntities.ENTITY3, -16777216, -14671840, new Item.Properties()));

	public static final RegistryObject<Item> SERAPH_EYE_SPAWN_EGG = REGISTRY.register("seraph_eye_spawn_egg",
			() -> new MiscEntitySpawnEggItem(OutofboundExtraEntities.SERAPH_EYE, 0xF2E6C9, 0x8B1A1A, new Item.Properties()));

	public static final RegistryObject<Item> SERAPH_WRATH_SPAWN_EGG = REGISTRY.register("seraph_wrath_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundExtraEntities.SERAPH_WRATH, 0x8B1A1A, 0xF2E6C9, new Item.Properties()));

	public static final RegistryObject<Item> THE_SUN_SPAWN_EGG = REGISTRY.register("the_sun_spawn_egg",
			() -> new MiscEntitySpawnEggItem(OutofboundExtraEntities.THE_SUN, 0xFFE45C, 0xFF8A00, new Item.Properties()));

	public static final RegistryObject<Item> BACK_TEETHMAN_SPAWN_EGG = REGISTRY.register("back_teethman_spawn_egg",
			() -> new MiscEntitySpawnEggItem(OutofboundExtraEntities.BACK_TEETHMAN, 0x2B2B2B, 0x8B0000, new Item.Properties()));

	public static final RegistryObject<Item> TEETHMAN_SPAWN_EGG = REGISTRY.register("teethman_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundExtraEntities.TEETHMAN, -1, -1, new Item.Properties()));

	public static final RegistryObject<Item> OVERWORLD_TEETHMAN_SPAWN_EGG = REGISTRY.register("overworld_teethman_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundExtraEntities.OVERWORLD_TEETHMAN, -1, -1, new Item.Properties()));

	public static final RegistryObject<Item> NOTEXTUREMAN_SPAWN_EGG = REGISTRY.register("notextureman_spawn_egg",
			() -> new ForgeSpawnEggItem(OutofboundExtraEntities.NOTEXTUREMAN, -1, -1, new Item.Properties()));

	public static final RegistryObject<Item> SKY_PAINTER_SPAWN_EGG = REGISTRY.register("sky_painter_spawn_egg",
			() -> new MiscEntitySpawnEggItem(OutofboundExtraEntities.SKY_PAINTER, 0x1A1030, 0x7A5CFF, new Item.Properties()));

	public static final RegistryObject<Item> OVERWORLD_NOTEXTUREMAN_SPAWN_EGG = REGISTRY.register("overworld_notextureman_spawn_egg",
			() -> new MiscEntitySpawnEggItem(OutofboundExtraEntities.OVERWORLD_NOTEXTUREMAN, 0x1A0000, 0x4A0000, new Item.Properties()));

	public static final RegistryObject<Item> UNKNOWN_SPAWN_EGG = REGISTRY.register("unknown_spawn_egg",
			() -> new MiscEntitySpawnEggItem(OutofboundExtraEntities.UNKNOWN, 0x000000, 0x101010, new Item.Properties()));

	private OutofboundExtraItems() {
	}
}
