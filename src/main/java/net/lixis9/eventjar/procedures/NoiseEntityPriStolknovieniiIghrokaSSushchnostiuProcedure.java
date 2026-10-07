package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.lixis9.eventjar.init.EventjarModBlocks;

import java.util.Map;

public class NoiseEntityPriStolknovieniiIghrokaSSushchnostiuProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {

        int viewDistanceChunks = 5;
        int playerChunkX = (int) Math.floor(x) >> 4;
        int playerChunkZ = (int) Math.floor(z) >> 4;

        int minChunkX = playerChunkX - viewDistanceChunks;
        int maxChunkX = playerChunkX + viewDistanceChunks;
        int minChunkZ = playerChunkZ - viewDistanceChunks;
        int maxChunkZ = playerChunkZ + viewDistanceChunks;

        int minY = world.getMinBuildHeight();
        int maxY = world.getMaxBuildHeight();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {

                int startX = chunkX * 16;
                int startZ = chunkZ * 16;
                int endX = startX + 16;
                int endZ = startZ + 16;

                for (int bx = startX; bx < endX; bx++) {
                    for (int bz = startZ; bz < endZ; bz++) {
                        for (int by = minY; by <= maxY; by++) {
                            BlockPos pos = BlockPos.containing(bx, by, bz);

                            BlockState oldState = world.getBlockState(pos);

                            if (oldState.isAir()) {
                                continue;
                            }

                            BlockState newState = EventjarModBlocks.NOISEBLOCK.get().defaultBlockState();

                            for (Map.Entry<Property<?>, Comparable<?>> entry : oldState.getValues().entrySet()) {
                                Property<?> oldProp = entry.getKey();
                                Property<?> newProp = newState.getBlock().getStateDefinition().getProperty(oldProp.getName());
                                if (newProp != null && newState.getValue(newProp) != null) {
                                    try {
                                        newState = setValue(newState, newProp, entry.getValue());
                                    } catch (Exception e) {

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<?> prop, Comparable<?> value) {
        return state.setValue((Property<T>) prop, (T) value);
    }
}
