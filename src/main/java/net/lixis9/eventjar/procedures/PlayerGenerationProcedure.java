package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.entity.PlayerEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class PlayerGenerationProcedure {

    public static void execute(Level world) {

        if (world.isClientSide()) {
            return;
        }

        long time = world.getDayTime() % 24000;

        if (time >= 13000 && time < 23000) {

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
