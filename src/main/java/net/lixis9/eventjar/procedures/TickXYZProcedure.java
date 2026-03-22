package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.lixis9.eventjar.network.EventjarModVariables;

@Mod.EventBusSubscriber
public class TickXYZProcedure {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Фиксируем только на сервере и в конце тика игрока 
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            LevelAccessor world = event.player.level();
            Vec3 look = event.player.getLookAngle(); // направление взгляда игрока 
            // Записываем и синхронизируем разом
            EventjarModVariables.WorldVariables vars = EventjarModVariables.WorldVariables.get(world);
            vars.X = look.x;
            vars.Y = look.y;
            vars.Z = look.z;
            vars.syncData(world);
        }
    }
}
