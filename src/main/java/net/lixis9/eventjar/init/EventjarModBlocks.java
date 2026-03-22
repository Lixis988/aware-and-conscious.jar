
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.lixis9.eventjar.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.lixis9.eventjar.block.WierdPortalBlock;
import net.lixis9.eventjar.block.TastyblockBlock;
import net.lixis9.eventjar.block.NoiseblockBlock;
import net.lixis9.eventjar.block.MeetblockBlock;
import net.lixis9.eventjar.block.MeeetttPortalBlock;
import net.lixis9.eventjar.block.GodisloveyoublockBlock;
import net.lixis9.eventjar.block.EyesworldPortalBlock;
import net.lixis9.eventjar.block.EyesseemeblockBlock;
import net.lixis9.eventjar.EventjarMod;

public class EventjarModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, EventjarMod.MODID);
	public static final RegistryObject<Block> MEETBLOCK = REGISTRY.register("meetblock", () -> new MeetblockBlock());
	public static final RegistryObject<Block> TASTYBLOCK = REGISTRY.register("tastyblock", () -> new TastyblockBlock());
	public static final RegistryObject<Block> MEEETTT_PORTAL = REGISTRY.register("meeettt_portal", () -> new MeeetttPortalBlock());
	public static final RegistryObject<Block> GODISLOVEYOUBLOCK = REGISTRY.register("godisloveyoublock", () -> new GodisloveyoublockBlock());
	public static final RegistryObject<Block> WIERD_PORTAL = REGISTRY.register("wierd_portal", () -> new WierdPortalBlock());
	public static final RegistryObject<Block> NOISEBLOCK = REGISTRY.register("noiseblock", () -> new NoiseblockBlock());
	public static final RegistryObject<Block> EYESSEEMEBLOCK = REGISTRY.register("eyesseemeblock", () -> new EyesseemeblockBlock());
	public static final RegistryObject<Block> EYESWORLD_PORTAL = REGISTRY.register("eyesworld_portal", () -> new EyesworldPortalBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
