package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;
import net.lixis9.eventjar.network.ClientOsEffectPacket;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RndcalcnightProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player instanceof ServerPlayer player)) {
			return;
		}
		if (!TickThrottle.due(player)) {
			return;
		}
		execute(event, player.level(), player);
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (!(entity instanceof ServerPlayer player)) {
			return;
		}
		if (!TickThrottle.rollDenomEvent(400000)) {
			return;
		}
		ClientOsEffectPacket.send(player, ClientOsEffectPacket.Effect.CALC);
	}
}
