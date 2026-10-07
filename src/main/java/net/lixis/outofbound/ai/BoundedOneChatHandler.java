package net.lixis.outofbound.ai;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class BoundedOneChatHandler {

	private BoundedOneChatHandler() {
	}

	@SubscribeEvent
	public static void onChat(ServerChatEvent event) {

		if (BoundedOneAiConfig.enabled) {
			OutofboundMod.LOGGER.debug("Bounded One AI ignores player chat (anonymous server noise mode)");
		}
	}
}
