package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class SleepMazeHandler {

	private static final double MAZE_CHANCE = 0.25D;

	private SleepMazeHandler() {
	}

	@SubscribeEvent
	public static void onPlayerSleep(PlayerSleepInBedEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (MazeDimensions.isMazeDimension(player.level().dimension().location())) {
			return;
		}
		if (ThreadLocalRandom.current().nextDouble() >= MAZE_CHANCE) {
			return;
		}

		event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
		long currentIndex = player.getPersistentData().getLong("outofbound_maze_index");
		player.server.execute(() -> MazeTeleportUtil.teleportToRandomMaze(player, currentIndex));
	}
}
