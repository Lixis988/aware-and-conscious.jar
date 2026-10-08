package net.lixis.outofbound.client;

import net.lixis.outofbound.world.MeatGameMode;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.lixis9.eventjar.AacConfig;
import net.lixis9.eventjar.EventjarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EventjarMod.MODID, value = Dist.CLIENT)
public final class MeatTextureSwap {

	public static final ResourceLocation[] MEAT_TEXTURES = {
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat2.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat3.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat4.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat6.png"),
			new ResourceLocation(EventjarMod.MODID, "textures/block/meat7.png")
	};

	private static final long SWAP_START_DAY = 10L;
	private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> Boolean.FALSE);

	private static long cachedDays;
	private static int cachedTick = Integer.MIN_VALUE;
	private static volatile boolean packActive;
	private static volatile boolean reloadQueued;
	private static volatile boolean packFailed;
	private static String resolvedPackId = MeatFilePack.PACK_ID;

	private MeatTextureSwap() {
	}

	private static void refreshDays() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			cachedDays = 0L;
			cachedTick = Integer.MIN_VALUE;
			return;
		}
		int tick = (int) (mc.level.getGameTime() & 0x7fffffff);
		if (tick == cachedTick) {
			return;
		}
		cachedTick = tick;
		cachedDays = WorldProgressClientState.getDaysSinceBoundedcowCollision(mc);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || reloadQueued || packFailed) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			if (packActive) {
				setPackEnabled(false);
			}
			return;
		}
		boolean want = worldMeatReady();
		boolean selected = isMeatPackSelected(mc.getResourcePackRepository());
		if (want == packActive && want == selected) {
			return;
		}
		setPackEnabled(want);
	}

	public static void requestEnable() {
		packFailed = false;
	}

	public static boolean worldMeatReady() {
		if (!AacConfig.meatSwapEnabled() || AacConfig.meatSwapStrength() <= 0.001F) {
			return false;
		}
		if (MeatGameMode.isPendingCreate() || WorldProgressClientState.isMeatGameMode()) {
			return true;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.getSingleplayerServer() != null && WorldInternalConfig.isMeatGameMode(mc.getSingleplayerServer())) {
			return true;
		}
		refreshDays();
		return cachedDays >= SWAP_START_DAY;
	}

	public static boolean isActive() {
		return packActive;
	}

	public static boolean shouldSwapTexture(ResourceLocation location) {
		if (!AacConfig.meatSwapEnabled()) {
			return false;
		}
		if (location == null || Boolean.TRUE.equals(REENTRY.get()) || isMeatLocation(location) || isAirLikeLocation(location)) {
			return false;
		}
		String path = location.getPath();
		if (path.startsWith("textures/font") || path.contains("shader") || path.startsWith("shaders/")
				|| path.startsWith("textures/atlas") || path.contains("/atlas/")
				|| path.startsWith("textures/colormap") || path.startsWith("textures/misc")
				|| path.contains("lightmap")) {
			return false;
		}
		return isGuiMenuPath(path);
	}

	public static boolean shouldCutLoadedTexture(ResourceLocation location) {
		return false;
	}

	public static boolean isPackReplaceable(ResourceLocation location) {
		if (location == null || isMeatLocation(location) || isAirLikeLocation(location)) {
			return false;
		}
		String path = location.getPath();
		if (path.endsWith(".png")) {
			path = path.substring(0, path.length() - 4);
		}
		if (path.startsWith("textures/")) {
			path = path.substring("textures/".length());
		}
		if (path.startsWith("font") || path.startsWith("gui") || path.startsWith("atlas") || path.contains("/atlas/")
				|| path.startsWith("colormap") || path.startsWith("misc") || path.contains("shader")
				|| path.contains("lightmap") || path.contains("font/")) {
			return false;
		}
		return path.startsWith("block/")
				|| path.startsWith("item/")
				|| path.startsWith("entity/")
				|| path.startsWith("models/armor")
				|| path.startsWith("painting/")
				|| path.startsWith("particle/")
				|| path.contains("/block/")
				|| path.contains("/item/")
				|| path.contains("/entity/");
	}

	public static boolean isMeatLocation(ResourceLocation location) {
		if (location == null) {
			return false;
		}
		String path = location.getPath();
		return EventjarMod.MODID.equals(location.getNamespace())
				&& (path.contains("/meat") || path.endsWith("meat") || path.contains("meat.")
				|| path.contains("meat2") || path.contains("meat3") || path.contains("meat4")
				|| path.contains("meat6") || path.contains("meat7") || path.contains("meet_"));
	}

	public static boolean isAirLikeLocation(ResourceLocation location) {
		if (location == null) {
			return true;
		}
		String path = location.getPath();
		int slash = path.lastIndexOf('/');
		String leaf = slash >= 0 ? path.substring(slash + 1) : path;
		if (leaf.endsWith(".png")) {
			leaf = leaf.substring(0, leaf.length() - 4);
		}
		return leaf.equals("air")
				|| leaf.equals("cave_air")
				|| leaf.equals("void_air")
				|| leaf.equals("structure_void")
				|| leaf.equals("light")
				|| leaf.equals("barrier")
				|| leaf.equals("bubble_column")
				|| leaf.equals("moving_piston")
				|| leaf.equals("missingno")
				|| path.equals("missingno");
	}

	private static boolean isGuiMenuPath(String path) {
		if (!path.startsWith("textures/gui/")) {
			return false;
		}
		return !path.equals("textures/gui/icons.png") && !path.endsWith("/icons.png");
	}

	public static void blitMeatBackground(net.minecraft.client.gui.GuiGraphics graphics, int width, int height) {
		blitMeatBackground(graphics, 0, 0, width, height);
	}

	public static void blitMeatBackground(net.minecraft.client.gui.GuiGraphics graphics, int x0, int y0, int width, int height) {
		if (!AacConfig.meatSwapEnabled()) {
			return;
		}
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
		if (!AacConfig.meatSwapEnabled() || slots == null || slots.isEmpty()) {
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

	public static void runWithoutSwap(Runnable action) {
		REENTRY.set(Boolean.TRUE);
		try {
			action.run();
		} finally {
			REENTRY.set(Boolean.FALSE);
		}
	}

	public static void clearCache() {
		cachedTick = Integer.MIN_VALUE;
		cachedDays = 0L;
		packFailed = false;
	}

	private static void setPackEnabled(boolean enable) {
		if (reloadQueued) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		PackRepository repo = mc.getResourcePackRepository();
		if (repo == null) {
			return;
		}

		if (enable) {
			MeatFilePack.ensureGenerated(mc);
			repo.reload();
			String packId = resolvePackId(repo.getAvailableIds());
			if (packId == null) {
				EventjarMod.LOGGER.warn("Meat pack not found after generate. available={}", repo.getAvailableIds());
				packActive = false;
				packFailed = true;
				return;
			}
			resolvedPackId = packId;
			List<String> selected = new ArrayList<>(repo.getSelectedIds());
			if (selected.contains(packId)) {
				packActive = true;
				return;
			}
			selected.add(packId);
			applySelection(mc, repo, selected, true);
			return;
		}

		List<String> selected = new ArrayList<>(repo.getSelectedIds());
		boolean removed = selected.removeIf(id -> id != null && id.contains(MeatFilePack.DIR_NAME));
		if (!removed && !packActive) {
			return;
		}
		applySelection(mc, repo, selected, false);
	}

	private static void applySelection(Minecraft mc, PackRepository repo, List<String> selected, boolean enable) {
		reloadQueued = true;
		repo.setSelected(selected);
		mc.options.updateResourcePacks(repo);
		EventjarMod.LOGGER.info("Meat pack toggle enable={} id={}", enable, resolvedPackId);
		mc.reloadResourcePacks().whenComplete((ignored, error) -> mc.execute(() -> {
			reloadQueued = false;
			if (error != null) {
				EventjarMod.LOGGER.warn("Meat pack reload failed", error);
				packActive = false;
				packFailed = true;
				return;
			}
			packActive = enable && isMeatPackSelected(repo);
			if (enable && !packActive) {
				EventjarMod.LOGGER.warn("Meat pack missing after reload, giving up. selected={}", repo.getSelectedIds());
				packFailed = true;
			}
		}));
	}

	private static boolean isMeatPackSelected(PackRepository repo) {
		if (repo == null) {
			return false;
		}
		for (String id : repo.getSelectedIds()) {
			if (id != null && id.contains(MeatFilePack.DIR_NAME)) {
				resolvedPackId = id;
				return true;
			}
		}
		return false;
	}

	private static String resolvePackId(Collection<String> available) {
		if (available.contains(MeatFilePack.PACK_ID)) {
			return MeatFilePack.PACK_ID;
		}
		for (String id : available) {
			if (id != null && id.contains(MeatFilePack.DIR_NAME)) {
				return id;
			}
		}
		return null;
	}

	private static int mixHash(ResourceLocation location) {
		int h = location.getNamespace().hashCode() * 31 + location.getPath().hashCode();
		h ^= (h >>> 16);
		h *= 0x45d9f3b;
		h ^= (h >>> 16);
		return h & 0x7fffffff;
	}
}
