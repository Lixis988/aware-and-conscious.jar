package net.lixis.outofbound.dimension;

import net.lixis.outofbound.WorldProgressionServerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class MazeOverworldTeleport {

	private MazeOverworldTeleport() {
	}

	public static void teleportToOverworld(ServerPlayer player) {
		ServerLevel overworld = player.server.overworld();
		BlockPos spawn = overworld.getSharedSpawnPos();
		double x = spawn.getX() + 0.5D;
		double y = Math.max(overworld.getMinBuildHeight() + 1.0D, spawn.getY());
		double z = spawn.getZ() + 0.5D;

		player.setDeltaMovement(0.0D, 0.0D, 0.0D);
		player.teleportTo(overworld, x, y, z, player.getYRot(), player.getXRot());
		player.gameMode.setLevel(overworld);
		player.resetFallDistance();
		player.getPersistentData().putInt("outofbound_portal_cooldown", 20);

		WorldProgressionServerHandler.handleOverworldReturn(player);
	}
}
