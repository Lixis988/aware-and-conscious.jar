package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class StopFollowTaskProcedure {
    public static void execute(Entity entity) {
        // Проверяем, что переданная сущность — игрок
        if (entity instanceof Player) {
            // Вызываем метод остановки задачи слежения
            MobFollowTaskProcedure.stopFollowTask();
        }
    }
}
