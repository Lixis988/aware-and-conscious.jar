package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.RandomMessagePacket;
import net.lixis.outofbound.dimension.MazeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class RandomMessageHandler {

	private static final Map<UUID, Long> nextMessageTick = new ConcurrentHashMap<>();

	private RandomMessageHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !MazeConfig.enableRandomMessages) {
			return;
		}

		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			if (player.isSpectator() || player.isCreative()) {
				continue;
			}
			if ((event.getServer().overworld().getGameTime() + player.getId()) % PostEyeWeirdness.scaledInterval(event.getServer(), MazeConfig.randomMessageCheckIntervalTicks) != 0L) {
				continue;
			}
			tryShow(player, ThreadLocalRandom.current());
		}
	}

	public static void showForPlayer(ServerPlayer player, ThreadLocalRandom random) {
		if (!MazeConfig.enableRandomMessages) {
			return;
		}
		int index = random.nextInt(RandomMessagePacket.MESSAGE_COUNT);
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player),
				new RandomMessagePacket(index, MazeConfig.randomMessageDurationTicks));
		markShown(player, player.serverLevel().getGameTime());
	}

	private static void tryShow(ServerPlayer player, ThreadLocalRandom random) {
		ServerLevel level = player.serverLevel();
		long now = level.getGameTime();
		if (now < nextMessageTick.getOrDefault(player.getUUID(), 0L)) {
			return;
		}
		if (random.nextDouble() >= PostEyeWeirdness.scaledChance(player.server, MazeConfig.randomMessageChance)) {
			return;
		}
		showForPlayer(player, random);
	}

	static void markShown(ServerPlayer player, long gameTime) {
		nextMessageTick.put(player.getUUID(), gameTime + PostEyeWeirdness.scaledCooldown(player.server, MazeConfig.randomMessageCooldownTicks));
	}

	public static void clearCooldown(UUID playerId) {
		nextMessageTick.remove(playerId);
	}
}
