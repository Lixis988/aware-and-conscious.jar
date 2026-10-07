package net.lixis9.eventjar.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

import java.util.Random;

public class ChunkDellProcedure {

    public static void execute(LevelAccessor world) {

        if (!(world instanceof ServerLevel serverWorld)) {
            return;
        }

        Random random = new Random();

        if (serverWorld.players().isEmpty()) {
            return;
        }

        ServerPlayer player = serverWorld.players().get(random.nextInt(serverWorld.players().size()));

        ChunkPos playerChunkPos = player.chunkPosition();

        int offsetX = random.nextInt(11) - 5;
        int offsetZ = random.nextInt(11) - 5;

        int targetChunkX = playerChunkPos.x + offsetX;
        int targetChunkZ = playerChunkPos.z + offsetZ;
        ChunkPos targetChunkPos = new ChunkPos(targetChunkX, targetChunkZ);

        ChunkAccess chunk = serverWorld.getChunk(targetChunkX, targetChunkZ, ChunkStatus.FULL, false);
        if (chunk == null) {
            return;
        }

        int minY = serverWorld.getMinBuildHeight();
        int maxY = serverWorld.getMaxBuildHeight();
        int startX = targetChunkX * 16;
        int startZ = targetChunkZ * 16;
        int endX = startX + 16;
        int endZ = startZ + 16;

        for (int bx = startX; bx < endX; bx++) {
            for (int bz = startZ; bz < endZ; bz++) {
                for (int by = minY; by < maxY; by++) {
                    BlockPos pos = new BlockPos(bx, by, bz);
                    serverWorld.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }
}
