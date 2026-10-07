package net.lixis9.eventjar.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lixis9.eventjar.DayProcedureHandler;
import net.lixis9.eventjar.WorldVariables;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DayCommand {

    @SubscribeEvent
    public static void onRegisterCommands(ServerStartingEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();

        dispatcher.register(
            Commands.literal("eventjar")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("day")
                    .then(Commands.argument("day", IntegerArgumentType.integer(1, 20))
                        .executes(DayCommand::setDay)
                    )
                    .executes(DayCommand::getDay)
                )
                .then(Commands.literal("nextday")
                    .executes(DayCommand::nextDay)
                )
                .then(Commands.literal("resetdays")
                    .executes(DayCommand::resetDays)
                )
        );
    }

    private static int setDay(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int day = IntegerArgumentType.getInteger(context, "day");
        ServerPlayer player = context.getSource().getPlayerOrException();

        DayProcedureHandler.setWorldDay(player.level(), day);
        context.getSource().sendSuccess(() -> Component.literal("Day set to: " + day), false);

        return 1;
    }

    private static int getDay(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        WorldVariables variables = WorldVariables.get(player.level());
        long currentDay = variables.getWorldDay();

        context.getSource().sendSuccess(() -> Component.literal("Current day: " + currentDay), false);

        return 1;
    }

    private static int nextDay(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        DayProcedureHandler.incrementWorldDay(player.level());
        WorldVariables variables = WorldVariables.get(player.level());
        long newDay = variables.getWorldDay();

        context.getSource().sendSuccess(() -> Component.literal("Day advanced to: " + newDay), false);

        return 1;
    }

    private static int resetDays(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        DayProcedureHandler.resetDailyTests(player.level());
        context.getSource().sendSuccess(() -> Component.literal("Daily tests reset (rejoin to reschedule)"), false);
        return 1;
    }
}
