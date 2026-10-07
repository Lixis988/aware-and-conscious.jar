package net.lixis.outofbound.feature.corruption;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CorruptionCommand {

	private CorruptionCommand() {
	}

	public static LiteralArgumentBuilder<CommandSourceStack> build() {
		return build("damage");
	}

	public static LiteralArgumentBuilder<CommandSourceStack> build(String literal) {
		return Commands.literal(literal)
				.requires(source -> source.hasPermission(2))
				.executes(CorruptionCommand::toggle)
				.then(Commands.literal("off")
						.executes(CorruptionCommand::off))
				.then(Commands.literal("status")
						.executes(CorruptionCommand::status))
				.then(Commands.argument("level", FloatArgumentType.floatArg(0.0F, 100.0F))
						.executes(CorruptionCommand::setLevel));
	}

	private static int toggle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerPlayer player = ctx.getSource().getPlayerOrException();
		float current = CorruptionAPI.getLevel(player.getUUID());
		if (current > 0.0F) {
			CorruptionAPI.clearManualAndApplyWorldBaseline(player);
			ctx.getSource().sendSuccess(() -> Component.literal(
					"[outofbound] Corruption returned to world baseline"), true);
		} else {
			CorruptionAPI.setLevelManual(player, 50.0F);
			ctx.getSource().sendSuccess(() -> Component.literal(
					"[outofbound] Corruption ENABLED (manual level 50)"), true);
		}
		return 1;
	}

	private static int off(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerPlayer player = ctx.getSource().getPlayerOrException();
		CorruptionAPI.clearManualAndApplyWorldBaseline(player);
		float level = CorruptionAPI.getLevel(player.getUUID());
		ctx.getSource().sendSuccess(() -> Component.literal(
				"[outofbound] Corruption manual override cleared (world level " + (int) level + ")"), true);
		return 1;
	}

	private static int status(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerPlayer player = ctx.getSource().getPlayerOrException();
		float level = CorruptionAPI.getLevel(player.getUUID());
		boolean manual = OutofboundMod.CORRUPTION.hasManualOverride(player.getUUID());
		ctx.getSource().sendSuccess(() -> Component.literal(
				"[outofbound] Corruption level: " + (int) level + (manual ? " (manual)" : " (world)")), false);
		return 1;
	}

	private static int setLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerPlayer player = ctx.getSource().getPlayerOrException();
		float level = FloatArgumentType.getFloat(ctx, "level");
		CorruptionAPI.setLevelManual(player, level);
		ctx.getSource().sendSuccess(() -> Component.literal(
				"[outofbound] Corruption level set to " + (int) level + " (manual)"), true);
		return 1;
	}
}
