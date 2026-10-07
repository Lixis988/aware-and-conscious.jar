package net.lixis9.eventjar.command;

import com.mojang.brigadier.CommandDispatcher;
import net.lixis9.eventjar.network.ClientOsEffectPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CalculatorFindCommand {
	@SubscribeEvent
	public static void onRegisterCommands(ServerStartingEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
		dispatcher.register(
				Commands.literal("calcfind")
						.requires(cs -> cs.hasPermission(2))
						.executes(ctx -> {
							if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
								ClientOsEffectPacket.send(player, ClientOsEffectPacket.Effect.CALC);
								return 1;
							}
							ctx.getSource().sendFailure(Component.literal("calcfind requires a player"));
							return 0;
						})
		);
	}
}
