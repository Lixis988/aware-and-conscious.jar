package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class YouaresafeProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != event.getEntity().getUsedItemHand())
			return;
		execute(event, event.getLevel(), event.getLevel().getBlockState(event.getPos()));
	}

	public static void execute(LevelAccessor world, BlockState blockstate) {
		execute(null, world, blockstate);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, BlockState blockstate) {
		if ((blockstate.getBlock() == Blocks.OAK_DOOR || blockstate.getBlock() == Blocks.SPRUCE_DOOR || blockstate.getBlock() == Blocks.BIRCH_DOOR || blockstate.getBlock() == Blocks.JUNGLE_DOOR || blockstate.getBlock() == Blocks.ACACIA_DOOR
				|| blockstate.getBlock() == Blocks.DARK_OAK_DOOR || blockstate.getBlock() == Blocks.CRIMSON_DOOR || blockstate.getBlock() == Blocks.WARPED_DOOR || blockstate.getBlock() == Blocks.MANGROVE_DOOR
				|| blockstate.getBlock() == Blocks.CHERRY_DOOR || blockstate.getBlock() == Blocks.BAMBOO_DOOR || blockstate.getBlock() == Blocks.IRON_DOOR) && Math.random() < (1) / ((float) 200)) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Do you think you are safe?"), false);
		}
	}
}
