package net.lixis9.eventjar;

import net.lixis.outofbound.entity.SeraphWrathEntity;
import net.lixis9.eventjar.entity.ErrundefineEntity;
import net.lixis9.eventjar.entity.FaultEntity;
import net.lixis9.eventjar.entity.ScavengerEntity;
import net.lixis9.eventjar.entity.SeekeractEntity;
import net.lixis9.eventjar.network.SeekerCatchShakePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerDeathHandler {

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}

		Entity killer = event.getSource().getEntity();
		if (killer == null) {
			killer = event.getSource().getDirectEntity();
		}
		if (killer instanceof SeekeractEntity) {
			SeekerCatchShakePacket.send(player, false);
		}

		if (isAggressiveKiller(killer)) {
			killer.discard();
		}

		if (killer instanceof FaultEntity
				|| killer instanceof ScavengerEntity
				|| killer instanceof ErrundefineEntity) {
			player.connection.disconnect(Component.literal("§4§lITS ALL YOUR FAULT"));
		}
	}

	private static boolean isAggressiveKiller(Entity killer) {
		if (killer == null || killer.isRemoved()) {
			return false;
		}
		if (killer instanceof FaultEntity fault) {
			return fault.isAggressive();
		}
		if (killer instanceof ScavengerEntity scavenger) {
			return scavenger.isAggressive();
		}
		if (killer instanceof SeraphWrathEntity wrath) {
			return wrath.isAggressive();
		}
		return killer instanceof SeekeractEntity || killer instanceof ErrundefineEntity;
	}
}
