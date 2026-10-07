package net.lixis.outofbound.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

@OnlyIn(Dist.CLIENT)
public final class GifTextureAtlas {

	private static final int MAX_FRAME_SIZE = 128;

	private static final int BACKGROUND_THRESHOLD = 22;

	private static GifTextureAtlas instance;
	private static ResourceLocation loadedFrom;

	private final ResourceLocation textureLocation;
	private final int frameCount;
	private final int frameWidth;
	private final int frameHeight;
	private final int[] frameDelaysCentiseconds;

	private GifTextureAtlas(
			ResourceLocation textureLocation,
			int frameCount,
			int frameWidth,
			int frameHeight,
			int[] frameDelaysCentiseconds) {
		this.textureLocation = textureLocation;
		this.frameCount = frameCount;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.frameDelaysCentiseconds = frameDelaysCentiseconds;
	}

	@Nullable
	public static GifTextureAtlas getOrLoad(ResourceLocation gifPath, ResourceLocation pngFallback) {
		if (instance == null || !gifPath.equals(loadedFrom)) {
			load(gifPath, pngFallback);
		}
		return instance;
	}

	public static void invalidateCache() {
		instance = null;
		loadedFrom = null;
	}

	private static void load(ResourceLocation gifPath, ResourceLocation pngFallback) {
		instance = null;
		loadedFrom = gifPath;
		if (tryLoadGif(gifPath) || tryLoadPngStrip(pngFallback)) {
			return;
		}
		createProceduralFallback();
	}

	private static boolean tryLoadGif(ResourceLocation gifPath) {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			return false;
		}

		Optional<net.minecraft.server.packs.resources.Resource> resource = mc.getResourceManager().getResource(gifPath);
		if (resource.isEmpty()) {
			OutofboundMod.LOGGER.warn("Dark eye GIF not found: {}", gifPath);
			return false;
		}

