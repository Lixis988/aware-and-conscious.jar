package net.lixis.outofbound.registry;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.client.OutofboundMainMenuTitle;
import net.lixis.outofbound.block.OutofboundBlocks;
import net.lixis.outofbound.client.renderer.AnimatedBillboardEntityRenderer;
import net.lixis.outofbound.client.renderer.Entity3Renderer;
import net.lixis.outofbound.client.renderer.ShakingBillboardEntityRenderer;
import net.lixis.outofbound.client.renderer.BillboardEntityRenderer;
import net.lixis.outofbound.client.renderer.SeraphEyeEntityRenderer;
import net.lixis.outofbound.client.renderer.SeraphWrathEntityRenderer;
import net.lixis.outofbound.client.renderer.SkyFigureEntityRenderer;
import net.lixis.outofbound.client.renderer.SunEntityRenderer;
import net.lixis.outofbound.client.renderer.VanillaBillboardSprites;
import net.lixis.outofbound.item.MiscEntitySpawnEggItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.GrassColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class OutofboundExtraClientSetup {

	private OutofboundExtraClientSetup() {
	}

	@SubscribeEvent
	public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> {
			if (stack.getItem() instanceof MiscEntitySpawnEggItem egg) {
				return tintIndex == 0 ? egg.getBackgroundColor() : egg.getHighlightColor();
			}
			return -1;
		}, OutofboundExtraItems.SERAPH_EYE_SPAWN_EGG.get(), OutofboundExtraItems.THE_SUN_SPAWN_EGG.get(),
				OutofboundExtraItems.BACK_TEETHMAN_SPAWN_EGG.get(), OutofboundExtraItems.SKY_PAINTER_SPAWN_EGG.get(),
				OutofboundExtraItems.OVERWORLD_NOTEXTUREMAN_SPAWN_EGG.get(), OutofboundExtraItems.UNKNOWN_SPAWN_EGG.get());
	}

	@SubscribeEvent
	public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		event.register((state, level, pos, tintIndex) -> {
			if (level == null || pos == null) {
				return GrassColor.getDefaultColor();
			}
			return BiomeColors.getAverageGrassColor(level, pos);
		}, OutofboundBlocks.GLITCH_GRASS.get());
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemBlockRenderTypes.setRenderLayer(OutofboundBlocks.GLITCH_GRASS.get(), RenderType.cutoutMipped());
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.getTextureManager() != null) {
				minecraft.getTextureManager().getTexture(OutofboundMainMenuTitle.MINECRAFT_TITLE);
			}
		});
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		VanillaBillboardSprites.registerAll(event);

		event.registerEntityRenderer(OutofboundExtraEntities.ENTITY1.get(),
				context -> new BillboardEntityRenderer<>(context, new ResourceLocation(OutofboundMod.MODID, "textures/entities/entity1.png"), 1.2F, 2.6F));
		event.registerEntityRenderer(OutofboundExtraEntities.ENTITY2.get(),
				context -> new BillboardEntityRenderer<>(context, new ResourceLocation(OutofboundMod.MODID, "textures/entities/entity2.png"), 1.1F, 2.3F));
		event.registerEntityRenderer(OutofboundExtraEntities.ENTITY3.get(), Entity3Renderer::new);
		event.registerEntityRenderer(OutofboundExtraEntities.DARK_EYE.get(),
				context -> new AnimatedBillboardEntityRenderer<>(context, 0.85F, 0.85F));
		event.registerEntityRenderer(OutofboundExtraEntities.BLACK_SQUARE.get(),
				context -> new ShakingBillboardEntityRenderer<>(context,
						new ResourceLocation(OutofboundMod.MODID, "textures/entities/black_square.png"),
						1.0F, 1.0F, 0.12F));
		event.registerEntityRenderer(OutofboundExtraEntities.SKY_FIGURE.get(), SkyFigureEntityRenderer::new);
		event.registerEntityRenderer(OutofboundExtraEntities.SERAPH_EYE.get(), SeraphEyeEntityRenderer::new);
		event.registerEntityRenderer(OutofboundExtraEntities.SERAPH_WRATH.get(), SeraphWrathEntityRenderer::new);
		event.registerEntityRenderer(OutofboundExtraEntities.THE_SUN.get(),
				context -> new SunEntityRenderer(context, 3.0F));
		event.registerEntityRenderer(OutofboundExtraEntities.BACK_TEETHMAN.get(),
				context -> new BillboardEntityRenderer<>(context, new ResourceLocation(OutofboundMod.MODID, "textures/entities/teethman.png"), 1.8F, 4.0F));
		event.registerEntityRenderer(OutofboundExtraEntities.TEETHMAN.get(),
				context -> new BillboardEntityRenderer<>(context, new ResourceLocation(OutofboundMod.MODID, "textures/entities/teethman.png"), 1.8F, 4.0F));
		event.registerEntityRenderer(OutofboundExtraEntities.OVERWORLD_TEETHMAN.get(),
				context -> new BillboardEntityRenderer<>(context, new ResourceLocation(OutofboundMod.MODID, "textures/entities/teethman.png"), 1.8F, 4.0F));
		event.registerEntityRenderer(OutofboundExtraEntities.NOTEXTUREMAN.get(),
				context -> new BillboardEntityRenderer<>(context, new ResourceLocation(OutofboundMod.MODID, "textures/entities/notexture.png"), 0.9F, 2.0F));
		event.registerEntityRenderer(OutofboundExtraEntities.OVERWORLD_NOTEXTUREMAN.get(),
				context -> new BillboardEntityRenderer<>(context, new ResourceLocation(OutofboundMod.MODID, "textures/entities/notextureevil.png"), 0.9F, 2.0F));
		event.registerEntityRenderer(OutofboundExtraEntities.SKY_PAINTER.get(),
				net.minecraft.client.renderer.entity.NoopRenderer::new);
		event.registerEntityRenderer(OutofboundExtraEntities.HEART_DECOR.get(),
				net.lixis.outofbound.client.renderer.HeartDecorRenderer::new);
		event.registerEntityRenderer(OutofboundExtraEntities.UNKNOWN.get(),
				context -> new ShakingBillboardEntityRenderer<>(context,
						new ResourceLocation(OutofboundMod.MODID, "textures/entities/black_square.png"),
						1.8F, 3.8F, 0.15F));
	}

	@SubscribeEvent
	public static void buildTabContents(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(OutofboundExtraItems.ENTITY1_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.ENTITY2_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.ENTITY3_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.SERAPH_EYE_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.SERAPH_WRATH_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.THE_SUN_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.BACK_TEETHMAN_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.TEETHMAN_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.OVERWORLD_TEETHMAN_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.NOTEXTUREMAN_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.SKY_PAINTER_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.OVERWORLD_NOTEXTUREMAN_SPAWN_EGG.get());
			tabData.accept(OutofboundExtraItems.UNKNOWN_SPAWN_EGG.get());
		}
		if (tabData.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			tabData.accept(OutofboundBlocks.DIMENSION_PORTAL_ITEM.get());
			tabData.accept(OutofboundBlocks.OVERWORLD_PORTAL_ITEM.get());
		}
		if (tabData.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			tabData.accept(OutofboundBlocks.GLITCH_GRASS_ITEM.get());
		}
	}
}
