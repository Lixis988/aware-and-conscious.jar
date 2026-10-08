package net.lixis.outofbound.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.lixis9.eventjar.EventjarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MeatFilePack {

	public static final String DIR_NAME = "eventjar_meat";
	public static final String PACK_ID = "file/eventjar_meat";
	public static final int CONTENT_VERSION = 5;

	private static final String VERSION_FILE = "eventjar_meat_version";
	private static final String[] PACK_FOLDERS = {
			"textures/block",
			"textures/item",
			"textures/entity",
			"textures/models/armor"
	};
	private static final String[] MEAT_CLASSPATH = {
			"/assets/eventjar/textures/block/meat.png",
			"/assets/eventjar/textures/block/meat2.png",
			"/assets/eventjar/textures/block/meat3.png",
			"/assets/eventjar/textures/block/meat4.png",
			"/assets/eventjar/textures/block/meat6.png",
			"/assets/eventjar/textures/block/meat7.png"
	};

	private static final byte[][] MEAT_BYTES = loadMeatBytes();

	private MeatFilePack() {
	}

	public static Path packDir() {
		return FMLPaths.GAMEDIR.get().resolve("resourcepacks").resolve(DIR_NAME);
	}

	public static boolean ensureGenerated(Minecraft mc) {
		if (isCurrentVersion()) {
			return false;
		}
		try {
			deleteRecursive(packDir());
			writePack(mc);
			EventjarMod.LOGGER.info("Generated meat resource pack v{} at {}", CONTENT_VERSION, packDir());
			return true;
		} catch (Exception exception) {
			EventjarMod.LOGGER.warn("Failed to write meat resource pack", exception);
			return false;
		}
	}

	private static boolean isCurrentVersion() {
		Path root = packDir();
		Path version = root.resolve(VERSION_FILE);
		if (!Files.isRegularFile(root.resolve("pack.mcmeta")) || !Files.isRegularFile(version)) {
			return false;
		}
		try {
			return Integer.parseInt(Files.readString(version, StandardCharsets.UTF_8).trim()) >= CONTENT_VERSION;
		} catch (Exception ignored) {
			return false;
		}
	}

	private static void writePack(Minecraft mc) throws Exception {
		Path root = packDir();
		Files.createDirectories(root);
		deleteRecursive(root.resolve("assets"));
		Files.writeString(root.resolve("pack.mcmeta"), """
				{
				  "pack": {
				    "pack_format": 15,
				    "description": "eventjar meat (shape-preserving)"
				  }
				}
				""", StandardCharsets.UTF_8);

		ResourceManager resources = mc.getResourceManager();
		if (resources == null) {
			return;
		}

		NativeImage[] meats = decodeMeatImages();
		int written = 0;
		try {
			for (String folder : PACK_FOLDERS) {
				written += cutListed(resources, folder, root, meats);
			}
		} finally {
			for (NativeImage meat : meats) {
				if (meat != null) {
					meat.close();
				}
			}
		}
		Files.writeString(root.resolve(VERSION_FILE), Integer.toString(CONTENT_VERSION), StandardCharsets.UTF_8);
		EventjarMod.LOGGER.info("Meat pack wrote {} textures", written);
	}

	private static int cutListed(ResourceManager resources, String folder, Path root, NativeImage[] meats) {
		int written = 0;
		Map<ResourceLocation, Resource> listed = resources.listResources(folder, location -> location.getPath().endsWith(".png"));
		for (ResourceLocation location : listed.keySet()) {
			if (!MeatTextureSwap.isPackReplaceable(location)) {
				continue;
			}
			NativeImage meat = meatImageFor(location, meats);
			if (meat == null) {
				continue;
			}
			Resource originalResource = vanillaResource(resources, location);
			if (originalResource == null) {
				continue;
			}
			Path out = root.resolve("assets").resolve(location.getNamespace()).resolve(location.getPath());
			try {
				Files.createDirectories(out.getParent());
				try (InputStream in = originalResource.open(); NativeImage original = NativeImage.read(in);
						NativeImage cut = cutMeat(original, meat)) {
					cut.writeToFile(out);
					written++;
				}
				copyMcmeta(resources, location, root);
			} catch (Exception exception) {
				EventjarMod.LOGGER.debug("Skipped meat cut for {}", location, exception);
			}
		}
		return written;
	}

	private static Resource vanillaResource(ResourceManager resources, ResourceLocation location) {
		List<Resource> stack = resources.getResourceStack(location);
		for (int i = stack.size() - 1; i >= 0; i--) {
			Resource resource = stack.get(i);
			String packId = resource.sourcePackId();
			if (packId != null && packId.contains("eventjar_meat")) {
				continue;
			}
			return resource;
		}
		return stack.isEmpty() ? null : stack.get(stack.size() - 1);
	}

	private static NativeImage cutMeat(NativeImage original, NativeImage meat) {
		int width = original.getWidth();
		int height = original.getHeight();
		int meatW = meat.getWidth();
		int meatH = meat.getHeight();
		NativeImage cut = new NativeImage(width, height, true);
		if (width <= 0 || height <= 0 || meatW <= 0 || meatH <= 0) {
			return cut;
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int src = original.getPixelRGBA(x, y);
				int alpha = (src >>> 24) & 0xFF;
				if (alpha == 0) {
					cut.setPixelRGBA(x, y, 0);
					continue;
				}
				// Scale across the whole meat photo — corner crop of 542px source looks solid on 16px tiles.
				int mx = Math.min(meatW - 1, (int) ((long) x * meatW / width));
				int my = Math.min(meatH - 1, (int) ((long) y * meatH / height));
				int fill = meat.getPixelRGBA(mx, my);
				cut.setPixelRGBA(x, y, (alpha << 24) | (fill & 0x00FFFFFF));
			}
		}
		return cut;
	}

	private static void copyMcmeta(ResourceManager resources, ResourceLocation png, Path root) {
		ResourceLocation metaLocation = new ResourceLocation(png.getNamespace(), png.getPath() + ".mcmeta");
		Optional<Resource> meta = resources.getResource(metaLocation);
		if (meta.isEmpty() || meta.get().sourcePackId().contains("eventjar_meat")) {
			List<Resource> stack = resources.getResourceStack(metaLocation);
			meta = Optional.empty();
			for (int i = stack.size() - 1; i >= 0; i--) {
				if (!stack.get(i).sourcePackId().contains("eventjar_meat")) {
					meta = Optional.of(stack.get(i));
					break;
				}
			}
		}
		if (meta.isEmpty()) {
			return;
		}
		Path out = root.resolve("assets").resolve(metaLocation.getNamespace()).resolve(metaLocation.getPath());
		try {
			Files.createDirectories(out.getParent());
			try (InputStream in = meta.get().open()) {
				Files.write(out, in.readAllBytes());
			}
		} catch (Exception ignored) {
		}
	}

	private static NativeImage meatImageFor(ResourceLocation location, NativeImage[] meats) {
		if (meats.length == 0) {
			return null;
		}
		int index = Math.floorMod(location.getNamespace().hashCode() * 31 + location.getPath().hashCode(), meats.length);
		for (int i = 0; i < meats.length; i++) {
			NativeImage image = meats[(index + i) % meats.length];
			if (image != null) {
				return image;
			}
		}
		return null;
	}

	private static NativeImage[] decodeMeatImages() {
		NativeImage[] images = new NativeImage[MEAT_BYTES.length];
		for (int i = 0; i < MEAT_BYTES.length; i++) {
			byte[] bytes = MEAT_BYTES[i];
			if (bytes == null || bytes.length == 0) {
				continue;
			}
			try {
				images[i] = NativeImage.read(bytes);
			} catch (Exception ignored) {
			}
		}
		return images;
	}

	private static void deleteRecursive(Path path) throws Exception {
		if (!Files.exists(path)) {
			return;
		}
		try (var walk = Files.walk(path)) {
			walk.sorted(Comparator.reverseOrder()).forEach(entry -> {
				try {
					Files.deleteIfExists(entry);
				} catch (Exception ignored) {
				}
			});
		}
	}

	public static byte[][] meatBytes() {
		return MEAT_BYTES;
	}

	private static byte[][] loadMeatBytes() {
		byte[][] loaded = new byte[MEAT_CLASSPATH.length][];
		for (int i = 0; i < MEAT_CLASSPATH.length; i++) {
			try (InputStream in = MeatFilePack.class.getResourceAsStream(MEAT_CLASSPATH[i])) {
				if (in != null) {
					loaded[i] = in.readAllBytes();
				}
			} catch (Exception ignored) {
			}
		}
		return loaded;
	}
}
