package net.lixis9.eventjar.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.lixis9.eventjar.EventjarMod;

public class TimerEventProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		// Оптимизированная версия таймера обратного отсчета
		startCountdown(world, x, y, z, 60);
	}
	
	private static void startCountdown(LevelAccessor world, double x, double y, double z, int seconds) {
		if (seconds < 0) {
			return;
		}
		
		// Показываем текущее значение
		if (world instanceof ServerLevel _level) {
																																		_level.getServer().getCommands().performPrefixedCommand(
																																				new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "",
																																						Component.literal(""), _level.getServer(), null).withSuppressedOutput(),
				"title @a actionbar \"" + seconds + "\""
			);
		}
		
		// Планируем следующий тик
		if (seconds > 0) {
																																	EventjarMod.queueServerWork(20, () -> {
				startCountdown(world, x, y, z, seconds - 1);
		});
	}
}
}
