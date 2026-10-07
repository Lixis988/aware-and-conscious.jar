package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class MazeVoidHandler {

	private static final String INDEX_TAG = "outofbound_maze_index";

	private MazeVoidHandler() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player instanceof ServerPlayer player)) {
			return;
		}

		ServerLevel level = player.serverLevel();
		long index = MazeDimensions.indexFromLocation(level.dimension().location());
		if (index < 0L) {
			return;
		}

		if (player.getY() >= level.getMinBuildHeight() - 8) {
			return;
		}

		MinecraftServer server = player.server;
		long nextIndex = index + 1L;
		ServerLevel next = DimensionManager.getOrCreateMazeLevel(server, nextIndex);
		if (next == null) {

			var rescue = MazeSafeSpawn.findSafeSpawn(level, player, player.getX(), level.getMinBuildHeight() + 1,
					player.getZ());
			player.setDeltaMovement(0.0D, 0.0D, 0.0D);
			player.teleportTo(level, rescue.x, rescue.y, rescue.z, player.getYRot(), player.getXRot());
			player.resetFallDistance();
			return;
		}

		MazeSafeSpawn.teleportPlayer(player, next);
		player.getPersistentData().putLong(INDEX_TAG, nextIndex);

		MazePortalPlacer.ensurePortal(next);
		net.lixis.outofbound.entity.BlackSquareSpawnManager.trySpawnForPlayer(next, player);
	}
}
