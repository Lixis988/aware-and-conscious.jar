package net.lixis.outofbound;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class WorldProgressServerHandler {

	private WorldProgressServerHandler() {
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			MinecraftServer server = serverPlayer.getServer();
			if (server != null) {
				WorldInternalConfig.ensureBoundedcowCollisionOnJoin(server, serverPlayer);
			}
			WorldProgressionServerHandler.handleOverworldReturn(serverPlayer, false);
			syncToPlayer(serverPlayer);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			WorldProgressionServerHandler.handleOverworldReturn(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (!MazeDimensions.isMazeDimension(event.getTo().location())) {
			WorldProgressionServerHandler.handleOverworldReturn(player);
		}
	}

	public static void syncToPlayer(ServerPlayer player) {
		sendPacket(player);
	}

	public static void syncAllPlayers(ServerPlayer source) {
		syncAllPlayers(source.getServer());
	}

	public static void syncAllPlayers(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			sendPacket(player);
		}
	}

	private static void sendPacket(ServerPlayer player) {
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), createPacket(player.getServer()));
	}

	public static WorldProgressSyncPacket createPacket(net.minecraft.server.MinecraftServer server) {
		WorldInternalConfig.migrateIfNeeded(server);
		return new WorldProgressSyncPacket(
				WorldInternalConfig.hasBoundedcowCollision(server),
				WorldInternalConfig.getGameStage(server),
				WorldInternalConfig.getSinkUntilTick(server),
				server.overworld().getGameTime(),
				server.overworld().getDayTime(),
				WorldInternalConfig.getMazeIndex(server),
				WorldInternalConfig.isUndefiendMaze(server),
				WorldInternalConfig.getBoundedcowCollisionTick(server),
				WorldInternalConfig.getBoundedcowCollisionDayTime(server));
	}
}
