package net.lixis.outofbound.feature.corruption;

import net.lixis9.eventjar.DarknessConfig;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.WorldProgressServerHandler;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class WorldCorruptionProgressionHandler {

	private static final float MAX_WORLD_LEVEL = 50.0F;
	private static final float LEVEL_PER_DAY = 5.0F;

	private WorldCorruptionProgressionHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!DarknessConfig.ENABLE_DAMAGE_CORRUPTION) {
			return;
		}
		MinecraftServer server = event.getServer();
		if (server.getTickCount() % 20 != 0) {
			return;
		}
		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}

		WorldInternalConfig.migrateIfNeeded(server);
		float worldLevel = computeWorldLevel(server);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			CorruptionAPI.applyWorldBaseline(player, worldLevel);
		}
		WorldProgressServerHandler.syncAllPlayers(server);
	}

	public static float computeWorldLevel(MinecraftServer server) {
		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return 0.0F;
		}
		long daysSince = WorldInternalConfig.getDaysSinceBoundedcowCollision(server);
		return Math.min(MAX_WORLD_LEVEL, daysSince * LEVEL_PER_DAY);
	}
}
