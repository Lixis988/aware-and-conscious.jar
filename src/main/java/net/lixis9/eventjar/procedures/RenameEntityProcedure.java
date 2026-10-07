package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RenameEntityProcedure {
	private static final String LABEL = "sacrifice";
	private static final float CHANCE = 0.20F;

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		execute(event, event.getEntity());
	}

	@SubscribeEvent
	public static void onEntityJump(LivingEvent.LivingJumpEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null || entity.level().isClientSide()) {
			return;
		}
		if (!(entity instanceof Animal)) {
			return;
		}
		if (entity.getCustomName() != null) {
			return;
		}
		if (entity.level().getRandom().nextFloat() >= CHANCE) {
			return;
		}
		entity.setCustomName(Component.literal(LABEL));
		entity.setCustomNameVisible(true);
	}
}
