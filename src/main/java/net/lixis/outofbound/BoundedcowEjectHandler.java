package net.lixis.outofbound;

import net.lixis.outofbound.entity.BoundedcowEntity;
import net.lixis.outofbound.entity.BoundedcowSpawnManager;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public final class BoundedcowEjectHandler {

	private static final int DISCONNECT_DELAY_TICKS = 4;

	private BoundedcowEjectHandler() {
	}

	public static void onPlayerTouch(Player player, BoundedcowEntity cow) {
		if (player.level().isClientSide || cow.isRemoved()) {
			return;
		}
		if (cow.getPersistentData().getBoolean("outofbound_triggered")) {
			return;
		}
		cow.getPersistentData().putBoolean("outofbound_triggered", true);
		cow.discard();

		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (WorldInternalConfig.hasBoundedcowCollision(serverPlayer.getServer())) {
			BoundedcowSpawnManager.removeAll(serverPlayer.getServer());
			return;
		}

		WorldInternalConfig.recordBoundedcowCollision(serverPlayer.getServer(), serverPlayer);
		WorldInternalConfig.beginSinkingPhase(serverPlayer.getServer());
		BoundedcowSpawnManager.removeAll(serverPlayer.getServer());
		WorldProgressServerHandler.syncAllPlayers(serverPlayer);

		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new BoundedcowScarePacket());

		OutofboundMod.queueServerWork(DISCONNECT_DELAY_TICKS, () -> {
			if (!serverPlayer.hasDisconnected()) {
				serverPlayer.connection.disconnect(Component.literal("Unable to access the file winload.exe"));
			}
		});
	}
}
