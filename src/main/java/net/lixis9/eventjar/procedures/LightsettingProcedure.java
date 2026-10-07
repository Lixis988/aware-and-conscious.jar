package net.lixis9.eventjar.procedures;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class LightsettingProcedure {
	private static int tickCounter;

	public static void execute() {
		Minecraft.getInstance().options.gamma().set(0.1);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		if (++tickCounter % 100 != 0)
			return;
		execute();
	}
}
