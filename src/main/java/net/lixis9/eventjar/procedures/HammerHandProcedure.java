package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.init.EventjarModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class HammerHandProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null) {
			return;
		}
		if (!(entity instanceof LivingEntity living)) {
			return;
		}

		boolean alreadyHolding = living.isHolding(EventjarModItems.HAMMER.get());
		if (!alreadyHolding) {
			ItemStack hammer = new ItemStack(EventjarModItems.HAMMER.get());
			hammer.setCount(1);
			living.setItemInHand(InteractionHand.OFF_HAND, hammer);
			if (living instanceof Player player) {
				player.getInventory().setChanged();
			}
		}

		if (!world.isClientSide() && world.getServer() != null) {
			world.getServer().getPlayerList().broadcastSystemMessage(
					Component.literal(alreadyHolding
							? "you could have just closed it and not made your choice"
							: "you closed it. the hammer is yours anyway"),
					false);
		}
	}
}
