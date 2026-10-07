package net.lixis9.eventjar;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AacSafeModePrompt {

	private AacSafeModePrompt() {
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (AacConfig.SAFE_MODE_CHOSEN) {
			return;
		}
		player.sendSystemMessage(Component.literal("§c[AAC] §fChoose your experience mode:"));
		player.sendSystemMessage(Component.literal(
				"§7Normal§f (default): full horror — including rare desktop/OS side-effects."));
		player.sendSystemMessage(Component.literal(
				"§aSafe Mode§f: same in-game content, but desktop/OS effects are blocked."));
		player.sendSystemMessage(Component.literal(
				"§e/safemode on§7 · §e/safemode off§7 · or §e/aac_config"));
	}
}
