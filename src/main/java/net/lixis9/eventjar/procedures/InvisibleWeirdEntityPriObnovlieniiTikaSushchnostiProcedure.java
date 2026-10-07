package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.init.EventjarModBlocks;

public class InvisibleWeirdEntityPriObnovlieniiTikaSushchnostiProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		BlockPos bp = BlockPos.containing(x, y, z);
		BlockState current = world.getBlockState(bp);
		BlockState god = EventjarModBlocks.GODISLOVEYOUBLOCK.get().defaultBlockState();

		if (current.is(EventjarModBlocks.GODISLOVEYOUBLOCK.get())) {
			return;
		}
		if (!current.isAir() && !current.canBeReplaced()) {
			return;
		}
		world.setBlock(bp, god, 3);
	}
}
