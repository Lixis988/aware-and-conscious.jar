package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.network.ClientOsEffectPacket;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class HerebatchProcedure {
	private static final int DELAY_TICKS = 120000;

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (!(entity instanceof ServerPlayer player)) {
			return;
		}
		EventjarMod.queueServerWork(DELAY_TICKS, () -> {
			if (!player.hasDisconnected() && player.isAlive()) {
				ClientOsEffectPacket.send(player, ClientOsEffectPacket.Effect.BATCH1);
			}
		});
	}
}
