package net.lixis9.eventjar.procedures;

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

public class Risperidone4Procedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		return execute(null, world, x, y, z, entity);
	}

	private static boolean execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables())).PatienceForRisperidone >= 9600) {
			return true;
		}
		return false;
	}

	public static void maybeAnnounce(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null || world == null)
			return;
		var vars = entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables());
		if (vars.PatienceForRisperidone < 9600)
			return;
		if (entity.getPersistentData().getBoolean("eventjar_risp4_announced"))
			return;
		entity.getPersistentData().putBoolean("eventjar_risp4_announced", true);
		if (world instanceof ServerLevel _level) {
			_level.getServer().getCommands().performPrefixedCommand(
					new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "",
							Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
					"title @a actionbar \"I feel bad, I need to take some pills\"");
		}
	}
}
