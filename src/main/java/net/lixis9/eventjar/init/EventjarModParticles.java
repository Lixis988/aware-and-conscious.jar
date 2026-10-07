package net.lixis9.eventjar.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.lixis9.eventjar.client.particle.BloodRainParticle;
import net.lixis9.eventjar.client.particle.Meet6Particle;
import net.lixis9.eventjar.client.particle.Meet5Particle;
import net.lixis9.eventjar.client.particle.Meet4Particle;
import net.lixis9.eventjar.client.particle.Meet3Particle;
import net.lixis9.eventjar.client.particle.Meet2Particle;
import net.lixis9.eventjar.client.particle.Meet1Particle;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventjarModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(EventjarModParticleTypes.MEET_1.get(), Meet1Particle::provider);
		event.registerSpriteSet(EventjarModParticleTypes.MEET_2.get(), Meet2Particle::provider);
		event.registerSpriteSet(EventjarModParticleTypes.MEET_3.get(), Meet3Particle::provider);
		event.registerSpriteSet(EventjarModParticleTypes.MEET_4.get(), Meet4Particle::provider);
		event.registerSpriteSet(EventjarModParticleTypes.MEET_5.get(), Meet5Particle::provider);
		event.registerSpriteSet(EventjarModParticleTypes.MEET_6.get(), Meet6Particle::provider);
		event.registerSpriteSet(EventjarModParticleTypes.BLOOD_RAIN.get(), BloodRainParticle::provider);
	}
}
