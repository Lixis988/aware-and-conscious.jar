package net.lixis.outofbound.util;

import net.lixis.outofbound.registry.OutofboundExtraSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.ThreadLocalRandom;

public final class ScareSoundUtil {

	private ScareSoundUtil() {
	}

	public static void playRandomNoise(ServerPlayer player) {
		if (player == null) {
			return;
		}
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int slot = random.nextInt(OutofboundExtraSounds.dimensionNoiseTrackCount());
		SoundEvent noise = OutofboundExtraSounds.dimensionNoiseTrack(slot);
		if (noise == null) {
			return;
		}
		player.playNotifySound(noise, SoundSource.MASTER, 1.0F, 1.0F);
	}
}
