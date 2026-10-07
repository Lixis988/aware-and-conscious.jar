package net.lixis9.eventjar.command;

import net.lixis9.eventjar.network.ClientOsEffectPacket;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WinmsgCommand {
	@SubscribeEvent
	public static void registerCommand(ServerStartingEvent event) {
		event.getServer().getCommands().getDispatcher().register(Commands.literal("winmsg")
				.executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof ServerPlayer player) {
						ClientOsEffectPacket.send(player, ClientOsEffectPacket.Effect.WINMSG);
						return 1;
					}
					arguments.getSource().sendFailure(Component.literal("winmsg requires a player"));
					return 0;
				}));
	}
}
