package net.lixis9.eventjar;

import net.lixis9.eventjar.init.EventjarModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
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

	private static final int STREAM_GRACE_TICKS = 40;

	private static SoundInstance instance;
	private static int startGrace;

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
		instance = SimpleSoundInstance.forMusic(EventjarModSounds.GAMEMENU.get());
		soundManager.play(instance);
		startGrace = STREAM_GRACE_TICKS;
	}

	private static void stop(Minecraft minecraft) {
		SoundManager soundManager = minecraft.getSoundManager();
		if (instance != null) {
			soundManager.stop(instance);
			instance = null;
		}
		soundManager.stop(EventjarModSounds.GAMEMENU.get().getLocation(), SoundSource.MUSIC);
		startGrace = 0;
	}
}
