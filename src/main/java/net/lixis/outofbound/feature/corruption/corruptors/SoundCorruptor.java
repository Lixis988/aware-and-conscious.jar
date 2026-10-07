package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.util.SafeCorruptor;
import net.lixis.outofbound.registry.OutofboundExtraSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.ThreadLocalRandom;

public final class SoundCorruptor extends ServerCorruptor {

	@Override
	public float minLevel() {
		return 5.0F;
	}

	@Override
	public int weight() {
		return 1;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int slot = random.nextInt(OutofboundExtraSounds.dimensionNoiseTrackCount());
		SoundEvent sound = OutofboundExtraSounds.dimensionNoiseTrack(slot);
		float pitch = SafeCorruptor.safeFloat(random.nextFloat() * 5.0F);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				sound, SoundSource.HOSTILE, 0.9F, pitch);
	}
}
