package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.registry.OutofboundExtraSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class RandomVoiceHandler {

	private static final Map<UUID, Long> nextVoiceTick = new ConcurrentHashMap<>();

	private RandomVoiceHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !MazeConfig.enableRandomVoices) {
			return;
		}

		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			if (player.isSpectator() || player.isCreative()) {
				continue;
			}
			if ((event.getServer().overworld().getGameTime() + player.getId()) % PostEyeWeirdness.scaledInterval(event.getServer(), MazeConfig.randomVoiceCheckIntervalTicks) != 0L) {
				continue;
			}
			tryPlay(player, ThreadLocalRandom.current());
		}
	}

	public static void playForPlayer(ServerLevel level, ServerPlayer player, ThreadLocalRandom random) {
		if (!MazeConfig.enableRandomVoices) {
			return;
		}
		float volume = 0.55F + random.nextFloat() * 0.35F;
		float pitch = 0.85F + random.nextFloat() * 0.25F;
		OccludedSoundUtil.playBehindOrThroughWall(level, player, OutofboundExtraSounds.RANDOM_VOICE.get(),
				SoundSource.HOSTILE, volume, pitch, MazeConfig.randomVoiceMinDistance,
				MazeConfig.randomVoiceMaxDistance, random);
		markPlayed(player, level.getGameTime());
	}

	private static void tryPlay(ServerPlayer player, ThreadLocalRandom random) {
		ServerLevel level = player.serverLevel();
		long now = level.getGameTime();
		if (now < nextVoiceTick.getOrDefault(player.getUUID(), 0L)) {
			return;
		}
		if (random.nextDouble() >= PostEyeWeirdness.scaledChance(player.server, MazeConfig.randomVoiceChance)) {
			return;
		}
		playForPlayer(level, player, random);
	}

	static void markPlayed(ServerPlayer player, long gameTime) {
		nextVoiceTick.put(player.getUUID(), gameTime + PostEyeWeirdness.scaledCooldown(player.server, MazeConfig.randomVoiceCooldownTicks));
	}

	public static void clearCooldown(UUID playerId) {
		nextVoiceTick.remove(playerId);
	}
}
