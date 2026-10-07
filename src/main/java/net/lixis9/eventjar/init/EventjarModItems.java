package net.lixis9.eventjar.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.lixis9.eventjar.item.WierdItem;
import net.lixis9.eventjar.item.TAstyblock122Item;
import net.lixis9.eventjar.item.ScissorsItem;
import net.lixis9.eventjar.item.RisperidoneItem;
import net.lixis9.eventjar.item.RedemptionItem;
import net.lixis9.eventjar.item.MeeetttItem;
import net.lixis9.eventjar.item.HammerItem;
import net.lixis9.eventjar.item.FingerItem;
import net.lixis9.eventjar.item.EyesworldItem;
import net.lixis9.eventjar.EventjarMod;

public class EventjarModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, EventjarMod.MODID);
	public static final RegistryObject<Item> MEETBOY_SPAWN_EGG = REGISTRY.register("meetboy_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.MEETBOY, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> MEETBOY_GLITCH_SPAWN_EGG = REGISTRY.register("meetboy_glitch_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.MEETBOY_GLITCH, -16777216, -3407872, new Item.Properties()));
	public static final RegistryObject<Item> MEETBOY_ELONGATED_SPAWN_EGG = REGISTRY.register("meetboy_elongated_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.MEETBOY_ELONGATED, -16777216, -65536, new Item.Properties()));
	public static final RegistryObject<Item> MEETBOY_STUBBY_SPAWN_EGG = REGISTRY.register("meetboy_stubby_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.MEETBOY_STUBBY, -16777216, -256, new Item.Properties()));
	public static final RegistryObject<Item> MEETBLOCK = block(EventjarModBlocks.MEETBLOCK);
	public static final RegistryObject<Item> WHOAMI_SPAWN_EGG = REGISTRY.register("whoami_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.WHOAMI, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> ERRUNDEFINE_SPAWN_EGG = REGISTRY.register("errundefine_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.ERRUNDEFINE, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> TASTYBLOCK = block(EventjarModBlocks.TASTYBLOCK);
	public static final RegistryObject<Item> T_ASTYBLOCK_122 = REGISTRY.register("t_astyblock_122", () -> new TAstyblock122Item());
	public static final RegistryObject<Item> ENTITY_000125_SPAWN_EGG = REGISTRY.register("entity_000125_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.ENTITY_000125, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> ENTITYWHOWATCHYOU_SPAWN_EGG = REGISTRY.register("entitywhowatchyou_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.ENTITYWHOWATCHYOU, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> ENTITY_CENTIPEDE_SPAWN_EGG = REGISTRY.register("entity_centipede_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.ENTITY_CENTIPEDE, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> RISPERIDONE = REGISTRY.register("risperidone", () -> new RisperidoneItem());
	public static final RegistryObject<Item> LIXIS_9_SPAWN_EGG = REGISTRY.register("lixis_9_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.LIXIS_9, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> HAMMER = REGISTRY.register("hammer", () -> new HammerItem());
	public static final RegistryObject<Item> MEEETTT = REGISTRY.register("meeettt", () -> new MeeetttItem());
	public static final RegistryObject<Item> RAGE_THEN_60S_SPAWN_EGG = REGISTRY.register("rage_then_60s_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.RAGE_THEN_60S, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> GODISLOVEYOUBLOCK = block(EventjarModBlocks.GODISLOVEYOUBLOCK);
	public static final RegistryObject<Item> MEETBOYFAST_SPAWN_EGG = REGISTRY.register("meetboyfast_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.MEETBOYFAST, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> CARETAKER_SPAWN_EGG = REGISTRY.register("caretaker_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.CARETAKER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> GOD_SPAWN_EGG = REGISTRY.register("god_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.GOD, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> PLAYER_SPAWN_EGG = REGISTRY.register("player_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.PLAYER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> FINGER = REGISTRY.register("finger", () -> new FingerItem());
	public static final RegistryObject<Item> SCISSORS = REGISTRY.register("scissors", () -> new ScissorsItem());
	public static final RegistryObject<Item> EYES_SPAWN_EGG = REGISTRY.register("eyes_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.EYES, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> WIERD = REGISTRY.register("wierd", () -> new WierdItem());
	public static final RegistryObject<Item> EYE_SPAWN_EGG = REGISTRY.register("eye_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.EYE, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> CAVEMEETBOY_SPAWN_EGG = REGISTRY.register("cavemeetboy_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.CAVEMEETBOY, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> INVISIBLE_WEIRD_ENTITY_SPAWN_EGG = REGISTRY.register("invisible_weird_entity_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.INVISIBLE_WEIRD_ENTITY, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> REDEMPTION = REGISTRY.register("redemption", () -> new RedemptionItem());
	public static final RegistryObject<Item> NOISE_ENTITY_SPAWN_EGG = REGISTRY.register("noise_entity_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.NOISE_ENTITY, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> NOISEBLOCK = block(EventjarModBlocks.NOISEBLOCK);
	public static final RegistryObject<Item> INVISIBLE_DIST_SPAWN_EGG = REGISTRY.register("invisible_dist_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.INVISIBLE_DIST, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> SEEKER_SPAWN_EGG = REGISTRY.register("seeker_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.SEEKER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> SEEKERACT_SPAWN_EGG = REGISTRY.register("seekeract_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.SEEKERACT, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> WATCHER_SPAWN_EGG = REGISTRY.register("watcher_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.WATCHER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> MEETBOY_LONG_SPAWN_EGG = REGISTRY.register("meetboy_long_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.MEETBOY_LONG, -3407872, -1, new Item.Properties()));
	public static final RegistryObject<Item> FAULT_SPAWN_EGG = REGISTRY.register("fault_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.FAULT, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> SCAVENGER_SPAWN_EGG = REGISTRY.register("scavenger_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.SCAVENGER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> EYESSEEMEBLOCK = block(EventjarModBlocks.EYESSEEMEBLOCK);
	public static final RegistryObject<Item> EYESWORLD = REGISTRY.register("eyesworld", () -> new EyesworldItem());
	public static final RegistryObject<Item> MEATBOYDISTORTED_SPAWN_EGG = REGISTRY.register("meatboydistorted_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.MEATBOYDISTORTED, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> EYESINDARK_SPAWN_EGG = REGISTRY.register("eyesindark_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.EYESINDARK, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> SON_OFGOD_SPAWN_EGG = REGISTRY.register("son_ofgod_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.SON_OFGOD, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> DEFAULT_VILLAGER_SPAWN_EGG = REGISTRY.register("default_villager_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.DEFAULT_VILLAGER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> SCARY_DEFAULT_VILLAGER_SPAWN_EGG = REGISTRY.register("scary_default_villager_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.SCARY_DEFAULT_VILLAGER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> VILLAGER_MIMIC_SPAWN_EGG = REGISTRY.register("villager_mimic_spawn_egg", () -> new ForgeSpawnEggItem(EventjarModEntities.VILLAGER_MIMIC, 0x563C33, 0x000000, new Item.Properties()));

	public static final RegistryObject<Item> MEAT = block(EventjarModBlocks.MEAT);
	public static final RegistryObject<Item> MEAT2 = block(EventjarModBlocks.MEAT2);
	public static final RegistryObject<Item> MEAT3 = block(EventjarModBlocks.MEAT3);
	public static final RegistryObject<Item> MEAT4 = block(EventjarModBlocks.MEAT4);
	public static final RegistryObject<Item> MEAT6 = block(EventjarModBlocks.MEAT6);
	public static final RegistryObject<Item> MEAT7 = block(EventjarModBlocks.MEAT7);

	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
