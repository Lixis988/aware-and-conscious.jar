package net.lixis.outofbound;

import net.lixis.outofbound.client.UndefiendLookAtHelper;
import net.lixis.outofbound.entity.ChaserEntity;
import net.lixis.outofbound.init.OutofboundModSounds;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ChaserMusicClientHandler {

	private static final double TRIGGER_FOV_DEGREES = 50.0D;
	private static final double TRIGGER_DISTANCE = 128.0D;

	private static SoundInstance chaseInstance;
	private static int chaseEntityId = -1;

	private ChaserMusicClientHandler() {
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
			if (latched instanceof ChaserEntity && latched instanceof Mob mob && mob.isAlive() && !mob.isRemoved()) {
				ensurePlaying(minecraft, mob);
				return;
			}
			stop(minecraft);
		}

		Mob seen = findChaserInCamera(minecraft, player);
		if (seen != null) {
			startInstance(minecraft, seen);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && event.getEntity() == minecraft.player) {
			stop(minecraft);
		}
	}

	private static Mob findChaserInCamera(Minecraft minecraft, LocalPlayer player) {
		AABB box = player.getBoundingBox().inflate(TRIGGER_DISTANCE);
		List<Entity> nearby = minecraft.level.getEntities(player, box,
				entity -> entity instanceof ChaserEntity && entity.isAlive() && !entity.isRemoved());

		Mob best = null;
		double bestAngle = Double.MAX_VALUE;
		for (Entity entity : nearby) {
			if (!(entity instanceof Mob mob)) {
				continue;
			}
			if (player.distanceTo(mob) > TRIGGER_DISTANCE || !player.hasLineOfSight(mob)) {
				continue;
			}
			double angle = UndefiendLookAtHelper.viewAngleDegrees(player, mob);
			if (angle <= TRIGGER_FOV_DEGREES && angle < bestAngle) {
				best = mob;
				bestAngle = angle;
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
		LoopingChaseSound instance = new LoopingChaseSound(entity);
		chaseInstance = instance;
		chaseEntityId = entity.getId();
		soundManager.play(chaseInstance);
		OutofboundMod.PACKET_HANDLER.sendToServer(new ChaserRevealPacket(entity.getId()));
	}

	private static void stop(Minecraft minecraft) {
		if (minecraft != null && chaseInstance != null) {
			minecraft.getSoundManager().stop(chaseInstance);
		}
		chaseInstance = null;
		chaseEntityId = -1;
	}

	private static final class LoopingChaseSound extends EntityBoundSoundInstance {

		private LoopingChaseSound(Mob entity) {
			super(
					OutofboundModSounds.CHASE.get(),
					SoundSource.HOSTILE,
					1.0F,
					1.0F,
					entity,
					entity.getRandom().nextLong());
			this.looping = true;
		}
	}
}
