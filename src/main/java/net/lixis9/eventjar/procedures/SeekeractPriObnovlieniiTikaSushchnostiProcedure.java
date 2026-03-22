package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.nbt.CompoundTag;

public class SeekeractPriObnovlieniiTikaSushchnostiProcedure {
    public static void execute(Entity entity) {
        if (entity == null) {
            return;
        }

        // Берём NBT-данные сущности, чтобы хранить внутренний счётчик тиков
        CompoundTag data = entity.getPersistentData();

        // Увеличиваем tickCounter на 1
        int tickCounter = data.getInt("seekeractTickCounter");
        tickCounter++;
        data.putInt("seekeractTickCounter", tickCounter);

        // Если прошло 20 тиков, сбросим счётчик и проверим надет ли в лодку
        if (tickCounter >= 20) {
            data.putInt("seekeractTickCounter", 0);

            // Если сущность едет в лодке — удаляем её
            if (entity.getVehicle() instanceof Boat) {
                entity.discard();
            }
        }
    }
}
