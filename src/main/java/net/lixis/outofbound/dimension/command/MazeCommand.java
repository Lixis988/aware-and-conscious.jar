package net.lixis.outofbound.dimension.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.DimensionManager;
import net.lixis.outofbound.dimension.MazeSafeSpawn;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class MazeCommand {

	private static final String INDEX_TAG = "outofbound_maze_index";

	private MazeCommand() {
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		register(event.getDispatcher());
	}

	private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("outofbound_dim")
				.requires(source -> source.hasPermission(2))
				.then(Commands.literal("go")
						.then(Commands.argument("index", LongArgumentType.longArg(0))
								.executes(ctx -> go(ctx, LongArgumentType.getLong(ctx, "index")))))
				.then(Commands.literal("next")
						.executes(MazeCommand::next))
				.then(Commands.literal("leave")
						.executes(MazeCommand::leave)));
	}

	private static int go(CommandContext<CommandSourceStack> ctx, long index) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player;
		try {
			player = source.getPlayerOrException();
		} catch (Exception e) {
			source.sendFailure(Component.literal("This command must be run by a player."));
			return 0;
		}

		MinecraftServer server = source.getServer();
		ServerLevel target = DimensionManager.getOrCreateMazeLevel(server, index);
		if (target == null) {
			source.sendFailure(Component.literal("Could not open maze dimension #" + index
					+ " (dynamic dimensions may be disabled or creation failed)."));
			return 0;
		}

		net.lixis.outofbound.dimension.MazePortalPlacer.ensurePortal(target);
		teleportIntoMaze(player, target);
		player.getPersistentData().putLong(INDEX_TAG, index);
		source.sendSuccess(() -> Component.literal("Entered maze dimension #" + index), true);
		return 1;
	}

	private static int next(CommandContext<CommandSourceStack> ctx) {
		ServerPlayer player;
		try {
			player = ctx.getSource().getPlayerOrException();
		} catch (Exception e) {
			ctx.getSource().sendFailure(Component.literal("This command must be run by a player."));
			return 0;
		}
		long current = player.getPersistentData().getLong(INDEX_TAG);
		return go(ctx, current + 1L);
	}

	private static int leave(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player;
		try {
			player = source.getPlayerOrException();
		} catch (Exception e) {
			source.sendFailure(Component.literal("This command must be run by a player."));
			return 0;
		}

		ServerLevel overworld = source.getServer().overworld();
		BlockPos spawn = overworld.getSharedSpawnPos();
		player.teleportTo(overworld, spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D,
				player.getYRot(), player.getXRot());
		player.gameMode.setLevel(overworld);
		source.sendSuccess(() -> Component.literal("Returned to the overworld."), true);
		return 1;
	}

	private static void teleportIntoMaze(ServerPlayer player, ServerLevel level) {
		MazeSafeSpawn.teleportPlayerToMaze(player, level);
	}
}
