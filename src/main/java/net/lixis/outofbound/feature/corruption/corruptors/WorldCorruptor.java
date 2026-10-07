package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.util.SafeCorruptor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.ThreadLocalRandom;

public final class WorldCorruptor extends ServerCorruptor {

	@Override
	public float minLevel() {
		return 15.0F;
	}

	@Override
	public int weight() {
		return 1;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextBoolean()) {
			boolean raining = random.nextBoolean();
			boolean thundering = random.nextBoolean();
			level.setWeatherParameters(0, 40, raining, thundering);
		} else {
			float pitch = SafeCorruptor.safeFloat(random.nextFloat() * 5.0F);
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 0.8F, pitch);
		}
	}
}
