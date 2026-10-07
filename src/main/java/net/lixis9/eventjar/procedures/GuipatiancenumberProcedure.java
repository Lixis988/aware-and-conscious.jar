package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;
import net.lixis9.eventjar.network.EventjarModVariables;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class GuipatiancenumberProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		execute(event, event.getEntity());
		EventjarModVariables.guibool = false;
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if (EventjarModVariables.guibool) {
			EventjarModVariables.GuiPatience = 1 + (entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
					.orElse(new EventjarModVariables.PlayerVariables())).patience;
		}
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT)
	public static class ClientSync {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (event.phase != TickEvent.Phase.END)
				return;
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null || !EventjarModVariables.guibool)
				return;
			if (!TickThrottle.due(mc.player, 5))
				return;
			GuipatiancenumberProcedure.execute(mc.player);
		}
	}
}
