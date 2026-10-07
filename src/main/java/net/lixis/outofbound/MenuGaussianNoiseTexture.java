package net.lixis.outofbound;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MenuGaussianNoiseTexture {

	private static final int SIZE = 32;
	private static final int FRAME_INTERVAL = 2;

	private static DynamicTexture texture;
	private static ResourceLocation location;
	private static long lastUploadedFrame = -1L;

	private MenuGaussianNoiseTexture() {
	}

	public static ResourceLocation getForFrame(long frame) {
		Minecraft mc = Minecraft.getInstance();
		if (texture == null) {
			init(mc);
		}

		long uploadFrame = frame / FRAME_INTERVAL;
		if (uploadFrame != lastUploadedFrame) {
			fillGaussianNoise(uploadFrame);
			texture.upload();
			lastUploadedFrame = uploadFrame;
		}
		return location;
	}

	private static void init(Minecraft mc) {
		texture = new DynamicTexture(SIZE, SIZE, true);
		location = new ResourceLocation(OutofboundMod.MODID, "dynamic/menu_gaussian_noise");
		mc.getTextureManager().register(location, texture);
	}

	private static float hashUniform(int x, int y, int salt, long frame) {
		long h = frame;
		h ^= (long) x * 374761393L;
		h ^= (long) y * 668265263L;
		h ^= (long) salt * 144269504088896L;
		h ^= h >>> 33;
		h *= 0xff51afd7ed558ccdL;
		h ^= h >>> 33;
		h *= 0xc4ceb9fe1a85ec53L;
		h ^= h >>> 33;
		return (h & 0xFFFFFFL) / 16777216.0F;
	}

	private static float gaussian(int x, int y, int channel, long frame) {
		float sum = 0.0F;
		for (int i = 0; i < 8; i++) {
			sum += hashUniform(x, y, channel * 8 + i, frame);
		}
		return (sum - 4.0F) * 0.5F;
	}

	private static int channel(float sample) {
		int v = (int) ((sample * 0.45F + 0.5F) * 255.0F);
		return Math.max(0, Math.min(255, v));
	}

	private static void fillGaussianNoise(long frame) {
		NativeImage image = texture.getPixels();

		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				int r = channel(gaussian(x, y, 0, frame));
				int g = channel(gaussian(x, y, 1, frame));
				int b = channel(gaussian(x, y, 2, frame));
				int a = 130;

				image.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
			}
		}
	}
}
