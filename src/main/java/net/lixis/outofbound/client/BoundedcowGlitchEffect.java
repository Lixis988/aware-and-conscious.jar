package net.lixis.outofbound.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public final class BoundedcowGlitchEffect {

	private static Matrix4f savedTextureMatrix;

	private BoundedcowGlitchEffect() {
	}

	public static void beginTextureGlitch(float time, int entityId) {
		beginTextureGlitch(time, entityId, 1.0F);
	}

	public static void beginTextureGlitch(float time, int entityId, float intensity) {
		savedTextureMatrix = new Matrix4f(RenderSystem.getTextureMatrix());
		RenderSystem.setTextureMatrix(buildTextureMatrix(time, entityId, intensity));
	}

	public static void endTextureGlitch() {
		if (savedTextureMatrix != null) {
			RenderSystem.setTextureMatrix(savedTextureMatrix);
			savedTextureMatrix = null;
		} else {
			RenderSystem.resetTextureMatrix();
		}
	}

	public static Matrix4f buildTextureMatrix(float time, int seed) {
		return buildTextureMatrix(time, seed, 1.0F);
	}

	public static Matrix4f buildTextureMatrix(float time, int seed, float intensity) {
		float angleZ = glitchAngleRadians(time, seed, 0) * intensity;
		float angleX = glitchAngleRadians(time * 0.73F, seed, 1) * 0.35F * intensity;
		float angleY = glitchAngleRadians(time * 1.17F, seed, 2) * 0.25F * intensity;

		return new Matrix4f()
				.translate(0.5F, 0.5F, 0.0F)
				.rotateZ(angleZ)
				.rotateX(angleX)
				.rotateY(angleY)
				.translate(-0.5F, -0.5F, 0.0F);
	}

	public static float glitchAngleRadians(float time, int seed, int channel) {
		return glitchAngleDegrees(time, seed, channel) * ((float) Math.PI / 180.0F);
	}

	public static float glitchAngleDegrees(float time, int seed, int channel) {
		float speed = 2.1F + (seed % 11) * 0.17F + channel * 0.43F;
		float stepped = (float) Math.floor(time * speed);
		float snap = stepped * (41.0F + (seed % 97) * 1.37F + channel * 53.0F);
		float wobble = (float) Math.sin(time * (9.3F + channel * 2.1F) + seed * 0.71F) * 31.0F;
		float stutter = ((int) (time * 3.7F + seed) % 5 == 0) ? 90.0F + channel * 37.0F : 0.0F;
		return snap + wobble + stutter;
	}
}
