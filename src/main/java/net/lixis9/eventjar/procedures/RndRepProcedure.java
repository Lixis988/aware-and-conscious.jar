package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RndRepProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables())).reputation > 60 && Math.random() < (1) / ((float) 100000)) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("DIE DIE DIE DIE DIE DIE DIE DIE DIE DIE DIE DIE DIE"), true);
		}
	}
}
