package net.lixis9.eventjar.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.lixis.outofbound.OpenConfigScreenPacket;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.ai.BoundedOneAiConfigCommand;
import net.lixis.outofbound.feature.corruption.ChunkNumberCommand;
import net.lixis.outofbound.feature.corruption.CorruptionCommand;
import net.lixis9.eventjar.AacConfig;
import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.procedures.BloodRainProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AacConfigCommand {

	private AacConfigCommand() {
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(buildRoot("aac_config"));
		event.getDispatcher().register(buildRoot("outofbound_config"));
		event.getDispatcher().register(
				Commands.literal("configevent")
						.requires(source -> source.hasPermission(0))
						.executes(AacConfigCommand::openGui));
		event.getDispatcher().register(
				Commands.literal("safemode")
						.requires(source -> source.hasPermission(0))
						.executes(AacConfigCommand::safeModeStatus)
						.then(Commands.literal("on").executes(ctx -> setSafeMode(ctx, true)))
						.then(Commands.literal("off").executes(ctx -> setSafeMode(ctx, false)))
						.then(Commands.literal("status").executes(AacConfigCommand::safeModeStatus)));
	}

	private static LiteralArgumentBuilder<CommandSourceStack> buildRoot(String name) {
		return Commands.literal(name)
				.requires(source -> source.hasPermission(0))
				.executes(AacConfigCommand::statusAndGui)
				.then(Commands.literal("gui")
						.requires(source -> source.hasPermission(0))
						.executes(AacConfigCommand::openGui))
				.then(Commands.literal("safemode")
						.requires(source -> source.hasPermission(0))
						.executes(AacConfigCommand::safeModeStatus)
						.then(Commands.literal("on").executes(ctx -> setSafeMode(ctx, true)))
						.then(Commands.literal("off").executes(ctx -> setSafeMode(ctx, false)))
						.then(Commands.literal("status").executes(AacConfigCommand::safeModeStatus)))
				.then(buildBoolToggle("text", "textDistortion", () -> AacConfig.TEXT_DISTORTION,
						AacConfigCommand::setText))
				.then(buildBoolToggle("jumbled", "jumbledText", () -> AacConfig.TEXT_DISTORTION,
						AacConfigCommand::setText))
				.then(buildBoolToggle("items", "itemRenamer", () -> AacConfig.ITEM_RENAMER,
						AacConfigCommand::setItems))
				.then(buildBoolToggle("unstackable", "unstackableItems", () -> AacConfig.ITEM_RENAMER,
						AacConfigCommand::setItems))
				.then(Commands.literal("spawn")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("entity")
								.then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D, 2.0D))
										.executes(ctx -> setSpawn(ctx, "entity"))))
						.then(Commands.literal("event")
								.then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D, 2.0D))
										.executes(ctx -> setSpawn(ctx, "event"))))
						.then(Commands.literal("structure")
								.then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0D, 2.0D))
										.executes(ctx -> setSpawn(ctx, "structure")))))
				.then(Commands.literal("signs")
						.requires(source -> source.hasPermission(2))
						.executes(ctx -> boolStatus(ctx, "enableSigns", AacConfig.ENABLE_SIGNS))
						.then(Commands.literal("on").executes(ctx -> setSigns(ctx, true)))
						.then(Commands.literal("off").executes(ctx -> setSigns(ctx, false)))
						.then(Commands.argument("enabled", BoolArgumentType.bool())
								.executes(ctx -> setSigns(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
				.then(Commands.literal("structures")
						.requires(source -> source.hasPermission(2))
						.executes(ctx -> boolStatus(ctx, "enableStructures", AacConfig.ENABLE_STRUCTURES))
						.then(Commands.literal("on").executes(ctx -> setStructures(ctx, true)))
						.then(Commands.literal("off").executes(ctx -> setStructures(ctx, false)))
						.then(Commands.argument("enabled", BoolArgumentType.bool())
								.executes(ctx -> setStructures(ctx, BoolArgumentType.getBool(ctx, "enabled")))))
				.then(Commands.literal("bloodrain")
						.requires(source -> source.hasPermission(0))
						.executes(ctx -> boolStatus(ctx, "enableBloodRain", AacConfig.ENABLE_BLOOD_RAIN))
						.then(Commands.literal("on").executes(ctx -> setBloodRain(ctx, true)))
						.then(Commands.literal("off").executes(ctx -> setBloodRain(ctx, false)))
						.then(Commands.literal("now").requires(source -> source.hasPermission(2))
								.executes(AacConfigCommand::triggerBloodRain)))
				.then(BoundedOneAiConfigCommand.build())
				.then(CorruptionCommand.build("corruption"))
				.then(CorruptionCommand.build())
				.then(ChunkNumberCommand.build());
	}

	private static int statusAndGui(CommandContext<CommandSourceStack> ctx) {
		dumpStatus(ctx.getSource());
		return openGui(ctx);
	}

	private static int openGui(CommandContext<CommandSourceStack> ctx) {
		try {
			ServerPlayer player = ctx.getSource().getPlayerOrException();
			OutofboundMod.PACKET_HANDLER.send(
					PacketDistributor.PLAYER.with(() -> player),
					new OpenConfigScreenPacket());
		} catch (Exception e) {
			ctx.getSource().sendFailure(Component.literal("Players only can open the config GUI."));
			return 0;
		}
		return Command.SINGLE_SUCCESS;
	}

	private static void dumpStatus(CommandSourceStack source) {
		source.sendSuccess(() -> Component.literal("--- AAC config ---"), false);
		source.sendSuccess(() -> Component.literal(
				"safeMode: " + onOff(AacConfig.SAFE_MODE)
						+ " (chosen: " + AacConfig.SAFE_MODE_CHOSEN + ")"), false);
		source.sendSuccess(() -> Component.literal(
				"textDistortion: " + onOff(AacConfig.TEXT_DISTORTION)
						+ " | itemRenamer: " + onOff(AacConfig.ITEM_RENAMER)), false);
		source.sendSuccess(() -> Component.literal(
				"spawn entity/event/structure: "
						+ fmt(AacConfig.ENTITY_SPAWN_MULTIPLIER) + " / "
						+ fmt(AacConfig.EVENT_SPAWN_MULTIPLIER) + " / "
						+ fmt(AacConfig.STRUCTURE_SPAWN_MULTIPLIER)), false);
		source.sendSuccess(() -> Component.literal(
				"signs: " + onOff(AacConfig.ENABLE_SIGNS)
						+ " | structures: " + onOff(AacConfig.ENABLE_STRUCTURES)
						+ " | bloodRain: " + onOff(AacConfig.ENABLE_BLOOD_RAIN)), false);
		source.sendSuccess(() -> Component.literal(
				"Use /aac_config gui | safemode | text/jumbled | items/unstackable | spawn | signs | structures | bloodrain | ai | corruption"), false);
	}

	private static LiteralArgumentBuilder<CommandSourceStack> buildBoolToggle(
			String name,
			String statusKey,
			java.util.function.BooleanSupplier reader,
			java.util.function.BiFunction<CommandContext<CommandSourceStack>, Boolean, Integer> setter) {
		return Commands.literal(name)
				.requires(source -> source.hasPermission(0))
				.executes(ctx -> boolStatus(ctx, statusKey, reader.getAsBoolean()))
				.then(Commands.literal("on").executes(ctx -> setter.apply(ctx, true)))
				.then(Commands.literal("off").executes(ctx -> setter.apply(ctx, false)));
	}

	private static int safeModeStatus(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> Component.literal(
				"Safe Mode: " + onOff(AacConfig.SAFE_MODE)
						+ " (chosen: " + AacConfig.SAFE_MODE_CHOSEN + ")"), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setSafeMode(CommandContext<CommandSourceStack> ctx, boolean enabled) {
		AacConfig.setSafeMode(enabled);
		ctx.getSource().sendSuccess(() -> Component.literal(
				"Safe Mode " + onOff(enabled)
						+ (enabled
						? " — OS/desktop side-effects are blocked."
						: " — OS/desktop side-effects allowed (Normal).")), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int setText(CommandContext<CommandSourceStack> ctx, boolean enabled) {
		AacConfig.setTextDistortion(enabled);
		net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(
				net.minecraftforge.api.distmarker.Dist.CLIENT,
				() -> () -> net.lixis9.eventjar.DarknessConfig.setRandomLabels(enabled));
		ctx.getSource().sendSuccess(() -> Component.literal("jumbled/textDistortion: " + onOff(enabled)), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int setItems(CommandContext<CommandSourceStack> ctx, boolean enabled) {
		AacConfig.setItemRenamer(enabled);
		ctx.getSource().sendSuccess(() -> Component.literal("itemRenamer: " + onOff(enabled)), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int setSpawn(CommandContext<CommandSourceStack> ctx, String kind) {
		double value = DoubleArgumentType.getDouble(ctx, "value");
		switch (kind) {
			case "entity" -> AacConfig.setEntitySpawnMultiplier(value);
			case "event" -> AacConfig.setEventSpawnMultiplier(value);
			case "structure" -> AacConfig.setStructureSpawnMultiplier(value);
			default -> {
				ctx.getSource().sendFailure(Component.literal("Unknown spawn kind: " + kind));
				return 0;
			}
		}
		ctx.getSource().sendSuccess(() -> Component.literal(
				"spawn " + kind + " multiplier = " + fmt(value)), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int setSigns(CommandContext<CommandSourceStack> ctx, boolean enabled) {
		AacConfig.setEnableSigns(enabled);
		ctx.getSource().sendSuccess(() -> Component.literal("enableSigns: " + onOff(enabled)), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int setStructures(CommandContext<CommandSourceStack> ctx, boolean enabled) {
		AacConfig.setEnableStructures(enabled);
		ctx.getSource().sendSuccess(() -> Component.literal("enableStructures: " + onOff(enabled)), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int setBloodRain(CommandContext<CommandSourceStack> ctx, boolean enabled) {
		AacConfig.setEnableBloodRain(enabled);
		ctx.getSource().sendSuccess(() -> Component.literal("enableBloodRain: " + onOff(enabled)), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int triggerBloodRain(CommandContext<CommandSourceStack> ctx) {
		ServerLevel level = ctx.getSource().getLevel();
		if (!BloodRainProcedure.start(level, 20 * 90)) {
			ctx.getSource().sendFailure(Component.literal("Blood rain is disabled in config."));
			return 0;
		}
		ctx.getSource().sendSuccess(() -> Component.literal("Blood rain started (90s)."), true);
		return Command.SINGLE_SUCCESS;
	}

	private static int boolStatus(CommandContext<CommandSourceStack> ctx, String key, boolean value) {
		ctx.getSource().sendSuccess(() -> Component.literal(key + ": " + onOff(value)), false);
		return Command.SINGLE_SUCCESS;
	}

	private static String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}

	private static String fmt(double value) {
		return String.format(java.util.Locale.ROOT, "%.2f", value);
	}
}
