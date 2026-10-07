package net.lixis.outofbound.feature.corruption;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public final class CorruptionAPI {

	private CorruptionAPI() {
	}

	public static void enable() {
		OutofboundMod.CORRUPTION.setEnabled(true);
	}

	public static void disable() {
		OutofboundMod.CORRUPTION.setEnabled(false);
	}

	public static float getLevel(UUID playerId) {
		return OutofboundMod.CORRUPTION.getLevel(playerId);
	}

	public static void setLevel(UUID playerId, float level) {
		OutofboundMod.CORRUPTION.setLevel(playerId, level);
	}

	public static void setLevelManual(ServerPlayer player, float level) {
		OutofboundMod.CORRUPTION.setManualOverride(player.getUUID(), true);
		applyManualLevel(player, level);
	}

	public static void clearManualAndApplyWorldBaseline(ServerPlayer player) {
		OutofboundMod.CORRUPTION.clearManualOverride(player.getUUID());
		MinecraftServer server = player.getServer();
		float worldLevel = server != null ? WorldCorruptionProgressionHandler.computeWorldLevel(server) : 0.0F;
		applyWorldBaseline(player, worldLevel);
	}

	public static void applyWorldBaseline(ServerPlayer player, float level) {
		if (OutofboundMod.CORRUPTION.hasManualOverride(player.getUUID())) {
			return;
		}
		float current = OutofboundMod.CORRUPTION.getLevel(player.getUUID());
		if (Math.abs(current - level) < 0.01F) {
			return;
		}
		OutofboundMod.CORRUPTION.setLevel(player.getUUID(), level);
		if (level > 0.0F) {
			CorruptionSyncPacket.sendToPlayer(player, level, true, CorruptionScheduler.getBurstRemaining());
		} else {
			CorruptionSyncPacket.sendToPlayer(player, 0.0F, false, 0);
			if (player.getServer() != null) {
				CorruptionSnapshots.restoreAll(player.getServer());
			}
		}
	}

	private static void applyManualLevel(ServerPlayer player, float level) {
		OutofboundMod.CORRUPTION.setLevel(player.getUUID(), level);
		if (level > 0.0F) {
			int burst = CorruptionScheduler.burstDuration(level);
			CorruptionScheduler.forceBurst(burst);
			CorruptionEngine.runPlayerBurst(player, level);
			CorruptionSyncPacket.sendToPlayer(player, level, true, burst);
		} else {
			CorruptionSyncPacket.sendToPlayer(player, 0.0F, false, 0);
			if (player.getServer() != null) {
				CorruptionSnapshots.restoreAll(player.getServer());
			}
		}
	}

	public static void setLevel(ServerPlayer player, float level) {
		setLevelManual(player, level);
	}

	public static boolean isActiveFor(ServerPlayer player) {
		return OutofboundMod.CORRUPTION.isActiveFor(player.getUUID());
	}
}
