package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.util.PlayerLookUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lixis.outofbound.OutofboundMod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class ChaserRevealHandler {

	private static final String REVEALED_TAG = "outofbound_chaser_revealed";
	private static final double LOOK_RANGE = 128.0D;
	private static final double PROXIMITY_REVEAL = 18.0D;
	private static final double REVEAL_FOV_DEGREES = 55.0D;
	private static final int CHECK_INTERVAL = 5;

	private ChaserRevealHandler() {
	}

	public static boolean isRevealed(Mob mob) {
		return mob.getPersistentData().getBoolean(REVEALED_TAG);
	}

	public static void markRevealed(Mob mob) {
		if (isRevealed(mob)) {
			return;
		}
		mob.getPersistentData().putBoolean(REVEALED_TAG, true);
	}

	public static void markHidden(Mob mob) {
		mob.getPersistentData().putBoolean(REVEALED_TAG, false);
	}

	public static boolean shouldReveal(Player player, Mob mob) {
		double distance = player.distanceTo(mob);
		if (distance <= PROXIMITY_REVEAL) {
			return true;
		}
		if (distance > LOOK_RANGE) {
			return false;
		}
		if (!player.hasLineOfSight(mob)) {
			return false;
		}
		return PlayerLookUtil.viewAngleDegrees(player, mob) <= REVEAL_FOV_DEGREES;
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		for (ServerLevel level : event.getServer().getAllLevels()) {
			if (!MazeDimensions.isMazeDimension(level.dimension().location())) {
				continue;
			}

			for (Entity entity : level.getAllEntities()) {
				if (!(entity instanceof Mob mob) || !(entity instanceof ChaserEntity)) {
					continue;
				}
				if (!mob.isAlive() || mob.isRemoved()) {
					continue;
				}
				if (isRevealed(mob)) {
					continue;
				}
				if (mob.tickCount % CHECK_INTERVAL != 0) {
					continue;
				}

				for (ServerPlayer player : level.players()) {
					if (!player.isAlive() || player.isSpectator()) {
						continue;
					}
					if (shouldReveal(player, mob)) {
						markRevealed(mob);
						break;
					}
				}
			}
		}
	}
}
