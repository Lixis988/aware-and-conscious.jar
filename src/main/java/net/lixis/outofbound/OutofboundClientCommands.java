package net.lixis.outofbound;

import net.lixis9.eventjar.DarknessConfig;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.lixis.outofbound.feature.corruption.ChunkNumberRequestPacket;
import net.lixis.outofbound.feature.corruption.CorruptionClientState;
import net.lixis.outofbound.feature.corruption.CorruptionLevelRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OutofboundClientCommands {

	@SubscribeEvent
	public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> test = Commands.literal("test")
				.then(Commands.literal("shader")
						.executes(context -> toggleShader(context.getSource(),
								TestShaderHandler.GRAPHICAL_BUG, "Graphical bug")))
				.then(Commands.literal("shader2")
						.executes(context -> toggleShader(context.getSource(),
								TestShaderHandler.INVERSION, "Inversion")))
				.then(Commands.literal("shader3")
						.executes(context -> toggleShader(context.getSource(),
								TestShaderHandler.POSTERIZE, "Posterize")))
				.then(Commands.literal("shader4")
						.executes(context -> toggleShader(context.getSource(),
								TestShaderHandler.TEXTURE_SWAP, "Texture swap")))
				.then(Commands.literal("shader5")
						.executes(context -> toggleShader(context.getSource(),
								TestShaderHandler.RANDOM, "Random")))
				.then(Commands.literal("shader6")
						.executes(context -> {
							if (!DarknessConfig.ENABLE_MEMORY_CORRUPTION) {
								context.getSource().sendFailure(Component.literal(
										"[outofbound] Memory corruption is disabled in config"));
								return 0;
							}
							boolean on = net.lixis.outofbound.feature.corruption.render.MemoryCorruptionGate.toggleManualOverride();
							context.getSource().sendSuccess(() -> Component.literal(
									"[outofbound] Memory corruption " + (on ? "ENABLED" : "DISABLED")), false);
							return Command.SINGLE_SUCCESS;
						}));

		LiteralArgumentBuilder<CommandSourceStack> damage = Commands.literal("damage")
				.executes(context -> {
					requestServerCorruption(CorruptionLevelRequestPacket.TOGGLE, 0.0F);
					context.getSource().sendSuccess(() -> Component.literal(
							"[outofbound] Corruption toggle sent to server"), false);
					return Command.SINGLE_SUCCESS;
				})
				.then(Commands.literal("off")
						.executes(context -> {
							requestServerCorruption(CorruptionLevelRequestPacket.OFF, 0.0F);
							context.getSource().sendSuccess(() -> Component.literal(
									"[outofbound] Corruption disable sent to server"), false);
							return Command.SINGLE_SUCCESS;
						}))
				.then(Commands.literal("status")
						.executes(context -> {
							float level = CorruptionClientState.getLevel();
							context.getSource().sendSuccess(() -> Component.literal(
									"[outofbound] Corruption level: " + (int) level), false);
							return Command.SINGLE_SUCCESS;
						}))
				.then(Commands.argument("level", FloatArgumentType.floatArg(0.0F, 100.0F))
						.executes(context -> {
							float level = FloatArgumentType.getFloat(context, "level");
							requestServerCorruption(CorruptionLevelRequestPacket.SET, level);
							context.getSource().sendSuccess(() -> Component.literal(
									"[outofbound] Corruption level " + (int) level + " sent to server"), false);
							return Command.SINGLE_SUCCESS;
						}));

		event.getDispatcher().register(
				Commands.literal("outofbound_config")
						.executes(context -> {
							Minecraft.getInstance().setScreen(new OutofboundConfigScreen());
							return Command.SINGLE_SUCCESS;
						})
						.then(test)
						.then(damage)
						.then(Commands.literal("chunk_number")
								.then(Commands.argument("from", IntegerArgumentType.integer(0, 9))
										.then(Commands.argument("to", IntegerArgumentType.integer(0, 9))
												.executes(context -> {
													int from = IntegerArgumentType.getInteger(context, "from");
													int to = IntegerArgumentType.getInteger(context, "to");
													requestChunkNumber(from, to);
													context.getSource().sendSuccess(() -> Component.literal(
															"[outofbound] Chunk number " + from + " -> " + to
																	+ " sent to server"), false);
													return Command.SINGLE_SUCCESS;
												})
												.then(Commands.argument("chunkX", IntegerArgumentType.integer())
														.then(Commands.argument("chunkZ", IntegerArgumentType.integer())
																.executes(context -> {
																	int from = IntegerArgumentType.getInteger(context, "from");
																	int to = IntegerArgumentType.getInteger(context, "to");
																	int chunkX = IntegerArgumentType.getInteger(context, "chunkX");
																	int chunkZ = IntegerArgumentType.getInteger(context, "chunkZ");
																	requestChunkNumber(from, to, chunkX, chunkZ);
																	context.getSource().sendSuccess(() -> Component.literal(
																			"[outofbound] Chunk number " + from + " -> " + to
																					+ " for chunk [" + chunkX + ", " + chunkZ
																					+ "] sent to server"), false);
																	return Command.SINGLE_SUCCESS;
																})))))));
	}

	private static void requestChunkNumber(int fromDigit, int toDigit) {
		if (Minecraft.getInstance().player == null) {
			return;
		}
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.SERVER.noArg(),
				new ChunkNumberRequestPacket(fromDigit, toDigit));
	}

	private static void requestChunkNumber(int fromDigit, int toDigit, int chunkX, int chunkZ) {
		if (Minecraft.getInstance().player == null) {
			return;
		}
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.SERVER.noArg(),
				new ChunkNumberRequestPacket(fromDigit, toDigit, chunkX, chunkZ));
	}

	private static void requestServerCorruption(byte mode, float level) {
		if (Minecraft.getInstance().player == null) {
			return;
		}
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.SERVER.noArg(),
				new CorruptionLevelRequestPacket(mode, level));
	}

	private static int toggleShader(CommandSourceStack source,
			net.minecraft.resources.ResourceLocation shader, String label) {
		boolean on = TestShaderHandler.toggleManual(shader);
		source.sendSuccess(() -> Component.literal(
				"[outofbound] " + label + " shader " + (on ? "ENABLED" : "DISABLED")), false);
		return Command.SINGLE_SUCCESS;
	}
}
