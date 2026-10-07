package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.level.LevelAccessor;
import net.lixis9.eventjar.network.EventjarModVariables;

@Mod.EventBusSubscriber
public class TickXYZProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {

			if (!TickThrottle.due(event.player, 5))
				return;
			LevelAccessor world = event.player.level();
			var look = event.player.getLookAngle();
			EventjarModVariables.WorldVariables vars = EventjarModVariables.WorldVariables.get(world);
			vars.X = look.x;
			vars.Y = look.y;
			vars.Z = look.z;
			vars.syncData(world);
		}
	}
}
