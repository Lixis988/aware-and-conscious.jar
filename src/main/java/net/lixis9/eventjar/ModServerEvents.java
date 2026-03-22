package net.lixis9.eventjar.events;

import net.lixis9.eventjar.entity.EyesindarkEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "eventjar", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModServerEvents {

    private static final HashMap<UUID, PlayerData> playerDataMap = new HashMap<>();
    // Настройки спавна
    private static final int SPAWN_TIME = 60; // 3 секунды (60 тиков) взгляда в темноту
    private static final int DESPAWN_TIME = 60; // 3 секунды (60 тиков) до исчезновения
    private static final double MIN_SPAWN_DISTANCE = 8.0; // Минимальная дистанция спавна от игрока
    private static final double MAX_SPAWN_DISTANCE = 15.0; // Максимальная дистанция спавна от игрока
    private static final double VIEW_ANGLE_THRESHOLD = 0.9848; // cos(10°) для угла обзора 10 градусов

    public static class PlayerData {
        public int darkLookingTimer = 0;
        public int notLookingTimer = 0;
        public EyesindarkEntity currentEntity = null;
        public BlockPos lastDarkPos = null;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID playerId = player.getUUID();
                PlayerData data = playerDataMap.get(playerId);
                if (data == null) {
                    data = new PlayerData();
                    playerDataMap.put(playerId, data);
                }
                updatePlayerData(player, data);
            }
        }
    }

    private static void updatePlayerData(ServerPlayer player, PlayerData data) {
        Level world = player.level();

        // Проверяем уровень света вокруг игрока (блочный + небесный)
        BlockPos playerPos = player.blockPosition();
        int blockLight = world.getBrightness(LightLayer.BLOCK, playerPos);
        int skyLight = world.getBrightness(LightLayer.SKY, playerPos);
        int totalLight = Math.max(blockLight, skyLight);

        // Если свет слишком яркий (увеличим порог для большей избирательности), сбрасываем таймеры
        if (totalLight > 4) { // Порог снижен для спавна только в действительно тёмных местах
            resetTimersAndRemoveEntity(data);
            return;
        }

        // Определяем точку для спавна в пределах заданной дистанции
        Vec3 lookVec = player.getLookAngle();
        // Нормализуем и умножаем на желаемую дистанцию
        Vec3 spawnOffset = lookVec.normalize().scale(MIN_SPAWN_DISTANCE + (MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE) * 0.5);
        Vec3 spawnPos = player.getEyePosition(1.0F).add(spawnOffset);

        // Проверяем, что точка спавна находится в твёрдом блоке (например, в пещере, а не на открытом воздухе)
        BlockPos targetPos = new BlockPos((int) spawnPos.x, (int) spawnPos.y, (int) spawnPos.z);
        if (!isValidSpawnPosition(world, targetPos)) {
            data.darkLookingTimer = 0; // Сбрасываем таймер, если место неподходящее
            return;
        }

        int targetLightLevel = world.getMaxLocalRawBrightness(targetPos);

        if (targetLightLevel <= 4) { // Темное место
            data.darkLookingTimer++;
            data.lastDarkPos = targetPos;

            // Если игрок смотрит в темноту 3 секунды и сущности нет - создаем
            if (data.darkLookingTimer >= SPAWN_TIME && data.currentEntity == null) {
                spawnEyesEntity(player, data, spawnPos);
            }
        } else {
            data.darkLookingTimer = 0;
        }

        // Если сущность существует, проверяем взгляд игрока
        if (data.currentEntity != null && data.currentEntity.isAlive()) {
            if (isPlayerLookingAtEntity(player, data.currentEntity)) {
                // Игрок смотрит на сущность - наносим урон и сбрасываем таймер исчезновения
                data.notLookingTimer = 0;

                // Наносим урон каждую секунду (20 тиков)
                if (player.tickCount % 20 == 0) {
                    player.hurt(player.damageSources().magic(), 1.0F);
                }
            } else {
                // Игрок не смотрит на сущность
                data.notLookingTimer++;

                // Если не смотрит 3 секунды - удаляем сущность
                if (data.notLookingTimer >= DESPAWN_TIME) {
                    data.currentEntity.remove(Entity.RemovalReason.DISCARDED);
                    data.currentEntity = null;
                    data.notLookingTimer = 0;
                    data.darkLookingTimer = 0; // Сбрасываем и таймер спавна
                }
            }
        }
    }

    // Проверяем, является ли позиция валидной для спавна (например, в пещере)
    private static boolean isValidSpawnPosition(Level world, BlockPos pos) {
        // Проверяем, что блок в точке спавна является воздухом (или проходимым)
        // А блок под ногами - твёрдым (это имитирует условие нахождения в пещере или помещении)
        return world.getBlockState(pos).isAir() && !world.getBlockState(pos.below()).isAir();
    }

    private static boolean isPlayerLookingAtEntity(ServerPlayer player, EyesindarkEntity entity) {
        Vec3 playerLookVec = player.getLookAngle();
        Vec3 toEntityVec = entity.position().subtract(player.getEyePosition(1.0F)).normalize();
        double dotProduct = playerLookVec.dot(toEntityVec);
        return dotProduct > VIEW_ANGLE_THRESHOLD;
    }

    private static void spawnEyesEntity(ServerPlayer player, PlayerData data, Vec3 spawnPos) {
        EyesindarkEntity entity = new EyesindarkEntity(EventjarModEntities.EYESINDARK.get(), player.level());
        entity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        player.level().addFreshEntity(entity);
        data.currentEntity = entity;
        data.notLookingTimer = 0;
        // Таймер darkLookingTimer НЕ сбрасываем, чтобы сущность не появилась снова сразу после деспавна
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
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            PlayerData data = playerDataMap.remove(player.getUUID());
            if (data != null && data.currentEntity != null) {
                data.currentEntity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            PlayerData data = playerDataMap.get(player.getUUID());
            if (data != null) {
                resetTimersAndRemoveEntity(data);
            }
        }
    }
}