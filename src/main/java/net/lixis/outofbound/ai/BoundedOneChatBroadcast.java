package net.lixis.outofbound.ai;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

final class BoundedOneChatBroadcast {

	private BoundedOneChatBroadcast() {
	}

	static void broadcastReply(ServerPlayer player, String reply) {
		if (player.hasDisconnected() || reply == null || reply.isBlank()) {
			return;
		}
		player.server.getPlayerList().broadcastSystemMessage(Component.literal(reply.trim()), false);
	}
}
