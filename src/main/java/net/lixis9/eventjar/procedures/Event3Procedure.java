package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.entity.Entity;
import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Event3Procedure {
    // Таймер для отсчёта 1 секунды (20 тиков)
    private static int activeTimer = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Если таймер активен, уменьшаем его
            if (activeTimer > 0) {
                activeTimer--;
            }
            // Вызываем процедуру для игрока
            boolean result = execute(event, event.player);
            // Здесь можно использовать result, если требуется
        }
    }

    public static boolean execute(Entity entity) {
        return execute(null, entity);
    }

    private static boolean execute(@Nullable net.minecraftforge.eventbus.api.Event event, Entity entity) {
        if (entity == null) {
            return false;
        }
        // Если событие уже сработало и таймер ещё активен, всегда возвращаем false
        if (activeTimer > 0) {
            return false;
        }
        // Если значение patience равно 1000 и случайное срабатывание (вероятность 1/50) выполнено,
        // то возвращаем true в этот тик и запускаем таймер на 1 секунду (20 тиков)
        if ((entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new EventjarModVariables.PlayerVariables())).patience == 1000) {
            if (Math.random() < 1 / 50f) {
                activeTimer = 20; // устанавливаем таймер на 20 тиков (1 секунда)
                return true;
            }
        }
        return false;
    }
}
