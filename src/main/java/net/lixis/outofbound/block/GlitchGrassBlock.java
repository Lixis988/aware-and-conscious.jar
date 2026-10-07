package net.lixis.outofbound.block;

import net.lixis.outofbound.world.GlitchGrassTeleportHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GlitchGrassBlock extends GrassBlock {

	public GlitchGrassBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(level, pos, state, entity);
		if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
			return;
		}
		GlitchGrassTeleportHandler.tryTeleport(player);
	}
}
