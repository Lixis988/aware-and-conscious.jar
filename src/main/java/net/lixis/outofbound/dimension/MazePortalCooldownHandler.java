package net.lixis.outofbound.dimension;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.world.GlitchGrassTeleportHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class MazePortalCooldownHandler {

	private MazePortalCooldownHandler() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
			return;
		}
		MazeSafeSpawn.tickPortalCooldown(player);
		GlitchGrassTeleportHandler.tickCooldown(player);
	}
}
