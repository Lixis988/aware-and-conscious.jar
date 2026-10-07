package net.lixis.outofbound.feature.corruption;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nullable;

public final class ChunkNumberCommand {

	private ChunkNumberCommand() {
	}

	public static LiteralArgumentBuilder<CommandSourceStack> build() {
		return Commands.literal("chunk_number")
				.then(Commands.argument("from", IntegerArgumentType.integer(0, 9))
						.then(Commands.argument("to", IntegerArgumentType.integer(0, 9))
								.executes(ctx -> run(ctx, null))
								.then(Commands.argument("chunkX", IntegerArgumentType.integer())
										.then(Commands.argument("chunkZ", IntegerArgumentType.integer())
												.executes(ctx -> run(ctx, new ChunkPos(
														IntegerArgumentType.getInteger(ctx, "chunkX"),
														IntegerArgumentType.getInteger(ctx, "chunkZ"))))))));
	}

	private static int run(CommandContext<CommandSourceStack> ctx, @Nullable ChunkPos requestedChunk)
			throws CommandSyntaxException {
		ServerPlayer player = ctx.getSource().getPlayerOrException();
		int fromDigit = IntegerArgumentType.getInteger(ctx, "from");
		int toDigit = IntegerArgumentType.getInteger(ctx, "to");

		if (fromDigit == toDigit) {
			ctx.getSource().sendFailure(Component.literal(
					"[outofbound] from and to must be different digits (0-9)"));
			return 0;
		}

		ChunkNumberHandler.Result result = ChunkNumberHandler.corruptLoadedChunk(player, fromDigit, toDigit,
				requestedChunk);

		if (requestedChunk != null && !player.serverLevel().hasChunk(requestedChunk.x, requestedChunk.z)) {
			ctx.getSource().sendFailure(Component.literal(
					"[outofbound] Chunk [" + requestedChunk.x + ", " + requestedChunk.z + "] is not loaded"));
			return 0;
		}

		ctx.getSource().sendSuccess(() -> Component.literal(
				result.numericFieldsChanged() == 0
						? "[outofbound] Chunk [" + result.chunkX() + ", " + result.chunkZ()
								+ "]: no digit " + result.fromDigit() + " found in numeric data"
						: "[outofbound] Chunk [" + result.chunkX() + ", " + result.chunkZ() + "]: replaced digit "
								+ result.fromDigit() + " -> " + result.toDigit()
								+ " in " + result.numericFieldsChanged() + " numeric fields ("
								+ result.blockStatesChanged() + " blocks, "
								+ result.blockEntitiesChanged() + " block entities)"), true);
		return 1;
	}
}
