package net.lixis9.eventjar.client;

import net.lixis9.eventjar.init.EventjarModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class BloodRainClientHandler {

	private static final int PARTICLES_PER_TICK = 14;
	private static final double SPAWN_RADIUS = 18.0D;
	private static final double SPAWN_HEIGHT = 12.0D;

	private static int ticksRemaining;

	private BloodRainClientHandler() {
	}

	public static void trigger(int durationTicks) {
		ticksRemaining = Math.max(0, durationTicks);
	}

	public static boolean isActive() {
		return ticksRemaining > 0;
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (ticksRemaining <= 0) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null || mc.isPaused()) {
			return;
		}
		ticksRemaining--;
		spawnDrops(mc.player);
	}

	private static void spawnDrops(LocalPlayer player) {
		SimpleParticleType type = EventjarModParticleTypes.BLOOD_RAIN.get();
		var random = player.getRandom();
		for (int i = 0; i < PARTICLES_PER_TICK; i++) {
			double ox = (random.nextDouble() - 0.5D) * 2.0D * SPAWN_RADIUS;
			double oz = (random.nextDouble() - 0.5D) * 2.0D * SPAWN_RADIUS;
			double oy = SPAWN_HEIGHT + random.nextDouble() * 6.0D;
			double vx = (random.nextDouble() - 0.5D) * 0.03D;
			double vy = -0.55D - random.nextDouble() * 0.35D;
			double vz = (random.nextDouble() - 0.5D) * 0.03D;
			player.level().addParticle(type,
					player.getX() + ox,
					player.getY() + oy,
					player.getZ() + oz,
					vx, vy, vz);
		}
	}

	@SubscribeEvent
	public static void onFogColor(ViewportEvent.ComputeFogColor event) {
		if (ticksRemaining <= 0) {
			return;
		}
		float t = Math.min(1.0F, ticksRemaining / 40.0F);
		t = Math.min(t, 0.75F);
		event.setRed(event.getRed() * (1.0F - t) + 0.45F * t);
		event.setGreen(event.getGreen() * (1.0F - t) + 0.02F * t);
		event.setBlue(event.getBlue() * (1.0F - t) + 0.02F * t);
	}
}
