package net.lixis9.eventjar.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.world.level.LevelAccessor;

import net.lixis9.eventjar.EventjarMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class GodLovesYouProcedure {
	@SubscribeEvent
	public static void onWorldUnload(net.minecraftforge.event.level.LevelEvent.Unload event) {
		execute(event, event.getLevel());
	}

	public static boolean execute(LevelAccessor world) {
		return execute(null, world);
	}

	private static boolean execute(@Nullable Event event, LevelAccessor world) {
		if (Math.random() < (1) / ((float) 1000)) {
			EventjarMod.queueServerWork(300, () -> {
				if (!world.isClientSide() && world.getServer() != null)
					ServerLifecycleHooks.getCurrentServer().stopServer();
			});
			return true;
		}
		return false;
	}
}
