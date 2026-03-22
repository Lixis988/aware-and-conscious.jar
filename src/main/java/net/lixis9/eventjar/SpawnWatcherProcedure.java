// SpawnWatcherProcedure.java
package net.lixis9.eventjar.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.entity.WatcherEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SpawnWatcherProcedure {
    public static void execute(LevelAccessor world, Player player) {
        if (player == null || !(world instanceof ServerLevel serverWorld)) return;

        final double SEARCH_RADIUS = 50.0;
        AABB checkBox = player.getBoundingBox().inflate(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS);

        List<WatcherEntity> existing = serverWorld.getEntitiesOfClass(
            WatcherEntity.class,
            checkBox,
            e -> true
        );

        // Проверяем, есть ли среди найденных хотя бы одна сущность, находящаяся в загруженном чанке
        for (WatcherEntity e : existing) {
            BlockPos pos = e.blockPosition();
            if (serverWorld.isLoaded(pos)) {
                return; // если нашли загруженную WatcherEntity рядом, спавнить не нужно
            }
        }

        // Если ни одной загруженной сущности нет — спавним новую
        double dist = 20.0 + serverWorld.random.nextDouble() * 60.0;
        double angle = serverWorld.random.nextDouble() * Math.PI * 2.0;
        double rawX = player.getX() + Math.cos(angle) * dist;
        double rawZ = player.getZ() + Math.sin(angle) * dist;

        int topY = serverWorld.getHeight(
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Mth.floor(rawX),
            Mth.floor(rawZ)
        );

        int minY = serverWorld.getMinBuildHeight();
        int maxY = serverWorld.getMaxBuildHeight();
        if (topY < minY) topY = minY;
        if (topY > maxY) topY = maxY;

        BlockPos spawnPos = new BlockPos(Mth.floor(rawX), topY, Mth.floor(rawZ));
        EntityType<?> type = EventjarModEntities.WATCHER.get();
        if (type == null) return;

        // Spawning Watcher silently
        type.spawn(
            serverWorld,
            ItemStack.EMPTY,
            player,
            spawnPos,
            MobSpawnType.EVENT,
            true,
            false
        );
    }
}
