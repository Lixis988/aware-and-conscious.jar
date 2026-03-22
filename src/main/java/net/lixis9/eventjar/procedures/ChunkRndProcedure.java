package net.lixis9.eventjar.procedures;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level; // Импортируем Level, т.к. getCommandSenderWorld() возвращает именно Level

import net.lixis9.eventjar.network.EventjarModVariables;

@Mod.EventBusSubscriber
public class ChunkRndProcedure {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event, event.player);
        }
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null) return;

        // Получаем мир через публичный метод
        Level world = entity.getCommandSenderWorld(); // getCommandSenderWorld() возвращает Level :contentReference[oaicite:2]{index=2}

        // Проверяем репутацию и шанс (1 из 100 000)
        if (entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                  .orElse(new EventjarModVariables.PlayerVariables()).reputation > 60
            && Math.random() < 1.0 / 100_000.0) {
            // Вызываем вашу процедуру удаления чанков
            ChunkDellProcedure.execute(world);
        }
    }
}
