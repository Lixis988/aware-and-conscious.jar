package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.entity.PlayerEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class PlayerGenerationProcedure {
    /**
     * Данная процедура должна вызываться на серверной стороне каждую ночь.
     * Для каждого игрока, если случайное число меньше заданного порога, рядом со
     * игроком за спаунится новая сущность.
     *
     * @param world Игровой мир, в котором производится проверка времени суток и спаун сущности.
     */
    public static void execute(Level world) {
        // Выполняем только на сервере
        if (world.isClientSide()) {
            return;
        }
        
        // Определяем текущее время суток (в пределах суток 24000 тиков)
        long time = world.getDayTime() % 24000;
        // Если время в ночной период (например, с 13000 до 23000 тиков)
        if (time >= 13000 && time < 23000) {
            // Для каждого игрока в мире с вероятностью 10% спауним сущность рядом с ним
            for (Player player : world.players()) {
                if (world.getRandom().nextFloat() < 0.1F) {
                    double offsetX = (world.getRandom().nextDouble() - 0.5D) * 16;
                    double offsetZ = (world.getRandom().nextDouble() - 0.5D) * 16;
                    double spawnX = player.getX() + offsetX;
                    double spawnY = player.getY();
                    double spawnZ = player.getZ() + offsetZ;
                    
                    PlayerEntity entity = new PlayerEntity(EventjarModEntities.PLAYER.get(), world);
                    entity.setPos(spawnX, spawnY, spawnZ);
                    world.addFreshEntity(entity);
                }
            }
        }
    }
}
