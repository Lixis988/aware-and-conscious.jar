package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.nbt.CompoundTag;

public class EyePriObnovlieniiTikaSushchnostiProcedure {
    public static void execute(Entity entity) {
        if (entity == null)
            return;

        CompoundTag data = entity.getPersistentData();

        int tickCounter = data.getInt("tickCounter");
        tickCounter++;
        data.putInt("tickCounter", tickCounter);

        if (tickCounter >= 20) {
            data.putInt("tickCounter", 0);

            if (entity.getVehicle() instanceof Boat) {
                entity.discard();
            }
        }
    }
}
