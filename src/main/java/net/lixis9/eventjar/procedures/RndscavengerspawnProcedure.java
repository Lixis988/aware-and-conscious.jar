package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RndscavengerspawnProcedure {

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		LivingEntity dead = event.getEntity();
		if (dead.level().isClientSide()) {
			return;
		}
		if (dead instanceof Player) {
			return;
		}
		if (!(dead.level() instanceof ServerLevel level)) {
			return;
		}
		if (level.getRandom().nextInt(100) != 0) {
			return;
		}
		BlockPos spawn = dead.blockPosition();
		EventjarModEntities.SCAVENGER.get().spawn(level, spawn, MobSpawnType.EVENT);
	}
}
