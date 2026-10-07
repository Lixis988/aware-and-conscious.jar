package net.lixis9.eventjar.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public final class SeekerGlitchVisuals {

	public static final ResourceLocation SKIN =
			new ResourceLocation("eventjar:textures/entities/fdsafsfsaa222sfafs.png");
	public static final ResourceLocation MEAT =
			new ResourceLocation("eventjar:textures/block/meat.png");

	private SeekerGlitchVisuals() {
	}

	public static ResourceLocation textureFor(LivingEntity entity) {
		int t = entity.tickCount + entity.getId() * 13;
		int burst = t % 41;
		if (burst <= 1 || burst == 17 || (t % 67) < 2 || (t % 29 == 0 && (t / 29) % 4 == 0)) {
			return MEAT;
		}
		return SKIN;
	}

	public static boolean hardSnap(LivingEntity entity) {
		int t = entity.tickCount + entity.getId();
		return (t % 37) < 2 || (t % 53) < 1;
	}

	public static float scaleX(LivingEntity entity, float partial) {
		float t = entity.tickCount + partial;
		int id = entity.getId();
		float s = 0.95F + Mth.sin(t * 0.21F + id) * 0.12F;
		if (hardSnap(entity)) {
			s *= 0.55F + entity.getRandom().nextFloat() * 0.9F;
		}
		return s;
	}

	public static float scaleY(LivingEntity entity, float partial) {
		float t = entity.tickCount + partial;
		int id = entity.getId();
		float s = 1.05F + Mth.cos(t * 0.17F + id * 0.3F) * 0.18F;
		if (hardSnap(entity)) {
			s *= 0.7F + entity.getRandom().nextFloat() * 1.1F;
		}
		return s;
	}

	public static float scaleZ(LivingEntity entity, float partial) {
		float t = entity.tickCount + partial;
		return 0.9F + Mth.sin(t * 0.27F + 1.4F) * 0.14F;
	}
}
