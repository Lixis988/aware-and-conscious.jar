package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class TorchPlacmentHandlerProcedure {
	@SubscribeEvent
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
	}

	public static void execute(LevelAccessor world, double x, double y, double z) {
		execute(null, world, x, y, z);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z) {
		if (Math.random() < (1) / ((float) 100)) {
			world.setBlock(BlockPos.containing(x + 1, y, z + 50), Blocks.TORCH.defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y, z + 51), Blocks.TORCH.defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y, z + 50), Blocks.TORCH.defaultBlockState(), 3);
			world.setBlock(BlockPos.containing(x, y, z + 52), Blocks.TORCH.defaultBlockState(), 3);
			{
				BlockPos _pos = BlockPos.containing(x, y, z + 50);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _booleanProp)
					world.setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
			}
		}
	}
}
