package net.lixis9.eventjar.procedures;

import net.lixis.outofbound.entity.SeraphWrathEntity;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.network.ClientOsEffectPacket;

import net.minecraftforge.server.ServerLifecycleHooks;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class GodPriStolknovieniiIghrokaSSushchnostiuProcedure {

	public static void execute(LevelAccessor world, Entity godEntity, Player player) {
		if (godEntity == null || godEntity.level().isClientSide()) {
			return;
		}
		if (godEntity.getPersistentData().getBoolean("SeraphCatchDone")) {
			return;
		}
		godEntity.getPersistentData().putBoolean("SeraphCatchDone", true);

		removeAllWrathGods(world);

		final ServerPlayer target = player instanceof ServerPlayer sp ? sp : null;
		final LevelAccessor levelRef = world;

		EventjarMod.queueServerWork(5, () -> {
			removeAllWrathGods(levelRef);
			if (target != null && target.connection != null) {
				ClientOsEffectPacket.send(target, ClientOsEffectPacket.Effect.MEETCRAFT);
			}
			EventjarMod.queueServerWork(20, () -> {
				removeAllWrathGods(levelRef);
				if (!levelRef.isClientSide() && levelRef.getServer() != null) {
					ServerLifecycleHooks.getCurrentServer().stopServer();
				}
			});
		});
	}

	public static void removeAllWrathGods(LevelAccessor world) {
		if (!(world instanceof ServerLevel level)) {
			return;
		}
		List<Entity> toRemove = new ArrayList<>();
		for (Entity entity : level.getAllEntities()) {
			if (entity instanceof SeraphWrathEntity
					|| entity.getType() == OutofboundExtraEntities.SERAPH_WRATH.get()
					|| entity.getType() == EventjarModEntities.GOD.get()) {
				toRemove.add(entity);
			}
		}

		for (ServerPlayer player : level.players()) {
			AABB box = player.getBoundingBox().inflate(128.0D);
			toRemove.addAll(level.getEntitiesOfClass(SeraphWrathEntity.class, box));
		}
		for (Entity entity : toRemove) {
			if (entity.isAlive() || !entity.isRemoved()) {
				entity.discard();
			}
		}
	}
}
