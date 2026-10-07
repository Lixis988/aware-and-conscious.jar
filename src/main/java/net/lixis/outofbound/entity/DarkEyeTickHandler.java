package net.lixis.outofbound.entity;

import net.lixis.outofbound.world.PostEyeWeirdness;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class DarkEyeTickHandler {

	private DarkEyeTickHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		int tickCount = event.getServer().getTickCount();

		if (tickCount % 20 == 0) {
			for (ServerLevel level : event.getServer().getAllLevels()) {
				DarkEyeSpawnManager.tickDespawns(level);
			}
		}

		int spawnInterval = PostEyeWeirdness.isActive(event.getServer()) ? 16 : 20;

		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			ServerLevel level = player.serverLevel();
			if ((level.getGameTime() + player.getId()) % spawnInterval != 0L) {
				continue;
			}
			DarkEyeSpawnManager.trySpawnForPlayer(level, player);
		}
	}
}
