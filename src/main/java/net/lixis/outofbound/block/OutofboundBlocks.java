package net.lixis.outofbound.block;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class OutofboundBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OutofboundMod.MODID);
	public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OutofboundMod.MODID);

	public static final RegistryObject<Block> DIMENSION_PORTAL = BLOCKS.register("dimension_portal",
			() -> new DimensionPortalBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_PURPLE)
					.strength(-1.0F, 3600000.0F)
					.lightLevel(state -> 15)
					.noLootTable()));

	public static final RegistryObject<Block> OVERWORLD_PORTAL = BLOCKS.register("overworld_portal",
			() -> new OverworldPortalBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_LIGHT_GREEN)
					.strength(-1.0F, 3600000.0F)
					.lightLevel(state -> 12)
					.noLootTable()));

	public static final RegistryObject<Item> DIMENSION_PORTAL_ITEM = BLOCK_ITEMS.register("dimension_portal",
			() -> new BlockItem(DIMENSION_PORTAL.get(), new Item.Properties()));

	public static final RegistryObject<Item> OVERWORLD_PORTAL_ITEM = BLOCK_ITEMS.register("overworld_portal",
			() -> new BlockItem(OVERWORLD_PORTAL.get(), new Item.Properties()));

	public static final RegistryObject<Block> GLITCH_GRASS = BLOCKS.register("glitch_grass",
			() -> new GlitchGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK)));

	public static final RegistryObject<Item> GLITCH_GRASS_ITEM = BLOCK_ITEMS.register("glitch_grass",
			() -> new BlockItem(GLITCH_GRASS.get(), new Item.Properties()));

	private OutofboundBlocks() {
	}
}
