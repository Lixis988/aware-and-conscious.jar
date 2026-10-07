package net.lixis9.eventjar.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodRainParticle extends TextureSheetParticle {

	public static BloodRainParticleProvider provider(SpriteSet spriteSet) {
		return new BloodRainParticleProvider(spriteSet);
	}

	public static class BloodRainParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public BloodRainParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			return new BloodRainParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	protected BloodRainParticle(ClientLevel level, double x, double y, double z,
			double vx, double vy, double vz, SpriteSet spriteSet) {
		super(level, x, y, z);
		this.setSize(0.04F, 0.22F);
		this.quadSize = 0.07F + this.random.nextFloat() * 0.08F;
		this.lifetime = 18 + this.random.nextInt(28);
		this.gravity = 0.85F;
		this.hasPhysics = true;
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		float shade = 0.45F + this.random.nextFloat() * 0.35F;
		this.setColor(shade, 0.02F + this.random.nextFloat() * 0.04F, 0.02F);
		this.setAlpha(0.85F);
		this.pickSprite(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.onGround) {
			this.remove();
		}
	}
}
