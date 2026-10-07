package net.lixis.outofbound.world;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazeOverworldTeleport;
import net.lixis.outofbound.dimension.MazeSafeSpawn;
import net.lixis.outofbound.dimension.MazeTeleportUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.concurrent.ThreadLocalRandom;

public final class GlitchGrassTeleportHandler {

	public static final String COOLDOWN_TAG = "outofbound_glitch_grass_cooldown";
	public static final int COOLDOWN_TICKS = 120;
	private static final int PORTAL_COOLDOWN_ON_TELEPORT = 60;

	private static final float OVERWORLD_PICK_CHANCE = 0.35F;

	private GlitchGrassTeleportHandler() {
	}

	public static void tryTeleport(ServerPlayer player) {
		if (hasCooldown(player) || MazeSafeSpawn.hasPortalCooldown(player)) {
			return;
		}

		ResourceLocation current = player.level().dimension().location();
		ThreadLocalRandom random = ThreadLocalRandom.current();

		if (player.level().dimension() == Level.OVERWORLD) {
			teleportToRandomMaze(player, random, -1L);
			return;
		}

		if (MazeDimensions.isMazeDimension(current)) {
			long currentIndex = MazeDimensions.indexFromLocation(current);
			if (random.nextFloat() < OVERWORLD_PICK_CHANCE) {
				MazeOverworldTeleport.teleportToOverworld(player);
				setCooldown(player);
			} else {
				teleportToRandomMaze(player, random, currentIndex);
			}
			return;
		}

		teleportToRandomMaze(player, random, -1L);
	}

	public static boolean hasCooldown(ServerPlayer player) {
		return player.getPersistentData().getInt(COOLDOWN_TAG) > 0;
	}

	public static void setCooldown(ServerPlayer player) {
		player.getPersistentData().putInt(COOLDOWN_TAG, COOLDOWN_TICKS);
		player.getPersistentData().putInt("outofbound_portal_cooldown", PORTAL_COOLDOWN_ON_TELEPORT);
	}

	public static void tickCooldown(ServerPlayer player) {
		int ticks = player.getPersistentData().getInt(COOLDOWN_TAG);
		if (ticks > 0) {
			player.getPersistentData().putInt(COOLDOWN_TAG, ticks - 1);
		}
	}

	private static void teleportToRandomMaze(ServerPlayer player, ThreadLocalRandom random, long avoidIndex) {
		MazeTeleportUtil.teleportToRandomMaze(player, avoidIndex);
		setCooldown(player);
	}
}
