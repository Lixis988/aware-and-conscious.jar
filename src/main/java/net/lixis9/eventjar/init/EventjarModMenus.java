
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.lixis9.eventjar.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.lixis9.eventjar.world.inventory.Test9Menu;
import net.lixis9.eventjar.world.inventory.Test8Menu;
import net.lixis9.eventjar.world.inventory.Test7Menu;
import net.lixis9.eventjar.world.inventory.Test6Menu;
import net.lixis9.eventjar.world.inventory.Test5Menu;
import net.lixis9.eventjar.world.inventory.Test4Menu;
import net.lixis9.eventjar.world.inventory.Test3Menu;
import net.lixis9.eventjar.world.inventory.Test2Menu;
import net.lixis9.eventjar.world.inventory.Test20Menu;
import net.lixis9.eventjar.world.inventory.Test1Menu;
import net.lixis9.eventjar.world.inventory.Test19Menu;
import net.lixis9.eventjar.world.inventory.Test18Menu;
import net.lixis9.eventjar.world.inventory.Test17Menu;
import net.lixis9.eventjar.world.inventory.Test16Menu;
import net.lixis9.eventjar.world.inventory.Test15Menu;
import net.lixis9.eventjar.world.inventory.Test14Menu;
import net.lixis9.eventjar.world.inventory.Test13Menu;
import net.lixis9.eventjar.world.inventory.Test12Menu;
import net.lixis9.eventjar.world.inventory.Test11Menu;
import net.lixis9.eventjar.world.inventory.Test10Menu;
import net.lixis9.eventjar.world.inventory.Murders100Menu;
import net.lixis9.eventjar.world.inventory.MurderMenu;
import net.lixis9.eventjar.world.inventory.ConfigMenu;
import net.lixis9.eventjar.world.inventory.CardDataMenu;
import net.lixis9.eventjar.world.inventory.AreyouwillingtoshareyourpersonalnformationwithusMenu;
import net.lixis9.eventjar.EventjarMod;

public class EventjarModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, EventjarMod.MODID);
	public static final RegistryObject<MenuType<AreyouwillingtoshareyourpersonalnformationwithusMenu>> AREYOUWILLINGTOSHAREYOURPERSONALNFORMATIONWITHUS = REGISTRY.register("areyouwillingtoshareyourpersonalnformationwithus",
			() -> IForgeMenuType.create(AreyouwillingtoshareyourpersonalnformationwithusMenu::new));
	public static final RegistryObject<MenuType<Test1Menu>> TEST_1 = REGISTRY.register("test_1", () -> IForgeMenuType.create(Test1Menu::new));
	public static final RegistryObject<MenuType<Test2Menu>> TEST_2 = REGISTRY.register("test_2", () -> IForgeMenuType.create(Test2Menu::new));
	public static final RegistryObject<MenuType<MurderMenu>> MURDER = REGISTRY.register("murder", () -> IForgeMenuType.create(MurderMenu::new));
	public static final RegistryObject<MenuType<Murders100Menu>> MURDERS_100 = REGISTRY.register("murders_100", () -> IForgeMenuType.create(Murders100Menu::new));
	public static final RegistryObject<MenuType<Test3Menu>> TEST_3 = REGISTRY.register("test_3", () -> IForgeMenuType.create(Test3Menu::new));
	public static final RegistryObject<MenuType<Test4Menu>> TEST_4 = REGISTRY.register("test_4", () -> IForgeMenuType.create(Test4Menu::new));
	public static final RegistryObject<MenuType<Test5Menu>> TEST_5 = REGISTRY.register("test_5", () -> IForgeMenuType.create(Test5Menu::new));
	public static final RegistryObject<MenuType<Test6Menu>> TEST_6 = REGISTRY.register("test_6", () -> IForgeMenuType.create(Test6Menu::new));
	public static final RegistryObject<MenuType<Test7Menu>> TEST_7 = REGISTRY.register("test_7", () -> IForgeMenuType.create(Test7Menu::new));
	public static final RegistryObject<MenuType<Test8Menu>> TEST_8 = REGISTRY.register("test_8", () -> IForgeMenuType.create(Test8Menu::new));
	public static final RegistryObject<MenuType<Test9Menu>> TEST_9 = REGISTRY.register("test_9", () -> IForgeMenuType.create(Test9Menu::new));
	public static final RegistryObject<MenuType<Test10Menu>> TEST_10 = REGISTRY.register("test_10", () -> IForgeMenuType.create(Test10Menu::new));
	public static final RegistryObject<MenuType<Test11Menu>> TEST_11 = REGISTRY.register("test_11", () -> IForgeMenuType.create(Test11Menu::new));
	public static final RegistryObject<MenuType<Test12Menu>> TEST_12 = REGISTRY.register("test_12", () -> IForgeMenuType.create(Test12Menu::new));
	public static final RegistryObject<MenuType<Test13Menu>> TEST_13 = REGISTRY.register("test_13", () -> IForgeMenuType.create(Test13Menu::new));
	public static final RegistryObject<MenuType<Test14Menu>> TEST_14 = REGISTRY.register("test_14", () -> IForgeMenuType.create(Test14Menu::new));
	public static final RegistryObject<MenuType<Test15Menu>> TEST_15 = REGISTRY.register("test_15", () -> IForgeMenuType.create(Test15Menu::new));
	public static final RegistryObject<MenuType<Test16Menu>> TEST_16 = REGISTRY.register("test_16", () -> IForgeMenuType.create(Test16Menu::new));
	public static final RegistryObject<MenuType<Test17Menu>> TEST_17 = REGISTRY.register("test_17", () -> IForgeMenuType.create(Test17Menu::new));
	public static final RegistryObject<MenuType<Test18Menu>> TEST_18 = REGISTRY.register("test_18", () -> IForgeMenuType.create(Test18Menu::new));
	public static final RegistryObject<MenuType<Test19Menu>> TEST_19 = REGISTRY.register("test_19", () -> IForgeMenuType.create(Test19Menu::new));
	public static final RegistryObject<MenuType<Test20Menu>> TEST_20 = REGISTRY.register("test_20", () -> IForgeMenuType.create(Test20Menu::new));
	public static final RegistryObject<MenuType<CardDataMenu>> CARD_DATA = REGISTRY.register("card_data", () -> IForgeMenuType.create(CardDataMenu::new));
	public static final RegistryObject<MenuType<ConfigMenu>> CONFIG = REGISTRY.register("config", () -> IForgeMenuType.create(ConfigMenu::new));
}
