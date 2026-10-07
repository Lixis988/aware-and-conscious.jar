package net.lixis9.eventjar.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.Component;

import net.lixis9.eventjar.init.EventjarModMobEffects;
import net.lixis9.eventjar.network.EventjarModVariables;

@Mod.EventBusSubscriber
public class OverdoseTestCommand {

    @SubscribeEvent
    public static void registerCommand(ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(Commands.literal("overdose")
                .then(Commands.literal("apply")
                    .executes(arguments -> {
                        Entity entity = arguments.getSource().getEntity();
                        if (entity instanceof Player player) {
                            player.addEffect(new MobEffectInstance(
                                EventjarModMobEffects.OVERDOSE.get(),
                                200,
                                0,
                                false, true
                            ));
                            arguments.getSource().sendSuccess(() -> Component.literal("Applied overdose effect"), false);
                        }
                        return 1;
                    })
                )
                .then(Commands.literal("patience")
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                        .executes(arguments -> {
                            Entity entity = arguments.getSource().getEntity();
                            if (entity instanceof Player player) {
                                var playerVars = player.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables());
                                double newValue = DoubleArgumentType.getDouble(arguments, "value");
                                playerVars.patience = newValue;
                                playerVars.syncPlayerVariables(player);
                                String status;
                                if (newValue <= -10000) {
                                    status = " (CRITICAL! Effects will trigger)";
                                } else {
                                    status = " (Normal)";
                                }
                                final String finalStatus = status;
                                arguments.getSource().sendSuccess(() -> Component.literal("Set patience to: " + newValue + finalStatus), false);
                            }
                            return 1;
                        })
                    )
                    .executes(arguments -> {
                        Entity entity = arguments.getSource().getEntity();
                        if (entity instanceof Player player) {
                            var playerVars = player.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables());
                            arguments.getSource().sendSuccess(() -> Component.literal("Current patience: " + playerVars.patience), false);
                        }
                        return 1;
                    })
                )
        );
    }
}
