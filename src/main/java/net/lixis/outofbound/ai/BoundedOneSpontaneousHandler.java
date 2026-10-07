package net.lixis.outofbound.ai;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.ai.influence.BoundedOneAction;
import net.lixis.outofbound.ai.influence.BoundedOneHostContext;
import net.lixis.outofbound.ai.influence.BoundedOneHostContextGatherer;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class BoundedOneSpontaneousHandler {

	private static final int CHECK_INTERVAL_TICKS = 20;

	private static final Map<UUID, Long> SPONTANEOUS_COOLDOWN_UNTIL_MS = new ConcurrentHashMap<>();

	private BoundedOneSpontaneousHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (event.getServer().getTickCount() % CHECK_INTERVAL_TICKS != 0) {
			return;
		}
		if (!BoundedOneAiConfig.enabled || !BoundedOneAiConfig.enableSpontaneous) {
			return;
		}
		if (!WorldInternalConfig.hasBoundedcowCollision(event.getServer())) {
			return;
		}
		if (!BoundedOneAiService.get().isReady()) {
			return;
		}

		long days = WorldInternalConfig.getDaysSinceBoundedcowCollision(event.getServer());
		double chance = dayScaledChance(days);
		int cooldownScale = dayScaledCooldownSeconds(days);

		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			trySpontaneous(player, ThreadLocalRandom.current(), chance, cooldownScale);
		}
	}

	static double dayScaledChance(long days) {
		double base = BoundedOneAiConfig.spontaneousChancePerCheck;
		double scaled = base * (0.35D + Math.min(12.0D, Math.max(0L, days)) * 0.22D);
		return Math.min(0.85D, scaled);
	}

	static int dayScaledCooldownSeconds(long days) {
		int min = BoundedOneAiConfig.spontaneousMinCooldownSeconds;
		int max = Math.max(min, BoundedOneAiConfig.spontaneousMaxCooldownSeconds);
		double factor = 1.0D / (1.0D + Math.min(10.0D, Math.max(0L, days)) * 0.12D);
		int scaledMin = Math.max(8, (int) Math.round(min * factor));
		int scaledMax = Math.max(scaledMin, (int) Math.round(max * factor));
		return scaledMin + ThreadLocalRandom.current().nextInt(scaledMax - scaledMin + 1);
	}

	private static void trySpontaneous(ServerPlayer player, ThreadLocalRandom random, double chance, int cooldownSeconds) {
		if (random.nextDouble() >= chance) {
			return;
		}
		if (BoundedOneAiConfig.collisionPlayerOnly && !isCollisionPlayer(player)) {
			return;
		}
		if (BoundedOneAiConfig.respondInMazeOnly
				&& !MazeDimensions.isMazeDimension(player.level().dimension().location())) {
			return;
		}
		if (isOnSpontaneousCooldown(player)) {
			return;
		}

		markSpontaneousUsed(player, cooldownSeconds);

		BoundedOneHostContext context = BoundedOneHostContextGatherer.gather(player, BoundedOneAction.WHISPER);

		BoundedOneAiService.get().generateSpontaneousTurn(player, context, result -> player.server.execute(() -> {
			if (!result.silent()) {
				BoundedOneChatBroadcast.broadcastReply(player, result.displayText());
			}
		}));
	}

	private static boolean isCollisionPlayer(ServerPlayer player) {
		UUID collisionPlayer = WorldInternalConfig.getBoundedcowCollisionPlayerUuid(player.server);
		return collisionPlayer == null || collisionPlayer.equals(player.getUUID());
	}

	static boolean isOnSpontaneousCooldown(ServerPlayer player) {
		Long until = SPONTANEOUS_COOLDOWN_UNTIL_MS.get(player.getUUID());
		return until != null && until > System.currentTimeMillis();
	}

	static void markSpontaneousUsed(ServerPlayer player, int seconds) {
		SPONTANEOUS_COOLDOWN_UNTIL_MS.put(player.getUUID(),
				System.currentTimeMillis() + Math.max(1, seconds) * 1000L);
	}

	static void clearCooldowns() {
		SPONTANEOUS_COOLDOWN_UNTIL_MS.clear();
	}
}
