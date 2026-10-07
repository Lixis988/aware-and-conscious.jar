package net.lixis.outofbound;

import net.lixis.outofbound.dimension.MazeDimensionNoise;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class NoiseMusicHandler {

	private static LoopingNoiseInstance loopInstance;
	private static ResourceKey<Level> activeDimension;
	private static SoundEvent activeSound;

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

		ResourceKey<Level> dimension = minecraft.level.dimension();
		SoundEvent sound = soundForLevel(minecraft);
		SoundManager soundManager = minecraft.getSoundManager();

		if (loopInstance != null
				&& !loopInstance.isStopped()
				&& soundManager.isActive(loopInstance)
				&& dimension.equals(activeDimension)
				&& sound == activeSound) {
			loopInstance.setVolume(musicVolume);
			return;
		}

		stop(minecraft);
		minecraft.getMusicManager().stopPlaying();

		activeDimension = dimension;
		activeSound = sound;
		loopInstance = new LoopingNoiseInstance(sound, musicVolume);
		soundManager.play(loopInstance);
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
		if (loopInstance != null) {
			minecraft.getSoundManager().stop(loopInstance);
			loopInstance = null;
		}
		activeDimension = null;
		activeSound = null;
		minecraft.getMusicManager().stopPlaying();
	}

	private static final class LoopingNoiseInstance extends AbstractTickableSoundInstance {

		private LoopingNoiseInstance(SoundEvent sound, float volume) {
			super(sound, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
			this.volume = volume;
			this.pitch = 1.0F;
			this.looping = true;
			this.relative = true;
			this.attenuation = SoundInstance.Attenuation.NONE;
			this.x = 0.0D;
			this.y = 0.0D;
			this.z = 0.0D;
		}

		void setVolume(float volume) {
			this.volume = volume;
		}

		@Override
		public void tick() {

			if (!Minecraft.getInstance().getSoundManager().isActive(this)) {
				this.stop();
			}
		}
	}
}
