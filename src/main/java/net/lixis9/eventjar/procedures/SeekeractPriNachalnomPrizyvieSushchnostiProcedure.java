package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.network.SeekerCatchShakePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class SeekeractPriNachalnomPrizyvieSushchnostiProcedure {
	private static final int LIFETIME_TICKS = 1200;

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		TimerEventProcedure.execute(world, x, y, z);
		if (world instanceof ServerLevel level) {
			SeekerCatchShakePacket.sendToAll(level, true);
		}
		if (entity == null) {
			return;
		}
		EventjarMod.queueServerWork(LIFETIME_TICKS, () -> {
			if (!entity.level().isClientSide() && !entity.isRemoved()) {
				entity.discard();
			}
		});
	}
}
