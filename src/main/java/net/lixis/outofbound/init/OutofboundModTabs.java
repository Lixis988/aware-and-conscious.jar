package net.lixis.outofbound.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import net.lixis.outofbound.OutofboundMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class OutofboundModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OutofboundMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(OutofboundModItems.BOUNDEDCOW_SPAWN_EGG.get());
			tabData.accept(OutofboundModItems.NORMALCOW_SPAWN_EGG.get());
			tabData.accept(OutofboundModItems.UNDEFIEND_SPAWN_EGG.get());
			tabData.accept(OutofboundModItems.OVERWORLD_UNDEFIEND_SPAWN_EGG.get());
			tabData.accept(OutofboundModItems.CAVE_UNDEFIEND_SPAWN_EGG.get());
		}
	}
}
