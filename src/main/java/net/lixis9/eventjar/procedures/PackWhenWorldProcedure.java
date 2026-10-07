package net.lixis9.eventjar.procedures;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class PackWhenWorldProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.minecraftforge.event.level.LevelEvent.Load event) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			execute(event);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			execute(null);
		}
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		AddresourspackProcedure.execute();
		Addresourspack2Procedure.execute();
		Addresourspack3Procedure.execute();
	}
}
