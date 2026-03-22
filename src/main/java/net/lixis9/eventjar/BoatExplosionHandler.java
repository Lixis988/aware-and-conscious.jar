package net.lixis9.eventjar;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BoatExplosionHandler {

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        // Проверяем, когда сущность садится в транспорт
        if (event.isMounting() && event.getEntityBeingMounted() instanceof Boat) {
            Boat boat = (Boat) event.getEntityBeingMounted();
            Entity passenger = event.getEntityMounting();
            Level level = boat.level();
            
            // Если сущность не игрок и садится в лодку
            if (!(passenger instanceof Player) && !level.isClientSide()) {
                // Небольшая задержка перед взрывом (10 тиков = 0.5 секунды)
                if (level.getServer() != null) {
                    level.getServer().execute(() -> {
                    if (boat.isAlive() && !boat.getPassengers().isEmpty()) {
                        level.explode(
                            boat,
                            boat.getX(), boat.getY(), boat.getZ(),
                            2.5F,
                            false,
                            Level.ExplosionInteraction.TNT
                        );
                        boat.discard();
                    }
                    });
                }
            }
        }
    }
}