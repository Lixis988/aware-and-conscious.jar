package net.lixis9.eventjar;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

import net.lixis9.eventjar.init.EventjarModMobEffects;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;
import java.util.Random;

@Mod.EventBusSubscriber
public class Overdosestart {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event, event.player);
        }
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable TickEvent.PlayerTickEvent event, Entity entity) {
        if (entity == null)
            return;
            
        if (entity instanceof ServerPlayer serverPlayer) {
            // Получаем переменные игрока
            var playerVars = serverPlayer.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables());
            
            // Проверяем, что patience <= -10000 (критическое состояние)
            if (playerVars.patience <= -10000) {
                
                // Спавним случайные сущности рядом с игроком
                spawnRandomEntities(serverPlayer);
                
                // Удаляем случайные сущности в радиусе
                removeRandomEntities(serverPlayer);
                
                // Накладываем эффект передозировки
                if (!serverPlayer.hasEffect(EventjarModMobEffects.OVERDOSE.get())) {
                    serverPlayer.addEffect(new MobEffectInstance(
                        EventjarModMobEffects.OVERDOSE.get(),
                        600, // 30 секунд
                        1,   // Уровень 1
                        false, true
                    ));
                }
                
                // Сбрасываем patience в 0, чтобы избежать повторного срабатывания
                playerVars.patience = 0;
                playerVars.syncPlayerVariables(serverPlayer);
            }
        }
    }

    /**
     * Спавнит случайные сущности рядом с игроком
     */
    private static void spawnRandomEntities(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos playerPos = player.blockPosition();

        // Спавним 3-5 случайных сущностей
        int entityCount = RANDOM.nextInt(3) + 3;
        for (int i = 0; i < entityCount; i++) {
            // Случайная позиция в радиусе 10 блоков
            int offsetX = RANDOM.nextInt(21) - 10;
            int offsetZ = RANDOM.nextInt(21) - 10;
            BlockPos spawnPos = playerPos.offset(offsetX, 0, offsetZ);

            // Находим безопасную высоту для спавна
            int spawnY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ());
            BlockPos finalSpawnPos = new BlockPos(spawnPos.getX(), spawnY, spawnPos.getZ());

            try {
                // Выбираем случайную сущность для спавна
                int entityChoice = RANDOM.nextInt(6);
                switch (entityChoice) {
                    case 0:
                        EventjarModEntities.MEETBOY.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                        break;
                    case 1:
                        EventjarModEntities.WHOAMI.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                        break;
                    case 2:
                        EventjarModEntities.EYES.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                        break;
                    case 3:
                        EventjarModEntities.EYE.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                        break;
                    case 4:
                        EventjarModEntities.WATCHER.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                        break;
                    case 5:
                        EventjarModEntities.SEEKER.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                        break;
                }
            } catch (Exception e) {
                // Silent failure
            }
        }
    }

    /**
     * Удаляет случайные сущности в радиусе
     */
    private static void removeRandomEntities(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos playerPos = player.blockPosition();

        // Находим все сущности в радиусе 15 блоков
        var entities = serverLevel.getEntitiesOfClass(net.minecraft.world.entity.Entity.class, 
            new net.minecraft.world.phys.AABB(playerPos).inflate(15));

        // Удаляем случайные сущности (но не игрока)
        int removeCount = Math.min(RANDOM.nextInt(3) + 1, entities.size());
        for (int i = 0; i < removeCount && i < entities.size(); i++) {
            var entity = entities.get(RANDOM.nextInt(entities.size()));
            if (entity != player && !entity.isRemoved()) {
                entity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        }
    }
}
