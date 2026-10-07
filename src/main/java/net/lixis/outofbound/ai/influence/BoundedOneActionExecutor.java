package net.lixis.outofbound.ai.influence;

import net.lixis.outofbound.BoundedOneAtmospherePacket;
import net.lixis.outofbound.BoundedcowScarePacket;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.ai.BoundedOneAiConfig;
import net.lixis.outofbound.dimension.MazeAnomalyHandler;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.entity.BlackSquareSpawnManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOneActionExecutor {

	private static final int DISCONNECT_DELAY_TICKS = 4;

	private BoundedOneActionExecutor() {
	}

	public static void execute(BoundedOneAction action, ServerPlayer player, BoundedOneHostContext context) {
		if (!BoundedOneAiConfig.enableInfluence) {
			return;
		}

		player.server.execute(() -> run(action, player, context));
	}

	private static void run(BoundedOneAction action, ServerPlayer player, BoundedOneHostContext context) {
		if (player.hasDisconnected()) {
			return;
		}

		switch (action) {
			case IGNORE, WHISPER, WHISPER_OBSERVATION -> {
			}
			case AMBIENT_SOUND -> playAmbient(player);
			case SPAWN_PRESENCE -> spawnPresence(player);
			case CLIENT_GLITCH -> sendGlitch(player);
			case FAKE_LOG_CHAT -> sendFakeLog(player, context);
			case SCARE_DIALOG -> sendScare(player);
			case DISCONNECT_MESSAGE -> disconnectPlayer(player);
		}

		BoundedOneLogRingBuffer.append("[outofbound] Bounded One action " + action.name() + " for "
				+ player.getGameProfile().getName());
	}

	private static void playAmbient(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		MazeAnomalyHandler.playAmbientForPlayer(level, player);
	}

	private static void spawnPresence(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		if (MazeDimensions.isMazeDimension(level.dimension().location())) {
			BlackSquareSpawnManager.trySpawnForPlayer(level, player);
			return;
		}
		ServerLevel overworld = player.server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
		if (overworld != null) {
			BlackSquareSpawnManager.trySpawnForPlayer(overworld, player);
		}
	}

	private static void sendGlitch(ServerPlayer player) {
		int duration = 8 + ThreadLocalRandom.current().nextInt(12);
		BoundedOneAtmospherePacket.Effect effect = ThreadLocalRandom.current().nextBoolean()
				? BoundedOneAtmospherePacket.Effect.GLITCH_PULSE
				: BoundedOneAtmospherePacket.Effect.TITLE_FLICKER;
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player),
				new BoundedOneAtmospherePacket(effect, duration));
	}

	private static void sendFakeLog(ServerPlayer player, BoundedOneHostContext context) {
		String snippet = context.pickLogSnippet();
		if (snippet.length() > 96) {
			snippet = snippet.substring(0, 96);
		}
		Component fake = Component.literal("[Server thread/INFO]: " + snippet)
				.withStyle(ChatFormatting.DARK_GRAY);
		player.server.getPlayerList().broadcastSystemMessage(fake, false);
	}

	private static void sendScare(ServerPlayer player) {
		OutofboundMod.LOGGER.warn("[Bounded One] SCARE_DIALOG for {}", player.getGameProfile().getName());
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new BoundedcowScarePacket());
	}

	private static void disconnectPlayer(ServerPlayer player) {
		OutofboundMod.LOGGER.warn("[Bounded One] DISCONNECT_MESSAGE for {}", player.getGameProfile().getName());
		OutofboundMod.queueServerWork(DISCONNECT_DELAY_TICKS, () -> {
			if (!player.hasDisconnected()) {
				player.connection.disconnect(Component.literal("Unable to access the file winload.exe"));
			}
		});
	}
}
