package net.lixis.outofbound.ai;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class BoundedOneAiConfigCommand {

	private BoundedOneAiConfigCommand() {
	}

	public static LiteralArgumentBuilder<CommandSourceStack> build() {
		return Commands.literal("ai")
				.requires(source -> source.hasPermission(2))
				.executes(BoundedOneAiConfigCommand::status)
				.then(Commands.literal("on")
						.executes(ctx -> setEnabled(ctx, true)))
				.then(Commands.literal("off")
						.executes(ctx -> setEnabled(ctx, false)))
				.then(Commands.literal("reload")
						.executes(BoundedOneAiConfigCommand::reload))
				.then(Commands.literal("set")
						.then(Commands.argument("key", StringArgumentType.word())
								.then(Commands.argument("value", StringArgumentType.greedyString())
										.executes(BoundedOneAiConfigCommand::set))));
	}

	private static int status(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		BoundedOneAiService service = BoundedOneAiService.get();

		source.sendSuccess(() -> Component.literal("--- Bounded One AI ---"), false);
		source.sendSuccess(() -> Component.literal(
				"enabled: " + (BoundedOneAiConfig.enabled ? "ON" : "OFF")
						+ " | config modelFile: " + (BoundedOneAiConfig.modelFile.isEmpty()
						? "(default " + BoundedOneModelExtractor.MODEL_FILE_NAME + ")"
						: BoundedOneAiConfig.modelFile)), false);
		source.sendSuccess(() -> Component.literal(
				"loaded: " + service.loadedModelPathLabel()
						+ " | nGpuLayers: " + service.loadedGpuLayers()), false);
		source.sendSuccess(() -> Component.literal(
				"runtime: " + service.runtimeStateLabel()
						+ " | ready: " + service.isReady()
						+ " | chatTemplate: " + BoundedOneAiConfig.resolvedChatTemplate()), false);
		source.sendSuccess(() -> Component.literal(
				"temperature: " + BoundedOneAiConfig.temperature
						+ " | topP: " + BoundedOneAiConfig.topP
						+ " | maxTokens: " + BoundedOneAiConfig.maxTokens), false);
		source.sendSuccess(() -> Component.literal(
				"cooldownSeconds: " + BoundedOneAiConfig.cooldownSeconds
						+ " | maxReplyLength: " + BoundedOneAiConfig.maxReplyLength), false);
		source.sendSuccess(() -> Component.literal(
				"glitch%: " + BoundedOneAiConfig.glitchReplyChancePercent
						+ " | pseudocode%: " + BoundedOneAiConfig.pseudocodeReplyChancePercent), false);
		source.sendSuccess(() -> Component.literal(
				"influence: " + (BoundedOneAiConfig.enableInfluence ? "ON" : "OFF")
						+ " | modelChoosesActions: " + (BoundedOneAiConfig.modelChoosesActions ? "ON" : "OFF")), false);
		source.sendSuccess(() -> Component.literal(
				"spontaneous: " + (BoundedOneAiConfig.enableSpontaneous ? "ON" : "OFF")
						+ " | mazeOnly: " + (BoundedOneAiConfig.respondInMazeOnly ? "ON" : "OFF")), false);
		source.sendSuccess(() -> Component.literal(
				"Use: /outofbound_config ai on|off|reload | /outofbound_config ai set <key> <value>"), false);
		return 1;
	}

	private static int setEnabled(CommandContext<CommandSourceStack> ctx, boolean enabled) {
		BoundedOneAiConfig.setEnabled(enabled);
		CommandSourceStack source = ctx.getSource();
		source.sendSuccess(() -> Component.literal(
				"Bounded One AI " + (enabled ? "enabled" : "disabled")), true);
		return 1;
	}

	private static int reload(CommandContext<CommandSourceStack> ctx) {
		BoundedOneAiService.get().requestModelReload();
		ctx.getSource().sendSuccess(() -> Component.literal(
				"Bounded One AI model reload requested"), true);
		return 1;
	}

	private static int set(CommandContext<CommandSourceStack> ctx) {
		String key = StringArgumentType.getString(ctx, "key");
		String value = StringArgumentType.getString(ctx, "value");
		CommandSourceStack source = ctx.getSource();
		try {
			BoundedOneAiConfig.applySetting(key, value);
		} catch (IllegalArgumentException exception) {
			source.sendFailure(Component.literal(exception.getMessage()));
			return 0;
		}
		source.sendSuccess(() -> Component.literal(
				"Bounded One AI: " + key + " = " + value.trim()), true);
		return 1;
	}
}
