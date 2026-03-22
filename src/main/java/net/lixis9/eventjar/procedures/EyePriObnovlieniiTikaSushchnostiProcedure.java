package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.nbt.CompoundTag;

public class EyePriObnovlieniiTikaSushchnostiProcedure {
    public static void execute(Entity entity) {
        if (entity == null)
            return;

        // Получаем NBT-данные сущности, чтобы хранить собственный счётчик тиков
        CompoundTag data = entity.getPersistentData();

        // Извлекаем текущее значение счётчика и увеличиваем на единицу
        int tickCounter = data.getInt("tickCounter");
        tickCounter++;
        data.putInt("tickCounter", tickCounter);

        // Выполняем проверку раз в 20 тиков
        if (tickCounter >= 20) {
            data.putInt("tickCounter", 0); // сбрасываем счётчик

            // Если сущность находится в лодке → удаляем её
            if (entity.getVehicle() instanceof Boat) {
                entity.discard();
            }
        }
    }
}
