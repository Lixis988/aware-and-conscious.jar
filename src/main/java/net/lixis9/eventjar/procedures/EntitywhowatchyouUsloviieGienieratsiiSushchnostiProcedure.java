package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class EntitywhowatchyouUsloviieGienieratsiiSushchnostiProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event);
		}
	}

	public static boolean execute() {
		return execute(null);
	}

	private static boolean execute(@Nullable Event event) {
		if (Math.random() < (1) / ((float) 100000)) {
			return true;
		}
		return false;
	}
}
