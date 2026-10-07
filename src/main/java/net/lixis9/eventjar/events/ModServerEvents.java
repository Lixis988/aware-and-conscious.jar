package net.lixis9.eventjar.events;

import net.lixis9.eventjar.entity.EyesindarkEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "eventjar", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModServerEvents {

	private static final boolean ENABLED = false;

	private static final HashMap<UUID, PlayerData> playerDataMap = new HashMap<>();
	private static final int SPAWN_TIME = 60;
	private static final int DESPAWN_TIME = 60;
	private static final double MIN_SPAWN_DISTANCE = 8.0;
	private static final double MAX_SPAWN_DISTANCE = 15.0;
	private static final double VIEW_ANGLE_THRESHOLD = 0.9848;

	public static class PlayerData {
		public int darkLookingTimer = 0;
		public int notLookingTimer = 0;
		public EyesindarkEntity currentEntity = null;
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null) {
			return;
		}
		if (!ENABLED) {
			if (!playerDataMap.isEmpty()) {
				for (PlayerData data : playerDataMap.values()) {
					resetTimersAndRemoveEntity(data);
				}
				playerDataMap.clear();
			}
			if (server.getTickCount() % 40 == 0) {
				for (ServerPlayer player : server.getPlayerList().getPlayers()) {
					for (EyesindarkEntity eye : player.level().getEntitiesOfClass(
							EyesindarkEntity.class, player.getBoundingBox().inflate(64.0D))) {
						eye.discard();
					}
				}
			}
			return;
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			PlayerData data = playerDataMap.computeIfAbsent(player.getUUID(), id -> new PlayerData());
			updatePlayerData(player, data);
		}
	}

	private static void updatePlayerData(ServerPlayer player, PlayerData data) {
		Level world = player.level();
		if (world.getMaxLocalRawBrightness(player.blockPosition()) > 4) {
			resetTimersAndRemoveEntity(data);
			return;
		}

		Vec3 lookVec = player.getLookAngle();
		if (lookVec.lengthSqr() < 1.0E-8) {
			return;
		}
		Vec3 spawnOffset = lookVec.normalize().scale(MIN_SPAWN_DISTANCE + (MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE) * 0.5);
		Vec3 spawnPos = player.getEyePosition(1.0F).add(spawnOffset);
		BlockPos targetPos = BlockPos.containing(spawnPos.x, spawnPos.y, spawnPos.z);

		if (!isValidSpawnPosition(world, targetPos)) {
			data.darkLookingTimer = 0;
			return;
		}

		if (world.getMaxLocalRawBrightness(targetPos) <= 4) {
			data.darkLookingTimer++;
			if (data.darkLookingTimer >= SPAWN_TIME && (data.currentEntity == null || !data.currentEntity.isAlive())) {
				spawnEyesEntity(player, data, spawnPos);
			}
		} else {
			data.darkLookingTimer = 0;
		}

		if (data.currentEntity != null && data.currentEntity.isAlive()) {
			if (isPlayerLookingAtEntity(player, data.currentEntity)) {
				data.notLookingTimer = 0;
				if (player.tickCount % 20 == 0) {
					player.hurt(player.damageSources().magic(), 1.0F);
				}
			} else {
				data.notLookingTimer++;
				if (data.notLookingTimer >= DESPAWN_TIME) {
					data.currentEntity.remove(Entity.RemovalReason.DISCARDED);
					data.currentEntity = null;
					data.notLookingTimer = 0;
					data.darkLookingTimer = 0;
				}
			}
		} else {
			data.currentEntity = null;
		}
	}

	private static boolean isValidSpawnPosition(Level world, BlockPos pos) {
		return world.getWorldBorder().isWithinBounds(pos) && world.getBlockState(pos).isAir();
	}

	private static boolean isPlayerLookingAtEntity(ServerPlayer player, EyesindarkEntity entity) {
		Vec3 toEntityVec = entity.position().subtract(player.getEyePosition(1.0F));
		if (toEntityVec.lengthSqr() < 1.0E-8) {
			return true;
		}
		return player.getLookAngle().dot(toEntityVec.normalize()) > VIEW_ANGLE_THRESHOLD;
	}

	private static void spawnEyesEntity(ServerPlayer player, PlayerData data, Vec3 spawnPos) {
		EyesindarkEntity entity = new EyesindarkEntity(EventjarModEntities.EYESINDARK.get(), player.level());
		entity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
		player.level().addFreshEntity(entity);
		data.currentEntity = entity;
		data.notLookingTimer = 0;
	}

	private static void resetTimersAndRemoveEntity(PlayerData data) {
		data.darkLookingTimer = 0;
		data.notLookingTimer = 0;
		if (data.currentEntity != null) {
			data.currentEntity.remove(Entity.RemovalReason.DISCARDED);
			data.currentEntity = null;
		}
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			PlayerData data = playerDataMap.remove(player.getUUID());
			if (data != null) {
				resetTimersAndRemoveEntity(data);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			PlayerData data = playerDataMap.get(player.getUUID());
			if (data != null) {
				resetTimersAndRemoveEntity(data);
			}
		}
	}
}
