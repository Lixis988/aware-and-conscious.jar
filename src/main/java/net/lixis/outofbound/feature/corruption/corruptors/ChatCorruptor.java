package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.util.BitCorruptor;
import net.lixis.outofbound.feature.corruption.util.CorruptionChatSnippets;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ThreadLocalRandom;

public final class ChatCorruptor extends ServerCorruptor {

	private static final String RED_WARNING = "§4§lYOU SHOULD NOT BE HERE";

	private static final int RED_WARNING_CHANCE = 8;

	@Override
	public float minLevel() {
		return 3.0F;
	}

	@Override
	public int weight() {
		return 4;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		sendOne(player);
	}

	public static void sendOne(ServerPlayer player) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String message;
		if (random.nextInt(RED_WARNING_CHANCE) == 0) {
			message = RED_WARNING;
		} else {
			message = CorruptionChatSnippets.pickCorrupted(random);
			if (random.nextInt(5) == 0) {
				message = BitCorruptor.corruptText(message);
			}
		}
		player.sendSystemMessage(Component.literal(message));
	}
}
