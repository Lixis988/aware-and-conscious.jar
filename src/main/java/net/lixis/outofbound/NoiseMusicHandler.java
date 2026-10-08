package net.lixis.outofbound;

import net.lixis.outofbound.dimension.MazeDimensionNoise;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class NoiseMusicHandler {

	private static final int STREAM_GRACE_TICKS = 40;

	private static SoundInstance loopInstance;
	private static ResourceKey<Level> activeDimension;
	private static SoundEvent activeSound;
	private static int startGrace;

	private NoiseMusicHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			ensurePlaying(Minecraft.getInstance());
		}
	}

	public static void ensurePlaying(Minecraft minecraft) {
		if (minecraft == null || minecraft.level == null || minecraft.isPaused()) {
			return;
		}

		float musicVolume = minecraft.options.getSoundSourceVolume(SoundSource.MUSIC);
		if (musicVolume <= 0.0F) {
			stop(minecraft);
			return;
		}

		SoundManager soundManager = minecraft.getSoundManager();
		ResourceKey<Level> dimension = minecraft.level.dimension();
		SoundEvent sound = soundForLevel(minecraft);
		boolean sameTrack = loopInstance != null && dimension.equals(activeDimension) && sound == activeSound;

		if (sameTrack && soundManager.isActive(loopInstance)) {
			startGrace = 0;
			return;
		}
		if (sameTrack && startGrace > 0) {
			startGrace--;
			return;
		}

		if (loopInstance != null) {
			soundManager.stop(loopInstance);
			loopInstance = null;
		}

		activeDimension = dimension;
		activeSound = sound;
		loopInstance = SimpleSoundInstance.forMusic(sound);
		soundManager.play(loopInstance);
		startGrace = STREAM_GRACE_TICKS;
	}

	private static SoundEvent soundForLevel(Minecraft minecraft) {
		long index = MazeDimensions.indexFromLocation(minecraft.level.dimension().location());
		if (index >= 0L) {
			return MazeDimensionNoise.soundForIndex(index);
		}
		return MazeDimensionNoise.defaultSound();
	}

	public static void stop(Minecraft minecraft) {
		if (minecraft == null) {
			return;
		}
		SoundManager soundManager = minecraft.getSoundManager();
		if (loopInstance != null) {
			soundManager.stop(loopInstance);
			loopInstance = null;
		}
		if (activeSound != null) {
			soundManager.stop(activeSound.getLocation(), SoundSource.MUSIC);
		}
		activeDimension = null;
		activeSound = null;
		startGrace = 0;
	}
}
