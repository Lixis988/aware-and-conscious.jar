package net.lixis.outofbound.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.lixis9.eventjar.EventjarMod;
import net.minecraft.resources.ResourceLocation;

public final class MeatPixels {

	private static final Object LOCK = new Object();
	private static NativeImage[] meats;
	private static boolean loggedEmpty;
	private static int cutCount;

	private MeatPixels() {
	}

	public static void cutInto(NativeImage target, ResourceLocation name) {
		if (target == null || name == null || !MeatTextureSwap.shouldCutLoadedTexture(name)) {
			return;
		}
		NativeImage meat = pick(name);
		if (meat == null) {
			if (!loggedEmpty) {
				loggedEmpty = true;
				EventjarMod.LOGGER.warn("Meat pixel sources failed to load; cannot cut textures");
			}
			return;
		}
		int width = target.getWidth();
		int height = target.getHeight();
		int meatW = meat.getWidth();
		int meatH = meat.getHeight();
		if (width <= 0 || height <= 0 || meatW <= 0 || meatH <= 0) {
			return;
		}
		try {
			int painted = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int src = target.getPixelRGBA(x, y);
					int alpha = (src >>> 24) & 0xFF;
					if (alpha == 0) {
						continue;
					}
					int mx = Math.min(meatW - 1, (int) ((long) x * meatW / width));
					int my = Math.min(meatH - 1, (int) ((long) y * meatH / height));
					int fill = meat.getPixelRGBA(mx, my);
					target.setPixelRGBA(x, y, (alpha << 24) | (fill & 0x00FFFFFF));
					painted++;
				}
			}
			if (painted > 0) {
				int n = ++cutCount;
				if (n <= 8 || (n % 500) == 0) {
					EventjarMod.LOGGER.info("Meat-cut {} pixels in {}", painted, name);
				}
			}
		} catch (Exception exception) {
			EventjarMod.LOGGER.debug("Meat cut failed for {}", name, exception);
		}
	}

	private static NativeImage pick(ResourceLocation name) {
		NativeImage[] images = meats();
		if (images.length == 0) {
			return null;
		}
		int index = Math.floorMod(name.getNamespace().hashCode() * 31 + name.getPath().hashCode(), images.length);
		for (int i = 0; i < images.length; i++) {
			NativeImage image = images[(index + i) % images.length];
			if (image != null) {
				return image;
			}
		}
		return null;
	}

	private static NativeImage[] meats() {
		NativeImage[] loaded = meats;
		if (loaded != null) {
			return loaded;
		}
		synchronized (LOCK) {
			if (meats != null) {
				return meats;
			}
			byte[][] bytes = MeatFilePack.meatBytes();
			NativeImage[] images = new NativeImage[bytes.length];
			int ok = 0;
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] == null || bytes[i].length == 0) {
					continue;
				}
				try {
					images[i] = NativeImage.read(bytes[i]);
					ok++;
				} catch (Exception ignored) {
				}
			}
			EventjarMod.LOGGER.info("Loaded {} meat pixel sources", ok);
			meats = images;
			return images;
		}
	}
}
