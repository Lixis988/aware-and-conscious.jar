
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.lixis9.eventjar.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import net.lixis9.eventjar.EventjarMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventjarModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EventjarMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			tabData.accept(EventjarModBlocks.MEETBLOCK.get().asItem());
		} else if (tabData.getTabKey() == CreativeModeTabs.COMBAT) {
			tabData.accept(EventjarModItems.SCISSORS.get());
			tabData.accept(EventjarModItems.REDEMPTION.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(EventjarModItems.MEETBOY_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.WHOAMI_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.ERRUNDEFINE_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.ENTITY_000125_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.ENTITYWHOWATCHYOU_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.ENTITY_CENTIPEDE_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.LIXIS_9_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.RAGE_THEN_60S_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.MEETBOYFAST_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.CARETAKER_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.GOD_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.PLAYER_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.EYES_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.EYE_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.CAVEMEETBOY_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.INVISIBLE_WEIRD_ENTITY_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.NOISE_ENTITY_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.INVISIBLE_DIST_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.SEEKER_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.SEEKERACT_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.WATCHER_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.MEETBOY_LONG_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.FAULT_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.SCAVENGER_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.MEATBOYDISTORTED_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.EYESINDARK_SPAWN_EGG.get());
			tabData.accept(EventjarModItems.SON_OFGOD_SPAWN_EGG.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			tabData.accept(EventjarModBlocks.TASTYBLOCK.get().asItem());
			tabData.accept(EventjarModBlocks.GODISLOVEYOUBLOCK.get().asItem());
			tabData.accept(EventjarModBlocks.EYESSEEMEBLOCK.get().asItem());
		} else if (tabData.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
			tabData.accept(EventjarModItems.T_ASTYBLOCK_122.get());
			tabData.accept(EventjarModItems.RISPERIDONE.get());
			tabData.accept(EventjarModItems.FINGER.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(EventjarModItems.HAMMER.get());
			tabData.accept(EventjarModItems.MEEETTT.get());
			tabData.accept(EventjarModItems.WIERD.get());
			tabData.accept(EventjarModItems.EYESWORLD.get());
		}
	}
}
