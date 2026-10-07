package net.lixis9.eventjar;

import net.lixis9.eventjar.init.EventjarModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EventjarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Musichandler {

	private static MenuMusicInstance instance;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.screen instanceof TitleScreen) {
			ensurePlaying(minecraft);
		} else {
			stop(minecraft);
		}
	}

	private static void ensurePlaying(Minecraft minecraft) {
		SoundManager soundManager = minecraft.getSoundManager();
		if (instance != null && !instance.isStopped() && soundManager.isActive(instance)) {
			return;
		}
		minecraft.getMusicManager().stopPlaying();
		instance = new MenuMusicInstance();
		soundManager.play(instance);
	}

	private static void stop(Minecraft minecraft) {
		if (instance != null) {
			minecraft.getSoundManager().stop(instance);
			instance = null;
		}
	}

	private static final class MenuMusicInstance extends AbstractTickableSoundInstance {

		private MenuMusicInstance() {
			super(EventjarModSounds.GAMEMENU.get(), SoundSource.MUSIC, SoundInstance.createUnseededRandom());
			this.volume = 1.0F;
			this.pitch = 1.0F;
			this.looping = true;
			this.relative = true;
			this.attenuation = SoundInstance.Attenuation.NONE;
			this.x = 0.0D;
			this.y = 0.0D;
			this.z = 0.0D;
		}

		@Override
		public void tick() {
		}
	}
}
