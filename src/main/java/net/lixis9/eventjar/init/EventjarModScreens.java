package net.lixis9.eventjar.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import net.lixis9.eventjar.client.gui.Test9Screen;
import net.lixis9.eventjar.client.gui.Test8Screen;
import net.lixis9.eventjar.client.gui.Test7Screen;
import net.lixis9.eventjar.client.gui.Test6Screen;
import net.lixis9.eventjar.client.gui.Test5Screen;
import net.lixis9.eventjar.client.gui.Test4Screen;
import net.lixis9.eventjar.client.gui.Test3Screen;
import net.lixis9.eventjar.client.gui.Test2Screen;
import net.lixis9.eventjar.client.gui.Test20Screen;
import net.lixis9.eventjar.client.gui.Test1Screen;
import net.lixis9.eventjar.client.gui.Test19Screen;
import net.lixis9.eventjar.client.gui.Test18Screen;
import net.lixis9.eventjar.client.gui.Test17Screen;
import net.lixis9.eventjar.client.gui.Test16Screen;
import net.lixis9.eventjar.client.gui.Test15Screen;
import net.lixis9.eventjar.client.gui.Test14Screen;
import net.lixis9.eventjar.client.gui.Test13Screen;
import net.lixis9.eventjar.client.gui.Test12Screen;
import net.lixis9.eventjar.client.gui.Test11Screen;
import net.lixis9.eventjar.client.gui.Test10Screen;
import net.lixis9.eventjar.client.gui.Murders100Screen;
import net.lixis9.eventjar.client.gui.MurderScreen;
import net.lixis9.eventjar.client.gui.ConfigScreen;
import net.lixis9.eventjar.client.gui.CardDataScreen;
import net.lixis9.eventjar.client.gui.AreyouwillingtoshareyourpersonalnformationwithusScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventjarModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(EventjarModMenus.AREYOUWILLINGTOSHAREYOURPERSONALNFORMATIONWITHUS.get(), AreyouwillingtoshareyourpersonalnformationwithusScreen::new);
			MenuScreens.register(EventjarModMenus.TEST_1.get(), Test1Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_2.get(), Test2Screen::new);
			MenuScreens.register(EventjarModMenus.MURDER.get(), MurderScreen::new);
			MenuScreens.register(EventjarModMenus.MURDERS_100.get(), Murders100Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_3.get(), Test3Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_4.get(), Test4Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_5.get(), Test5Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_6.get(), Test6Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_7.get(), Test7Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_8.get(), Test8Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_9.get(), Test9Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_10.get(), Test10Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_11.get(), Test11Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_12.get(), Test12Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_13.get(), Test13Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_14.get(), Test14Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_15.get(), Test15Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_16.get(), Test16Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_17.get(), Test17Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_18.get(), Test18Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_19.get(), Test19Screen::new);
			MenuScreens.register(EventjarModMenus.TEST_20.get(), Test20Screen::new);
			MenuScreens.register(EventjarModMenus.CARD_DATA.get(), CardDataScreen::new);
			MenuScreens.register(EventjarModMenus.CONFIG.get(), ConfigScreen::new);
		});
	}
}
