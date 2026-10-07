package net.lixis.outofbound;

import net.lixis.outofbound.mixin.PostChainAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TestShaderHandler {

	public static final ResourceLocation GRAPHICAL_BUG =
			new ResourceLocation(OutofboundMod.MODID, "shaders/post/graphical_bug.json");
	public static final ResourceLocation INVERSION =
			new ResourceLocation(OutofboundMod.MODID, "shaders/post/inversion.json");
	public static final ResourceLocation POSTERIZE =
			new ResourceLocation(OutofboundMod.MODID, "shaders/post/posterize.json");
	public static final ResourceLocation TEXTURE_SWAP =
			new ResourceLocation(OutofboundMod.MODID, "shaders/post/texture_swap.json");
	public static final ResourceLocation RANDOM =
			new ResourceLocation(OutofboundMod.MODID, "shaders/post/random_effect.json");

	@Nullable
	private static volatile ResourceLocation manualRequest = null;
	@Nullable
	private static volatile ResourceLocation chaseRequest = null;
	@Nullable
	private static volatile ResourceLocation corruptionRequest = null;
	@Nullable
	private static volatile ResourceLocation dailyRequest = null;

	@Nullable
	private static ResourceLocation loadedShader = null;
	private static boolean loaded = false;

	private TestShaderHandler() {
	}

	@Nullable
	private static ResourceLocation desired() {
		if (manualRequest != null) {
			return manualRequest;
		}
		if (chaseRequest != null) {
			return chaseRequest;
		}
		if (corruptionRequest != null) {
			return corruptionRequest;
		}
		return dailyRequest;
	}

	public static boolean isActive() {
		return desired() != null;
	}

	public static boolean toggleManual(ResourceLocation shader) {
		if (shader.equals(manualRequest)) {
			manualRequest = null;
			resolve();
			return false;
		}
		manualRequest = shader;
		resolve();
		return true;
	}

	public static void setChase(@Nullable ResourceLocation shader) {
		chaseRequest = shader;
		resolve();
	}

	public static void setDaily(@Nullable ResourceLocation shader) {
		dailyRequest = shader;
		resolve();
	}

	public static void setCorruption(@Nullable ResourceLocation shader) {
		corruptionRequest = shader;
		resolve();
	}

	private static void resolve() {
		ensureLoaded();
	}

	public static void ensureLoaded() {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) {
			loaded = false;
			return;
		}
		ResourceLocation want = desired();
		if (want == null || mc.level == null) {
			unload();
			return;
		}
		PostChain current = mc.gameRenderer.currentEffect();
		if (current == null || !want.equals(loadedShader)) {
			if (current != null) {
				mc.gameRenderer.shutdownEffect();
			}
			mc.gameRenderer.loadEffect(want);
			loadedShader = want;
		}
		loaded = mc.gameRenderer.currentEffect() != null;
		applyUniforms(mc);
	}

	private static void applyUniforms(Minecraft mc) {
		if (mc == null) {
			return;
		}
		PostChain chain = mc.gameRenderer.currentEffect();
		if (chain == null) {
			return;
		}
		try {
			List<PostPass> passes = ((PostChainAccessor) chain).outofbound$getPasses();
			if (passes == null) {
				return;
			}
			for (PostPass pass : passes) {
				pass.getEffect().safeGetUniform("Intensity").set(1.0F);
				pass.getEffect().safeGetUniform("Levels").set(4.0F);
			}
		} catch (Throwable ignored) {
		}
	}

	public static void unload() {
		Minecraft mc = Minecraft.getInstance();
		if (mc != null && loaded) {
			mc.gameRenderer.shutdownEffect();
		}
		loaded = false;
		loadedShader = null;
	}
}
