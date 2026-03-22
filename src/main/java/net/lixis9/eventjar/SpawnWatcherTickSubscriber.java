// SpawnWatcherTickSubscriber.java
package net.lixis9.eventjar.procedures;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

@Mod.EventBusSubscriber(modid = "eventjar")
public class SpawnWatcherTickSubscriber {
    private static final long TICKS_BETWEEN_SPAWNS = 20L * 60L; // ≈ 1200 тиков (1 минута)

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player.level() instanceof ServerLevel serverWorld)) return;

        Player player = event.player;
        long currentTick = serverWorld.getGameTime();
        CompoundTag data = player.getPersistentData();
        long lastSpawnTick = data.getLong("lastWatcherSpawnTick");

        if (currentTick - lastSpawnTick < TICKS_BETWEEN_SPAWNS) {
            return;
        }

        data.putLong("lastWatcherSpawnTick", currentTick);
        SpawnWatcherProcedure.execute(serverWorld, player);
    }
}
