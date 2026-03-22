package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class GuipatiancenumberProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if (EventjarModVariables.guibool == true) {
			while (EventjarModVariables.guibool == true) {
				EventjarModVariables.GuiPatience = 1 + (entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables())).patience;
			}
		}
	}
}
