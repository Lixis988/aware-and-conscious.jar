package net.lixis9.eventjar.procedures;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import java.util.List;

public class MobFollowTaskProcedure {

    // Длительность задачи: 5 минут (5 * 60 секунд * 20 тиков)
    private static final long DURATION_TICKS = 5 * 60 * 20;
    // Сохраняем ссылку на зарегистрированный обработчик событий
    private static TickHandler tickHandler;

    public static void startFollowTask(Player player) {
        // Останавливаем предыдущую задачу, если она есть
        stopFollowTask();
        
        // Получаем ссылку на серверный мир игрока
        if (!(player.getCommandSenderWorld() instanceof ServerLevel serverWorld)) {
            return;
        }
        
        // Запоминаем время старта
        final long startTime = serverWorld.getGameTime();
        
        // Создаем и регистрируем обработчик тиков
        tickHandler = new TickHandler(serverWorld, player, startTime);
        MinecraftForge.EVENT_BUS.register(tickHandler);
    }

    public static void stopFollowTask() {
        if (tickHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(tickHandler);
            tickHandler = null;
        }
    }

    // Приватный статический класс-обработчик тиков
    private static class TickHandler {
        private final ServerLevel serverWorld;
        private final Player player;
        private final long startTime;

        public TickHandler(ServerLevel serverWorld, Player player, long startTime) {
            this.serverWorld = serverWorld;
            this.player = player;
            this.startTime = startTime;
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            // Обрабатываем только фазу END
            if (event.phase != TickEvent.Phase.END) return;

            // Проверяем, что игрок еще в игре
            if (!player.isAlive() || player.isRemoved()) {
                stopFollowTask();
                return;
            }

            long gameTime = serverWorld.getGameTime();
            // Если истекло время действия задачи, отменяем подписку
            if (gameTime - startTime >= DURATION_TICKS) {
                stopFollowTask();
                return;
            }

            // Находим всех мобов в радиусе 50 блоков от игрока
            List<Mob> mobs = serverWorld.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(50));
            mobs.forEach(mob -> {
                // Проверяем, что моб еще жив и не удален
                if (mob.isAlive() && !mob.isRemoved()) {
                    // Заставляем моба смотреть на игрока
                    mob.getLookControl().setLookAt(player, 30.0F, 30.0F);
                    // Заставляем моба двигаться к игроку (скорость 1.0)
                    mob.getNavigation().moveTo(player, 1.0D);
                }
            });
        }
    }
}
