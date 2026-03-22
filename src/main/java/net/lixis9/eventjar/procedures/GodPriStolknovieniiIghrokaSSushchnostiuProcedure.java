package net.lixis9.eventjar.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.lixis9.eventjar.EventjarMod;

public class GodPriStolknovieniiIghrokaSSushchnostiuProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		// Удаляем GodEntity сразу после касания
		if (!entity.level().isClientSide())
			entity.discard();
		// Запускаем Meetcraft через небольшую задержку
		EventjarMod.queueServerWork(20, () -> {
			MeetcraftProcedure.execute();
			// Останавливаем сервер через еще одну задержку
			EventjarMod.queueServerWork(20, () -> {
				if (!world.isClientSide() && world.getServer() != null)
					ServerLifecycleHooks.getCurrentServer().stopServer();
			});
		});
	}
}
