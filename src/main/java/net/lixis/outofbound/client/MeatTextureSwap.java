package net.lixis.outofbound.client;

import net.lixis9.eventjar.EventjarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class MeatTextureSwap {

	private static final float MAX_COVERAGE = 0.60F;
	private static final float WORLD_FULL_DAYS = 18.0F;
	private static final float WORLD_EASE = 1.6F;
	private static final float ITEM_START_DAY = 10.0F;
	private static final float ITEM_RAMP_DAYS = 8.0F;

	public static final ResourceLocation[] MEAT_TEXTURES = {
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat2.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat3.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat4.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat6.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat7.png")
	};

	private static final ResourceLocation[] MEAT_SPRITES = {
			new ResourceLocation(EventjarMod.MODID, "block/meat"),
			new ResourceLocation(EventjarMod.MODID, "block/meat2"),
			new ResourceLocation(EventjarMod.MODID, "block/meat3"),
			new ResourceLocation(EventjarMod.MODID, "block/meat4"),
			new ResourceLocation(EventjarMod.MODID, "block/meat6"),
			new ResourceLocation(EventjarMod.MODID, "block/meat7")
	};

	private static final List<TextureAtlasSprite> CACHED_BLOCK_MEAT = new ArrayList<>();
	private static final List<TextureAtlasSprite> CACHED_ITEM_MEAT = new ArrayList<>();
	private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> Boolean.FALSE);

	private static float cachedWorldCoverage;
	private static float cachedItemCoverage;
	private static long cachedDays;
	private static int cachedTick = Integer.MIN_VALUE;

	private MeatTextureSwap() {
	}

	private static void refreshCache() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			cachedWorldCoverage = 0.0F;
			cachedItemCoverage = 0.0F;
			cachedDays = 0L;
			cachedTick = Integer.MIN_VALUE;
			return;
		}
		int tick = (int) (mc.level.getGameTime() & 0x7fffffff);
		if (tick == cachedTick) {
			return;
		}
		cachedTick = tick;
		long days = WorldProgressClientState.getDaysSinceBoundedcowCollision(mc);
		cachedDays = days;

		float worldP = (float) Math.pow(Mth.clamp(days / WORLD_FULL_DAYS, 0.0F, 1.0F), WORLD_EASE);
		if (WorldProgressClientState.isSinking()) {
			worldP += 0.08F;
		}
		if (WorldProgressClientState.isInMaze()) {
			worldP += 0.12F;
		}
		cachedWorldCoverage = Mth.clamp(worldP, 0.0F, 1.0F) * MAX_COVERAGE;

		if (days < ITEM_START_DAY) {
			cachedItemCoverage = 0.0F;
		} else {
			float itemP = (days - ITEM_START_DAY) / ITEM_RAMP_DAYS;
			cachedItemCoverage = Mth.clamp(itemP, 0.0F, 1.0F) * MAX_COVERAGE;
		}
	}

	public static float worldCoverage() {
		refreshCache();
		return cachedWorldCoverage;
	}

	public static float itemCoverage() {
		refreshCache();
		return cachedItemCoverage;
	}

	public static boolean isMeatLocation(ResourceLocation location) {
		if (location == null) {
			return false;
		}
		String path = location.getPath();
		return EventjarMod.MODID.equals(location.getNamespace())
				&& (path.contains("meat") || path.contains("meet_"));
	}

	public static boolean isAirLikeLocation(ResourceLocation location) {
		if (location == null) {
			return true;
		}
		String path = location.getPath();
		int slash = path.lastIndexOf('/');
		String leaf = slash >= 0 ? path.substring(slash + 1) : path;
		return leaf.equals("air")
				|| leaf.equals("cave_air")
				|| leaf.equals("void_air")
				|| leaf.equals("structure_void")
				|| leaf.equals("light")
				|| leaf.equals("barrier")
				|| leaf.equals("bubble_column")
				|| leaf.equals("moving_piston")
				|| leaf.equals("missingno")
				|| path.equals("missingno")
				|| path.contains("missing");
	}

	private static boolean isItemTexturePath(String path) {
		return path.startsWith("textures/item") || path.contains("/item/") || path.contains("textures/items");
	}

	private static boolean isWorldTexturePath(String path) {
		return path.startsWith("textures/entity")
				|| path.startsWith("textures/block")
				|| path.contains("/entity/")
				|| path.contains("/block/");
	}

	private static boolean isGuiMenuPath(String path) {
		if (!path.startsWith("textures/gui/")) {
			return false;
		}

		if (path.equals("textures/gui/icons.png") || path.endsWith("/icons.png")) {
			return false;
		}
		return true;
	}

	public static boolean shouldSwapTexture(ResourceLocation location) {
		if (location == null || Boolean.TRUE.equals(REENTRY.get()) || isMeatLocation(location) || isAirLikeLocation(location)) {
			return false;
		}
		String path = location.getPath();
		if (path.startsWith("textures/font") || path.contains("shader") || path.startsWith("shaders/")) {
			return false;
		}

		if (isGuiMenuPath(path)) {
			return true;
		}

		refreshCache();
		float c;
		if (isItemTexturePath(path)) {
			c = cachedItemCoverage;
		} else if (isWorldTexturePath(path)) {
			c = cachedWorldCoverage;
		} else if (cachedDays < ITEM_START_DAY) {
			return false;
		} else {
			c = cachedWorldCoverage;
		}
		if (c <= 0.001F) {
			return false;
		}
		return (mixHash(location) % 10000) < (int) (c * 10000.0F);
	}

	public static void blitMeatBackground(net.minecraft.client.gui.GuiGraphics graphics, int width, int height) {
		blitMeatBackground(graphics, 0, 0, width, height);
	}

	public static void blitMeatBackground(net.minecraft.client.gui.GuiGraphics graphics, int x0, int y0, int width, int height) {
		int tile = 64;
		for (int y = 0; y < height; y += tile) {
			for (int x = 0; x < width; x += tile) {
				int tw = Math.min(tile, width - x);
				int th = Math.min(tile, height - y);
				int meatIndex = Math.floorMod((x / tile) * 31 + (y / tile) * 17, MEAT_TEXTURES.length);
				graphics.blit(MEAT_TEXTURES[meatIndex], x0 + x, y0 + y, 0.0F, 0.0F, tw, th, tile, tile);
			}
		}
	}

	public static void drawSlotWells(net.minecraft.client.gui.GuiGraphics graphics, int leftPos, int topPos,
			java.util.List<? extends net.minecraft.world.inventory.Slot> slots) {
		if (slots == null || slots.isEmpty()) {
			return;
		}
		for (net.minecraft.world.inventory.Slot slot : slots) {
			if (!slot.isActive()) {
				continue;
			}
			int x = leftPos + slot.x;
			int y = topPos + slot.y;

			graphics.fill(x - 1, y - 1, x + 17, y, 0xFF5A2A2A);
			graphics.fill(x - 1, y + 16, x + 17, y + 17, 0xFF1A0808);
			graphics.fill(x - 1, y, x, y + 16, 0xFF5A2A2A);
			graphics.fill(x + 16, y, x + 17, y + 16, 0xFF1A0808);

			graphics.fill(x, y, x + 16, y + 16, 0x99000000);
		}
	}

	public static ResourceLocation meatTextureFor(ResourceLocation original) {
		return MEAT_TEXTURES[Math.floorMod(mixHash(original), MEAT_TEXTURES.length)];
	}

	public static boolean shouldSwapSprite(TextureAtlasSprite sprite) {
		if (sprite == null || Boolean.TRUE.equals(REENTRY.get())) {
			return false;
		}
		ResourceLocation name = sprite.contents().name();
		if (name != null && (isMeatLocation(name) || isAirLikeLocation(name) || name.getPath().contains("meat"))) {
			return false;
		}

		refreshCache();
		ResourceLocation atlas = sprite.atlasLocation();
		String atlasPath = atlas != null ? atlas.getPath() : "";
		boolean itemsAtlas = atlasPath.contains("items");
		boolean blocksAtlas = atlasPath.contains("blocks") || atlasPath.contains("entities");

		float c;
		if (itemsAtlas) {
			c = cachedItemCoverage;
		} else if (blocksAtlas) {
			c = cachedWorldCoverage;
		} else if (cachedDays < ITEM_START_DAY) {
			return false;
		} else {
			c = cachedWorldCoverage;
		}
		if (c <= 0.001F) {
			return false;
		}
		ResourceLocation key = name != null ? name : atlas;
		if (isAirLikeLocation(key)) {
			return false;
		}
		return (mixHash(key) % 10000) < (int) (c * 10000.0F);
	}

	@Nullable
	public static TextureAtlasSprite meatSpriteFor(TextureAtlasSprite original) {
		ensureCached(original.atlasLocation());
		List<TextureAtlasSprite> pool = CACHED_BLOCK_MEAT;
		ResourceLocation atlas = original.atlasLocation();
		if (atlas != null && atlas.getPath().contains("items") && !CACHED_ITEM_MEAT.isEmpty()) {
			pool = CACHED_ITEM_MEAT;
		}
		if (pool.isEmpty()) {
			return null;
		}
		ResourceLocation name = original.contents().name();
		int i = Math.floorMod(mixHash(name != null ? name : atlas), pool.size());
		return pool.get(i);
	}

	private static void ensureCached(ResourceLocation atlasLocation) {
		if (atlasLocation == null || Boolean.TRUE.equals(REENTRY.get())) {
			return;
		}
		boolean items = atlasLocation.getPath().contains("items");
		List<TextureAtlasSprite> pool = items ? CACHED_ITEM_MEAT : CACHED_BLOCK_MEAT;
		if (!pool.isEmpty()) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.getTextureManager() == null) {
			return;
		}
		AbstractTexture texture = mc.getTextureManager().getTexture(atlasLocation);
		if (!(texture instanceof TextureAtlas atlas)) {
			return;
		}
		REENTRY.set(Boolean.TRUE);
		try {
			pool.clear();
			for (ResourceLocation meat : MEAT_SPRITES) {
				TextureAtlasSprite sprite = atlas.getSprite(meat);
				if (sprite != null) {
					pool.add(sprite);
				}
			}
		} catch (Exception ignored) {
		} finally {
			REENTRY.set(Boolean.FALSE);
		}
	}

	public static void clearCache() {
		CACHED_BLOCK_MEAT.clear();
		CACHED_ITEM_MEAT.clear();
		cachedTick = Integer.MIN_VALUE;
		cachedWorldCoverage = 0.0F;
		cachedItemCoverage = 0.0F;
		cachedDays = 0L;
	}

	public static void runWithoutSwap(Runnable action) {
		REENTRY.set(Boolean.TRUE);
		try {
			action.run();
		} finally {
			REENTRY.set(Boolean.FALSE);
		}
	}

	private static int mixHash(ResourceLocation location) {
		int h = location.getNamespace().hashCode() * 31 + location.getPath().hashCode();
		h ^= (h >>> 16);
		h *= 0x45d9f3b;
		h ^= (h >>> 16);
		return h & 0x7fffffff;
	}
}
