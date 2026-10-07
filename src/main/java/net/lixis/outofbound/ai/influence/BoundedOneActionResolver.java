package net.lixis.outofbound.ai.influence;

import net.lixis.outofbound.ai.BoundedOneAiConfig;
import net.minecraft.server.level.ServerPlayer;

public final class BoundedOneActionResolver {

	private BoundedOneActionResolver() {
	}

	public static BoundedOneAction resolve(ServerPlayer player, BoundedOneAction requested, String sayText,
			BoundedOneHostContext context) {
		if (requested == BoundedOneAction.IGNORE) {
			return BoundedOneAction.IGNORE;
		}
		if (sayText == null || sayText.isBlank()) {
			return BoundedOneAction.IGNORE;
		}

		if (!BoundedOneAiConfig.enableInfluence || !BoundedOneAiConfig.modelChoosesActions) {
			BoundedOneActionSelector.markUsed(player.getUUID(), BoundedOneAction.WHISPER);
			return BoundedOneAction.WHISPER;
		}

		BoundedOneAction action = requested == null ? BoundedOneAction.WHISPER : requested;

		if (action.heavy() && !BoundedOneAiConfig.enableHeavyActions) {
			action = BoundedOneAction.WHISPER;
		}

		if (BoundedOneActionSelector.isOnCooldown(player.getUUID(), action)) {
			action = downgrade(action);
		}

		if (action != BoundedOneAction.WHISPER && action != BoundedOneAction.IGNORE
				&& BoundedOneActionSelector.isOnCooldown(player.getUUID(), action)) {
			action = BoundedOneAction.WHISPER;
		}

		BoundedOneActionSelector.markUsed(player.getUUID(), action);
		return action;
	}

	private static BoundedOneAction downgrade(BoundedOneAction action) {
		return switch (action) {
			case SCARE_DIALOG, DISCONNECT_MESSAGE, SPAWN_PRESENCE, CLIENT_GLITCH -> BoundedOneAction.AMBIENT_SOUND;
			case AMBIENT_SOUND, FAKE_LOG_CHAT, WHISPER_OBSERVATION -> BoundedOneAction.WHISPER;
			default -> BoundedOneAction.WHISPER;
		};
	}
}
