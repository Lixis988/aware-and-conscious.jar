package net.lixis9.eventjar.procedures;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import java.util.List;

public class MobFollowTaskProcedure {

    private static final long DURATION_TICKS = 5 * 60 * 20;

    private static TickHandler tickHandler;

    public static void startFollowTask(Player player) {

        stopFollowTask();

        if (!(player.getCommandSenderWorld() instanceof ServerLevel serverWorld)) {
            return;
        }

        final long startTime = serverWorld.getGameTime();

        tickHandler = new TickHandler(serverWorld, player, startTime);
        MinecraftForge.EVENT_BUS.register(tickHandler);
    }

    public static void stopFollowTask() {
        if (tickHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(tickHandler);
            tickHandler = null;
        }
    }

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

            if (event.phase != TickEvent.Phase.END) return;

            if (!player.isAlive() || player.isRemoved()) {
                stopFollowTask();
                return;
            }

            long gameTime = serverWorld.getGameTime();

            if (gameTime - startTime >= DURATION_TICKS) {
                stopFollowTask();
                return;
            }

            List<Mob> mobs = serverWorld.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(50));
            mobs.forEach(mob -> {

                if (mob.isAlive() && !mob.isRemoved()) {

                    mob.getLookControl().setLookAt(player, 30.0F, 30.0F);

                    mob.getNavigation().moveTo(player, 1.0D);
                }
            });
        }
    }
}
