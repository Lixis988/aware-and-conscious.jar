package net.lixis9.eventjar.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.ServerChatEvent;

import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class MeetboyleftthegamerProcedure {
	@SubscribeEvent
	public static void onChat(ServerChatEvent event) {
		execute(event, event.getPlayer().level(), event.getRawText());
	}

	public static void execute(LevelAccessor world, String text) {
		execute(null, world, text);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, String text) {
		if (text == null)
			return;
		if (text.contains("DIE")) {
			if (!world.isClientSide() && world.getServer() != null)
				ServerLifecycleHooks.getCurrentServer().stopServer();
		}
	}
}
