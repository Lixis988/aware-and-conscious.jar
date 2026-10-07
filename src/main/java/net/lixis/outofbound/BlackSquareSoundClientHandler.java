package net.lixis.outofbound;

import net.lixis.outofbound.entity.BlackSquareEntity;
import net.lixis.outofbound.registry.OutofboundExtraSounds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class BlackSquareSoundClientHandler {

	private static final double TRIGGER_DISTANCE = 64.0D;

	private static SoundInstance chaseInstance;
	private static int chaseEntityId = -1;

	private BlackSquareSoundClientHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null || minecraft.level == null || minecraft.isPaused()) {
			stop(minecraft);
			return;
		}

		if (chaseEntityId != -1) {
			Entity latched = minecraft.level.getEntity(chaseEntityId);
			if (latched instanceof BlackSquareEntity && latched instanceof Mob mob && mob.isAlive() && !mob.isRemoved()) {
				ensurePlaying(minecraft, mob);
				return;
			}
			stop(minecraft);
		}

		BlackSquareEntity nearby = findNearbySquare(minecraft, player);
		if (nearby != null) {
			startInstance(minecraft, nearby);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && event.getEntity() == minecraft.player) {
			stop(minecraft);
		}
	}

	private static BlackSquareEntity findNearbySquare(Minecraft minecraft, LocalPlayer player) {
		AABB box = player.getBoundingBox().inflate(TRIGGER_DISTANCE);
		List<Entity> nearby = minecraft.level.getEntities(player, box,
				entity -> entity instanceof BlackSquareEntity && entity.isAlive() && !entity.isRemoved());

		BlackSquareEntity best = null;
		double bestDist = Double.MAX_VALUE;
		for (Entity entity : nearby) {
			double dist = player.distanceTo(entity);
			if (dist <= TRIGGER_DISTANCE && dist < bestDist) {
				best = (BlackSquareEntity) entity;
				bestDist = dist;
			}
		}
		return best;
	}

	private static void ensurePlaying(Minecraft minecraft, Mob entity) {
		SoundManager soundManager = minecraft.getSoundManager();
		if (chaseInstance != null && chaseEntityId == entity.getId() && soundManager.isActive(chaseInstance)) {
			return;
		}
		startInstance(minecraft, entity);
	}

	private static void startInstance(Minecraft minecraft, Mob entity) {
		SoundManager soundManager = minecraft.getSoundManager();
		if (chaseInstance != null) {
			soundManager.stop(chaseInstance);
		}
		LoopingNoiseSound instance = new LoopingNoiseSound(entity);
		chaseInstance = instance;
		chaseEntityId = entity.getId();
		soundManager.play(chaseInstance);
	}

	private static void stop(Minecraft minecraft) {
		if (minecraft != null && chaseInstance != null) {
			minecraft.getSoundManager().stop(chaseInstance);
		}
		chaseInstance = null;
		chaseEntityId = -1;
	}

	private static final class LoopingNoiseSound extends EntityBoundSoundInstance {

		private LoopingNoiseSound(Mob entity) {
			super(
					OutofboundExtraSounds.AMBIENT_NOISE.get(),
					SoundSource.HOSTILE,
					1.0F,
					0.9F + entity.getRandom().nextFloat() * 0.2F,
					entity,
					entity.getRandom().nextLong());
			this.looping = true;
		}
	}
}
