package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.lixis9.eventjar.init.EventjarModItems;

public class HammerHandProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _entity) ? _entity.isHolding(EventjarModItems.HAMMER.get()) : false) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("you could have just closed it and not made your choice"), false);
		}
	}
}
