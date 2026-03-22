package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.entity.Lixis9Entity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.EntityType;

public class GenerateMobLixis9Procedure {

    // Координаты блаженного дерева
    private static final int BLESSED_TREE_X = 100;
    private static final int BLESSED_TREE_Z = 100;

    // Переменная для отслеживания последнего дня, когда произошёл спавн
    private static long lastSpawnDay = -1;

    /**
     * Метод для спавна сущности Lixis9 на вершине блаженного дерева.
     * Срабатывает только ночью и один раз за ночь.
     *
     * @param level  Текущий уровень (мир)
     */
    public static void spawn(Level level) {
        // Проверяем, что код выполняется на сервере
        if (level.isClientSide()) {
            return;
        }

        // Получаем текущее время в тиках
        long time = level.getDayTime() % 24000;

        // Проверяем, что сейчас ночь (13000 - 23000 тиков)
        if (time < 13000 || time > 23000) {
            return;
        }

        // Вычисляем текущий день
        long currentDay = level.getDayTime() / 24000;

        // Проверяем, был ли уже спавн в этот день
        if (lastSpawnDay == currentDay) {
            return;
        }

        // Получаем серверный уровень
        ServerLevel serverLevel = (ServerLevel) level;

        // Определяем высоту блаженного дерева
        int treeY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BLESSED_TREE_X, BLESSED_TREE_Z);
        BlockPos blessedTreePos = new BlockPos(BLESSED_TREE_X, treeY, BLESSED_TREE_Z);

        // Проверяем, загружен ли чанк с блаженным деревом
        if (!serverLevel.isLoaded(blessedTreePos)) {
            return;
        }

        // Позиция для спавна сущности (на 1 блок выше блаженного дерева)
        BlockPos spawnPos = blessedTreePos.above();

        // Создаём и спавним сущность Lixis9
        Lixis9Entity lixis9 = new Lixis9Entity(EventjarModEntities.LIXIS_9.get(), serverLevel);
        lixis9.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        serverLevel.addFreshEntity(lixis9);

        // Обновляем последний день спавна
        lastSpawnDay = currentDay;
    }
}
