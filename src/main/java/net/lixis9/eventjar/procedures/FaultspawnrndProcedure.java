package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FaultspawnrndProcedure {

	@SubscribeEvent
	public static void onXpLevelChange(PlayerXpEvent.LevelChange event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (!(player.level() instanceof ServerLevel level)) {
			return;
		}
		if (player.getRandom().nextInt(30) != 0) {
			return;
		}
		BlockPos spawn = player.blockPosition().offset(0, 0, 20);
		EventjarModEntities.FAULT.get().spawn(level, spawn, MobSpawnType.EVENT);
	}
}
