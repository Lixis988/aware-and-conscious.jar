package net.lixis.outofbound.client.renderer;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VanillaBillboardSprites {

	private record Spec(String file, float aspect) {
	}

	private static final Map<EntityType<?>, Spec> CUSTOM_PNG = new LinkedHashMap<>();

	static {
		CUSTOM_PNG.put(EntityType.CHICKEN, new Spec("chicken", 0.832F));
		CUSTOM_PNG.put(EntityType.COW, new Spec("cow", 0.607F));
		CUSTOM_PNG.put(EntityType.SHEEP, new Spec("sheep", 0.854F));
		CUSTOM_PNG.put(EntityType.PIG, new Spec("pig", 1.020F));
		CUSTOM_PNG.put(EntityType.HORSE, new Spec("horse", 0.915F));
		CUSTOM_PNG.put(EntityType.CREEPER, new Spec("creeper", 0.442F));
		CUSTOM_PNG.put(EntityType.SKELETON, new Spec("skeleton", 0.380F));
		CUSTOM_PNG.put(EntityType.ZOMBIE, new Spec("zombie", 0.651F));
		CUSTOM_PNG.put(EntityType.SPIDER, new Spec("spider", 1.373F));
	}

	private static final Map<EntityType<?>, EntityRenderer<?>> RENDERERS = new HashMap<>();

	private VanillaBillboardSprites() {
	}

	public static void registerAll(EntityRenderersEvent.RegisterRenderers event) {
		for (Map.Entry<EntityType<?>, Spec> entry : CUSTOM_PNG.entrySet()) {
			EntityType<?> type = entry.getKey();
			Spec spec = entry.getValue();
			ResourceLocation texture = new ResourceLocation(OutofboundMod.MODID, "textures/entities/" + spec.file() + ".png");
			registerCustom(event, type, texture, spec.aspect());
		}
		OutofboundMod.LOGGER.info("[outofbound] Registered {} custom PNG billboard mobs", CUSTOM_PNG.size());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void registerCustom(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type,
			ResourceLocation texture, float aspect) {
		event.registerEntityRenderer((EntityType) type, (EntityRendererProvider) context -> {
			AspectBillboardEntityRenderer renderer = new AspectBillboardEntityRenderer<>(context, texture, aspect);
			RENDERERS.put(type, renderer);
			return renderer;
		});
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void wrapRemainingVanillaMobs(EntityRenderDispatcher dispatcher,
			Map<EntityType<?>, EntityRenderer<?>> renderers) {
		EntityRendererProvider.Context context = createContext(dispatcher);
		int wrapped = 0;
		for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : Map.copyOf(renderers).entrySet()) {
			EntityType<?> type = entry.getKey();
			EntityRenderer<?> current = entry.getValue();
			if (!shouldAutoBillboard(type, current)) {
				continue;
			}
			VanillaSkinBillboardRenderer billboard =
					new VanillaSkinBillboardRenderer<>(context, (EntityRenderer) current);
			renderers.put(type, billboard);
			RENDERERS.put(type, billboard);
			wrapped++;
		}
		OutofboundMod.LOGGER.info("[outofbound] Wrapped {} vanilla mobs into skin billboards", wrapped);
	}

	public static boolean shouldAutoBillboard(EntityType<?> type, EntityRenderer<?> current) {
		if (current instanceof AspectBillboardEntityRenderer || current instanceof VanillaSkinBillboardRenderer) {
			return false;
		}
		if (CUSTOM_PNG.containsKey(type)) {
			return false;
		}
		ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
		if (id == null || !"minecraft".equals(id.getNamespace())) {
			return false;
		}
		return Mob.class.isAssignableFrom(type.getBaseClass());
	}

	public static EntityRendererProvider.Context createContext(EntityRenderDispatcher dispatcher) {
		Minecraft mc = Minecraft.getInstance();
		return new EntityRendererProvider.Context(
				dispatcher,
				mc.getItemRenderer(),
				mc.getBlockRenderer(),
				dispatcher.getItemInHandRenderer(),
				mc.getResourceManager(),
				mc.getEntityModels(),
				mc.font
		);
	}

	@Nullable
	public static EntityRenderer<?> rendererOf(EntityType<?> type) {
		return RENDERERS.get(type);
	}

	public static boolean hasCustomPng(EntityType<?> type) {
		return CUSTOM_PNG.containsKey(type);
	}

	public static void clearCaches() {
		RENDERERS.clear();
	}

	public static boolean isBillboarded(@Nullable Entity entity) {
		return entity != null && RENDERERS.containsKey(entity.getType());
	}
}
