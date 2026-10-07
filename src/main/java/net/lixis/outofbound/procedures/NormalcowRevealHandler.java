package net.lixis.outofbound.procedures;

import net.lixis.outofbound.entity.BoundedcowSpawnManager;
import net.lixis.outofbound.entity.NormalcowEntity;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.lixis.outofbound.util.PlayerLookUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class NormalcowRevealHandler {

	private static final int LOOK_CHECK_INTERVAL = 10;
	private static final double LOOK_RANGE = 64.0D;
	private static final double LOOK_DOT_THRESHOLD = 0.96D;

	private NormalcowRevealHandler() {
	}

	public static void tickLookCheck(NormalcowEntity cow) {
		if (cow.level().isClientSide || cow.tickCount % LOOK_CHECK_INTERVAL != 0) {
			return;
		}
		if (cow.getPersistentData().getBoolean("outofbound_revealed")) {
			return;
		}

		Level level = cow.level();
		for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			if (!player.hasLineOfSight(cow)) {
				continue;
			}
			if (!PlayerLookUtil.isLookingAt(player, cow, LOOK_RANGE, LOOK_DOT_THRESHOLD)) {
				continue;
			}

			reveal(cow);
			return;
		}
	}

	public static void onPlayerTouch(NormalcowEntity cow, Player player) {
		reveal(cow);
	}

	public static void onDeath(NormalcowEntity cow) {
		reveal(cow);
	}

	public static void reveal(NormalcowEntity cow) {
		if (cow.getPersistentData().getBoolean("outofbound_revealed")) {
			return;
		}

		MinecraftServer server = cow.level().getServer();
		if (server != null) {
			if (WorldInternalConfig.hasBoundedcowCollision(server)) {
				cow.discard();
				return;
			}
			if (BoundedcowSpawnManager.hasAny(server)) {
				cow.getPersistentData().putBoolean("outofbound_revealed", true);
				cow.discard();
				return;
			}
		}

		cow.getPersistentData().putBoolean("outofbound_revealed", true);

		Level level = cow.level();
		double x = cow.getX();
		double y = cow.getY();
		double z = cow.getZ();

		NormalcowPriStolknovieniiIghrokaSSushchnostiuProcedure.execute(level, x, y, z);

		if (!cow.isRemoved()) {
			cow.discard();
		}
	}
}
