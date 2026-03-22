package net.lixis9.eventjar.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.world.level.LevelAccessor;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class IfPlayerStayAFKinGUIProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.minecraftforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		if (Math.random() < (1) / ((float) 150)) {
			EventjarModVariables.guibool = true;
			JumpScareOnnProcedure.execute();
			if (EventjarModVariables.GuiPatience == 2) {
				if (!world.isClientSide() && world.getServer() != null)
					ServerLifecycleHooks.getCurrentServer().stopServer();
			}
			if (!world.isClientSide() && world.getServer() != null)
				ServerLifecycleHooks.getCurrentServer().stopServer();
		}
	}
}
