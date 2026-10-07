package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.entity.Lixis9Entity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.EntityType;

public class GenerateMobLixis9Procedure {

    private static final int BLESSED_TREE_X = 100;
    private static final int BLESSED_TREE_Z = 100;

    private static long lastSpawnDay = -1;

    public static void spawn(Level level) {

        if (level.isClientSide()) {
            return;
        }

        long time = level.getDayTime() % 24000;

        if (time < 13000 || time > 23000) {
            return;
        }

        long currentDay = level.getDayTime() / 24000;

        if (lastSpawnDay == currentDay) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        int treeY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BLESSED_TREE_X, BLESSED_TREE_Z);
        BlockPos blessedTreePos = new BlockPos(BLESSED_TREE_X, treeY, BLESSED_TREE_Z);

        if (!serverLevel.isLoaded(blessedTreePos)) {
            return;
        }

        BlockPos spawnPos = blessedTreePos.above();

        Lixis9Entity lixis9 = new Lixis9Entity(EventjarModEntities.LIXIS_9.get(), serverLevel);
        lixis9.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        serverLevel.addFreshEntity(lixis9);

        lastSpawnDay = currentDay;
    }
}
