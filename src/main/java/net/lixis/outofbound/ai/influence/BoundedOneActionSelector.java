package net.lixis.outofbound.ai.influence;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.ai.BoundedOneAiConfig;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.world.WorldGameStage;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOneActionSelector {

	private static final Map<UUID, EnumMap<BoundedOneAction, Long>> COOLDOWN_UNTIL_MS = new ConcurrentHashMap<>();

	private BoundedOneActionSelector() {
	}

	public static BoundedOneAction pick(ServerPlayer player, String userMessage, BoundedOneHostContext context) {
		if (!BoundedOneAiConfig.enableInfluence) {
			return BoundedOneAction.WHISPER;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		WorldGameStage stage = WorldInternalConfig.getGameStage(player.server);
		boolean inMaze = MazeDimensions.isMazeDimension(player.level().dimension().location());
		String lower = userMessage == null ? "" : userMessage.toLowerCase(Locale.ROOT);
		boolean curious = lower.contains("where") || lower.contains("who") || lower.contains("what");
		boolean hasDesktop = context != null && !context.desktopEntries().isEmpty();

		int totalWeight = 0;
		EnumMap<BoundedOneAction, Integer> weights = new EnumMap<>(BoundedOneAction.class);
		for (BoundedOneAction action : BoundedOneAction.values()) {
			if (action == BoundedOneAction.IGNORE) {
				continue;
			}
			if (action.heavy() && !BoundedOneAiConfig.enableHeavyActions) {
				continue;
			}
			if (isOnCooldown(player.getUUID(), action)) {
				continue;
			}
			int weight = action.weight();
			if (inMaze && (action == BoundedOneAction.SPAWN_PRESENCE || action == BoundedOneAction.AMBIENT_SOUND)) {
				weight += 6;
			}
			if (stage == WorldGameStage.IN_MAZE && action == BoundedOneAction.CLIENT_GLITCH) {
				weight += 4;
			}
			if (curious && action == BoundedOneAction.WHISPER_OBSERVATION) {
				weight += 8;
			}
			if (curious && action == BoundedOneAction.FAKE_LOG_CHAT) {
				weight += 3;
			}
			if (hasDesktop && (action == BoundedOneAction.WHISPER_OBSERVATION || action == BoundedOneAction.FAKE_LOG_CHAT)) {
				weight += 5;
			}
			if (action.heavy() && !hasRecentHeavy(player.getUUID())) {
				weight += 2;
			}
			if (weight > 0) {
				weights.put(action, weight);
				totalWeight += weight;
			}
		}

		if (totalWeight <= 0) {
			return BoundedOneAction.WHISPER;
		}

		int roll = random.nextInt(totalWeight);
		for (Map.Entry<BoundedOneAction, Integer> entry : weights.entrySet()) {
			roll -= entry.getValue();
			if (roll < 0) {
				BoundedOneAction selected = entry.getKey();
				markUsed(player.getUUID(), selected);
				return selected;
			}
		}
		return BoundedOneAction.WHISPER;
	}

	public static String formatAvailableActions(ServerPlayer player) {
		List<String> actions = new ArrayList<>();
		actions.add(BoundedOneAction.IGNORE.name());
		actions.add(BoundedOneAction.WHISPER.name());
		if (!BoundedOneAiConfig.enableInfluence || !BoundedOneAiConfig.modelChoosesActions) {
			return String.join(", ", actions);
		}
		for (BoundedOneAction action : BoundedOneAction.values()) {
			if (action == BoundedOneAction.IGNORE || action == BoundedOneAction.WHISPER) {
				continue;
			}
			if (action.heavy() && !BoundedOneAiConfig.enableHeavyActions) {
				continue;
			}
			if (isOnCooldown(player.getUUID(), action)) {
				continue;
			}
			actions.add(action.name());
		}
		return String.join(", ", actions);
	}

	public static boolean isOnCooldown(UUID playerId, BoundedOneAction action) {
		if (action == BoundedOneAction.IGNORE) {
			return false;
		}
		Long until = cooldownMap(playerId).get(action);
		return until != null && until > System.currentTimeMillis();
	}

	public static void markUsed(UUID playerId, BoundedOneAction action) {
		long cooldownMs = cooldownSeconds(action) * 1000L;
		if (cooldownMs <= 0L) {
			return;
		}
		cooldownMap(playerId).put(action, System.currentTimeMillis() + cooldownMs);
		if (action.heavy()) {
			OutofboundMod.LOGGER.info("[Bounded One] heavy action {} armed for player {}", action, playerId);
		}
	}

	private static int cooldownSeconds(BoundedOneAction action) {
		return switch (action) {
			case IGNORE -> 0;
			case WHISPER -> 5;
			case AMBIENT_SOUND -> 15;
			case FAKE_LOG_CHAT -> 30;
			case WHISPER_OBSERVATION -> 120;
			case CLIENT_GLITCH -> BoundedOneAiConfig.glitchCooldownSeconds;
			case SPAWN_PRESENCE -> BoundedOneAiConfig.blackSquareCooldownSeconds;
			case SCARE_DIALOG -> BoundedOneAiConfig.scareCooldownSeconds;
			case DISCONNECT_MESSAGE -> BoundedOneAiConfig.disconnectCooldownSeconds;
		};
	}

	private static boolean hasRecentHeavy(UUID playerId) {
		EnumMap<BoundedOneAction, Long> map = cooldownMap(playerId);
		long now = System.currentTimeMillis();
		for (BoundedOneAction action : BoundedOneAction.values()) {
			if (!action.heavy()) {
				continue;
			}
			Long until = map.get(action);
			if (until != null && until > now) {
				return true;
			}
		}
		return false;
	}

	private static EnumMap<BoundedOneAction, Long> cooldownMap(UUID playerId) {
		return COOLDOWN_UNTIL_MS.computeIfAbsent(playerId, ignored -> new EnumMap<>(BoundedOneAction.class));
	}
}
