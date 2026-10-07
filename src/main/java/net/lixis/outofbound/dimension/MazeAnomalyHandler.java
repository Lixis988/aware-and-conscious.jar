package net.lixis.outofbound.dimension;

import net.lixis.outofbound.registry.OutofboundExtraSounds;
import net.lixis.outofbound.world.OccludedSoundUtil;
import net.lixis.outofbound.world.PostEyeWeirdness;
import net.lixis.outofbound.world.RandomVoiceHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class MazeAnomalyHandler {

	private static final SoundEvent[] NEARBY = {
			SoundEvents.STONE_STEP,
			SoundEvents.GRAVEL_STEP,
			SoundEvents.WOOD_STEP,
			SoundEvents.WOOL_STEP
	};

	private static final SoundEvent[] DISTANT = {
			SoundEvents.ZOMBIE_AMBIENT,
			SoundEvents.SKELETON_AMBIENT,
			SoundEvents.SPIDER_AMBIENT,
			SoundEvents.ENDERMAN_AMBIENT,
			SoundEvents.CREEPER_PRIMED,
			SoundEvents.AMBIENT_CAVE.value()
	};

	private MazeAnomalyHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			ServerLevel level = player.serverLevel();
			if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
				continue;
			}
			int interval = PostEyeWeirdness.scaledInterval(event.getServer(), 20);
			if ((level.getGameTime() + player.getId()) % interval != 0L) {
				continue;
			}

			ThreadLocalRandom random = ThreadLocalRandom.current();
			if (random.nextDouble() >= PostEyeWeirdness.scaledChance(event.getServer(), MazeConfig.anomalyChance)) {
				continue;
			}

			playRandomAnomaly(level, player, random);
		}
	}

	public static void playAmbientForPlayer(ServerLevel level, ServerPlayer player) {
		if (MazeConfig.enableRandomVoices) {
			RandomVoiceHandler.playForPlayer(level, player, ThreadLocalRandom.current());
			return;
		}
		playRandomAnomaly(level, player, ThreadLocalRandom.current());
	}

	private static void playRandomAnomaly(ServerLevel level, ServerPlayer player, ThreadLocalRandom random) {
		int category = random.nextInt(5);
		switch (category) {
			case 0 -> {
				SoundEvent step = pickCustom(random) ? OutofboundExtraSounds.DISTANT_STEP.get() : NEARBY[random.nextInt(NEARBY.length)];
				playAt(level, player, random, 2, 6, step, 0.35F, 0.7F + random.nextFloat() * 0.3F);
			}
			case 1 -> playAt(level, player, random, 14, 34, DISTANT[random.nextInt(DISTANT.length)], 0.7F, 0.6F + random.nextFloat() * 0.4F);
			case 2 -> RandomVoiceHandler.playForPlayer(level, player, random);
			case 3 -> playAt(level, player, random, 10, 28, OutofboundExtraSounds.RANDOM_EVENT.get(), 0.9F, 0.9F + random.nextFloat() * 0.2F);
			default -> playAt(level, player, random, 1, 8, OutofboundExtraSounds.AMBIENT_NOISE.get(), 0.6F, 1.0F);
		}
	}

	private static boolean pickCustom(ThreadLocalRandom random) {
		return random.nextBoolean();
	}

	private static void playAt(ServerLevel level, ServerPlayer player, ThreadLocalRandom random,
			int minOffset, int maxOffset, SoundEvent sound, float volume, float pitch) {
		if (sound == null) {
			return;
		}
		OccludedSoundUtil.playBehindOrThroughWall(level, player, sound, SoundSource.AMBIENT, volume, pitch,
				minOffset, maxOffset, random);
	}
}
