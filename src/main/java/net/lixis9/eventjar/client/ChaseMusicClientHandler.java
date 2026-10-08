package net.lixis9.eventjar.client;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.entity.MeetboyLongEntity;
import net.lixis9.eventjar.entity.SeekeractEntity;
import net.lixis9.eventjar.init.EventjarModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EventjarMod.MODID, value = Dist.CLIENT)
public final class ChaseMusicClientHandler {

	private static final int STREAM_GRACE_TICKS = 40;
	private static final double RANGE = 64.0D;

	private static SoundInstance instance;
	private static int startGrace;

	private ChaseMusicClientHandler() {
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
		if (minecraft.options.getSoundSourceVolume(SoundSource.PLAYERS) <= 0.0F) {
			stop(minecraft);
			return;
		}
		if (isChaseActive(minecraft, player)) {
			ensurePlaying(minecraft);
		} else {
			stop(minecraft);
		}
	}

	private static boolean isChaseActive(Minecraft minecraft, LocalPlayer player) {
		AABB box = player.getBoundingBox().inflate(RANGE);
		List<Entity> nearby = minecraft.level.getEntities(player, box,
				entity -> entity.isAlive() && !entity.isRemoved());
		for (Entity entity : nearby) {
			if (entity instanceof SeekeractEntity) {
				return true;
			}
			if (entity instanceof MeetboyLongEntity longBoy && longBoy.isChasing()) {
				return true;
			}
		}
		return false;
	}

	private static void ensurePlaying(Minecraft minecraft) {
		SoundManager soundManager = minecraft.getSoundManager();
		if (instance != null && soundManager.isActive(instance)) {
			startGrace = 0;
			return;
		}
		if (instance != null && startGrace > 0) {
			startGrace--;
			return;
		}
		if (instance != null) {
			soundManager.stop(instance);
			instance = null;
		}
		instance = new SimpleSoundInstance(
				EventjarModSounds.MEACHASE.get().getLocation(),
				SoundSource.PLAYERS,
				0.9F,
				1.0F,
				SoundInstance.createUnseededRandom(),
				false,
				0,
				SoundInstance.Attenuation.NONE,
				0.0D,
				0.0D,
				0.0D,
				true);
		soundManager.play(instance);
		startGrace = STREAM_GRACE_TICKS;
	}

	private static void stop(Minecraft minecraft) {
		SoundManager soundManager = minecraft.getSoundManager();
		if (instance != null) {
			soundManager.stop(instance);
			instance = null;
		}
		soundManager.stop(EventjarModSounds.MEACHASE.get().getLocation(), SoundSource.PLAYERS);
		startGrace = 0;
	}
}
