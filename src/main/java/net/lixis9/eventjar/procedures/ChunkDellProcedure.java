package net.lixis9.eventjar.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ChunkPos; // Исправленный импорт
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

import java.util.Random;

public class ChunkDellProcedure {
    /**
     * Удаляет содержимое случайного загруженного чанка, относительно случайного игрока.
     * Заменяет все блоки в выбранном чанке на воздух.
     *
     * @param world Серверный мир (ServerLevel), в котором производится операция.
     */
    public static void execute(LevelAccessor world) {
        // Работает только на серверной стороне.
        if (!(world instanceof ServerLevel serverWorld)) {
            return;
        }
        
        Random random = new Random();
        
        // Если на сервере нет игроков, завершаем выполнение процедуры.
        if (serverWorld.players().isEmpty()) {
            return;
        }
        
        // Выбираем случайного игрока
        ServerPlayer player = serverWorld.players().get(random.nextInt(serverWorld.players().size()));
        
        // Получаем координаты чанка, в котором находится игрок.
        ChunkPos playerChunkPos = player.chunkPosition();
        
        // Определяем случайное смещение (от -5 до +5 чанков) от позиции игрока.
        int offsetX = random.nextInt(11) - 5;  // Диапазон [-5; 5]
        int offsetZ = random.nextInt(11) - 5;  // Диапазон [-5; 5]
        
        int targetChunkX = playerChunkPos.x + offsetX;
        int targetChunkZ = playerChunkPos.z + offsetZ;
        ChunkPos targetChunkPos = new ChunkPos(targetChunkX, targetChunkZ);
        
        // Проверяем, что целевой чанк загружен.
        ChunkAccess chunk = serverWorld.getChunk(targetChunkX, targetChunkZ, ChunkStatus.FULL, false);
        if (chunk == null) {
            return;
        }
        
        // Определяем границы чанка: по горизонтали всегда 16 блоков, по вертикали от минимальной до максимальной высоты мира.
        int minY = serverWorld.getMinBuildHeight();
        int maxY = serverWorld.getMaxBuildHeight();
        int startX = targetChunkX * 16;
        int startZ = targetChunkZ * 16;
        int endX = startX + 16;
        int endZ = startZ + 16;
        
        // Проходим по всем блокам в пределах чанка и заменяем их на воздух.
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