		try (InputStream input = resource.get().open();
			 ImageInputStream stream = ImageIO.createImageInputStream(input)) {
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
			if (!readers.hasNext()) {
				return false;
			}

			ImageReader reader = readers.next();
			reader.setInput(stream, false);

			int frames = reader.getNumImages(true);
			if (frames <= 0) {
				return false;
			}

			int sourceWidth = reader.getWidth(0);
			int sourceHeight = reader.getHeight(0);
			int frameWidth = Math.min(sourceWidth, MAX_FRAME_SIZE);
			int frameHeight = Math.min(sourceHeight, MAX_FRAME_SIZE);

			BufferedImage canvas = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D canvasGraphics = canvas.createGraphics();
			List<BufferedImage> composited = new ArrayList<>(frames);
			int[] delays = new int[frames];

			for (int i = 0; i < frames; i++) {
				BufferedImage frame = reader.read(i);
				int disposal = readDisposalMethod(reader, i);
				if (disposal == 2) {
					canvasGraphics.setBackground(new java.awt.Color(0, 0, 0, 0));
					canvasGraphics.clearRect(0, 0, sourceWidth, sourceHeight);
				}
				canvasGraphics.drawImage(frame, 0, 0, null);

				BufferedImage snapshot = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D snapshotGraphics = snapshot.createGraphics();
				snapshotGraphics.drawImage(canvas, 0, 0, null);
				snapshotGraphics.dispose();

				BufferedImage scaled = downscale(snapshot, frameWidth, frameHeight);
				keyBackgroundFromEdges(scaled);
				composited.add(scaled);
				delays[i] = Math.max(1, readDelayCentiseconds(reader, i));
			}
			canvasGraphics.dispose();
			reader.dispose();

			NativeImage atlas = new NativeImage(NativeImage.Format.RGBA, frameWidth * frames, frameHeight, false);
			for (int i = 0; i < frames; i++) {
				copyBufferedFrame(atlas, composited.get(i), i * frameWidth, 0);
			}

			registerAtlas(mc, atlas, frames, frameWidth, frameHeight, delays);
			OutofboundMod.LOGGER.info("Loaded dark eye GIF {} ({} frames, {}x{} each)", gifPath, frames, frameWidth, frameHeight);
			return true;
		} catch (Exception exception) {
			OutofboundMod.LOGGER.warn("Failed to load dark eye GIF {}", gifPath, exception);
			return false;
		}
	}

	private static void keyBackgroundFromEdges(BufferedImage frame) {
		int width = frame.getWidth();
		int height = frame.getHeight();
		boolean[][] visited = new boolean[width][height];
		Queue<Long> queue = new ArrayDeque<>();

		for (int x = 0; x < width; x++) {
			enqueueBackgroundPixel(frame, queue, visited, x, 0);
			enqueueBackgroundPixel(frame, queue, visited, x, height - 1);
		}
		for (int y = 0; y < height; y++) {
			enqueueBackgroundPixel(frame, queue, visited, 0, y);
			enqueueBackgroundPixel(frame, queue, visited, width - 1, y);
		}

		while (!queue.isEmpty()) {
			long packed = queue.poll();
			int x = (int) (packed >> 32);
			int y = (int) packed;
			if (x < 0 || x >= width || y < 0 || y >= height || visited[x][y]) {
				continue;
			}
			int argb = frame.getRGB(x, y);
			if (!isBackgroundPixel(argb)) {
				continue;
			}
			visited[x][y] = true;
			frame.setRGB(x, y, argb & 0x00FFFFFF);
			queue.add(pack(x + 1, y));
			queue.add(pack(x - 1, y));
			queue.add(pack(x, y + 1));
			queue.add(pack(x, y - 1));
		}
	}

	private static long pack(int x, int y) {
		return ((long) x << 32) | (y & 0xFFFFFFFFL);
	}

	private static void enqueueBackgroundPixel(BufferedImage frame, Queue<Long> queue, boolean[][] visited, int x, int y) {
		if (x < 0 || x >= frame.getWidth() || y < 0 || y >= frame.getHeight() || visited[x][y]) {
			return;
		}
		if (isBackgroundPixel(frame.getRGB(x, y))) {
			queue.add(pack(x, y));
		}
	}

	private static boolean isBackgroundPixel(int argb) {
		int a = (argb >> 24) & 0xFF;
		if (a == 0) {
			return false;
		}
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		return r <= BACKGROUND_THRESHOLD && g <= BACKGROUND_THRESHOLD && b <= BACKGROUND_THRESHOLD;
	}

	private static BufferedImage downscale(BufferedImage source, int targetWidth, int targetHeight) {
		if (source.getWidth() == targetWidth && source.getHeight() == targetHeight) {
			return source;
		}
		BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = scaled.createGraphics();
		graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
		graphics.dispose();
		return scaled;
	}

	private static boolean tryLoadPngStrip(ResourceLocation pngPath) {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			return false;
		}

		Optional<net.minecraft.server.packs.resources.Resource> resource = mc.getResourceManager().getResource(pngPath);
		if (resource.isEmpty()) {
			return false;
		}

		try (InputStream input = resource.get().open()) {
			NativeImage image = NativeImage.read(input);
			int frameWidth = image.getWidth() / 4;
			int frameHeight = image.getHeight();
			if (frameWidth <= 0 || frameHeight <= 0) {
				image.close();
				return false;
			}

			int[] delays = {10, 10, 10, 10};
			registerAtlas(mc, image, 4, frameWidth, frameHeight, delays);
			OutofboundMod.LOGGER.info("Loaded dark eye PNG strip {}", pngPath);
			return true;
		} catch (Exception exception) {
			OutofboundMod.LOGGER.warn("Failed to load dark eye PNG {}", pngPath, exception);
			return false;
		}
	}

	private static void createProceduralFallback() {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			return;
		}

		int frameWidth = 32;
		int frameHeight = 32;
		int frames = 4;
		NativeImage atlas = new NativeImage(NativeImage.Format.RGBA, frameWidth * frames, frameHeight, false);

		for (int frame = 0; frame < frames; frame++) {
			for (int y = 0; y < frameHeight; y++) {
				for (int x = 0; x < frameWidth; x++) {
					int dx = x - frameWidth / 2;
					int dy = y - frameHeight / 2;
					double dist = Math.sqrt(dx * dx + dy * dy);
					int argb = 0;
					if (dist <= 13.0D) {
						argb = 0xFFFFFFFF;
					}
					if (dist <= 9.0D) {
						argb = 0xFFAA2222;
					}
					int pupil = 4 + frame;
					if (dx * dx + dy * dy <= pupil * pupil) {
						argb = 0xFF000000;
					}
					writePixel(atlas, frame * frameWidth + x, y, argb);
				}
			}
		}

		int[] delays = {10, 10, 10, 10};
		registerAtlas(mc, atlas, frames, frameWidth, frameHeight, delays);
		OutofboundMod.LOGGER.warn("Using procedural fallback texture for dark eye entity");
	}

	private static void registerAtlas(
			Minecraft mc,
			NativeImage atlas,
			int frames,
			int frameWidth,
			int frameHeight,
			int[] delays) {
		DynamicTexture dynamicTexture = new DynamicTexture(atlas);
		ResourceLocation registered = mc.getTextureManager().register("outofbound_dark_eye", dynamicTexture);
		instance = new GifTextureAtlas(registered, frames, frameWidth, frameHeight, delays);
	}

	public ResourceLocation textureLocation() {
		return textureLocation;
	}

	public int frameCount() {
		return frameCount;
	}

	public int frameWidth() {
		return frameWidth;
	}

	public int frameHeight() {
		return frameHeight;
	}

	public int frameIndexForTime(long timeMs) {
		if (frameCount <= 1) {
			return 0;
		}

		int totalDuration = 0;
		for (int delay : frameDelaysCentiseconds) {
			totalDuration += delay;
		}
		if (totalDuration <= 0) {
			return (int) ((timeMs / 100L) % frameCount);
		}

		int elapsed = (int) (timeMs / 10L) % totalDuration;
		int accumulated = 0;
		for (int i = 0; i < frameCount; i++) {
			accumulated += frameDelaysCentiseconds[i];
			if (elapsed < accumulated) {
				return i;
			}
		}
		return frameCount - 1;
	}

	private static int readDisposalMethod(ImageReader reader, int frameIndex) {
		try {
			IIOMetadata metadata = reader.getImageMetadata(frameIndex);
			IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree("javax_imageio_gif_image_1.0");
			IIOMetadataNode extension = findNode(root, "GraphicControlExtension");
			if (extension != null && extension.hasAttribute("disposalMethod")) {
				return Integer.parseInt(extension.getAttribute("disposalMethod"));
			}
		} catch (Exception ignored) {
		}
		return 0;
	}

	private static int readDelayCentiseconds(ImageReader reader, int frameIndex) {
		try {
			IIOMetadata metadata = reader.getImageMetadata(frameIndex);
			IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree("javax_imageio_gif_image_1.0");
			IIOMetadataNode extension = findNode(root, "GraphicControlExtension");
			if (extension != null && extension.hasAttribute("delayTime")) {
				return Integer.parseInt(extension.getAttribute("delayTime"));
			}
		} catch (Exception ignored) {
		}
		return 10;
	}

	@Nullable
	private static IIOMetadataNode findNode(IIOMetadataNode root, String name) {
		for (int i = 0; i < root.getLength(); i++) {
			if (root.item(i) instanceof IIOMetadataNode node && name.equals(node.getNodeName())) {
				return node;
			}
		}
		return null;
	}

	private static void copyBufferedFrame(NativeImage atlas, BufferedImage frame, int offsetX, int offsetY) {
		for (int y = 0; y < frame.getHeight(); y++) {
			for (int x = 0; x < frame.getWidth(); x++) {
				writePixel(atlas, offsetX + x, offsetY + y, frame.getRGB(x, y));
			}
		}
	}

	private static void writePixel(NativeImage atlas, int x, int y, int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		if (a == 0) {
			atlas.setPixelRGBA(x, y, 0);
			return;
		}

		if (r < 90 && g < 90 && b < 90) {
			r = Math.min(255, r + 35);
			g = Math.min(255, g + 35);
			b = Math.min(255, b + 35);
		}

		atlas.setPixelRGBA(x, y, (255 << 24) | (b << 16) | (g << 8) | r);
	}
}
