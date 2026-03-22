package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class RageThen60sPriObnovlieniiTikaSushchnostiProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		world.setBlock(BlockPos.containing(1 + x, y, z), Blocks.AIR.defaultBlockState(), 3);
		world.setBlock(BlockPos.containing(x, 1 + y, z), Blocks.AIR.defaultBlockState(), 3);
		world.setBlock(BlockPos.containing(x, y, 1 + z), Blocks.AIR.defaultBlockState(), 3);
	}
}
