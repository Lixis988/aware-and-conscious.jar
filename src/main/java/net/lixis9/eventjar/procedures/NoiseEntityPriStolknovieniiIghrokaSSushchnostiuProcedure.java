package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.lixis9.eventjar.init.EventjarModBlocks;

import java.util.Map;

public class NoiseEntityPriStolknovieniiIghrokaSSushchnostiuProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        // Радиус чанков для замены (например, 5 чанков в каждую сторону от чанка, где находится игрок)
        int viewDistanceChunks = 5;
        int playerChunkX = (int) Math.floor(x) >> 4;
        int playerChunkZ = (int) Math.floor(z) >> 4;
        
        int minChunkX = playerChunkX - viewDistanceChunks;
        int maxChunkX = playerChunkX + viewDistanceChunks;
        int minChunkZ = playerChunkZ - viewDistanceChunks;
        int maxChunkZ = playerChunkZ + viewDistanceChunks;
        
        // Используем полный вертикальный диапазон мира для замены блоков
        int minY = world.getMinBuildHeight();
        int maxY = world.getMaxBuildHeight();
        
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                // Горизонтальные границы чанка (16x16 блоков)
                int startX = chunkX * 16;
                int startZ = chunkZ * 16;
                int endX = startX + 16;
                int endZ = startZ + 16;
                
                // Перебор блоков в пределах чанка и полного вертикального диапазона
                for (int bx = startX; bx < endX; bx++) {
                    for (int bz = startZ; bz < endZ; bz++) {
                        for (int by = minY; by <= maxY; by++) {
                            BlockPos pos = BlockPos.containing(bx, by, bz);
                            
                            // Получаем текущее состояние блока
                            BlockState oldState = world.getBlockState(pos);
                            // Пропускаем замену, если блок является воздухом
                            if (oldState.isAir()) {
                                continue;
                            }
                            
                            // Создаём новое состояние для NoiseblockBlock
                            BlockState newState = EventjarModBlocks.NOISEBLOCK.get().defaultBlockState();
                            
                            // Копируем свойства из старого блока в новый, если это возможно
                            for (Map.Entry<Property<?>, Comparable<?>> entry : oldState.getValues().entrySet()) {
                                Property<?> oldProp = entry.getKey();
                                Property<?> newProp = newState.getBlock().getStateDefinition().getProperty(oldProp.getName());
                                if (newProp != null && newState.getValue(newProp) != null) {
                                    try {
                                        newState = setValue(newState, newProp, entry.getValue());
                                    } catch (Exception e) {
                                        // Если приведение типов не сработало для данного свойства – подавляем исключение
                                    }
                                }
                            }
                            
                            world.setBlock(pos, newState, 3);
                        }
                    }
                }
            }
        }
    }

    /**
     * Вспомогательный метод для корректного приведения обобщённых типов при вызове setValue.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<?> prop, Comparable<?> value) {
        return state.setValue((Property<T>) prop, (T) value);
    }
}
