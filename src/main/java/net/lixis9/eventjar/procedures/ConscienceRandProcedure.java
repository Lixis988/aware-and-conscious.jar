package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.lixis9.eventjar.init.EventjarModMobEffects;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ConscienceRandProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			if (!TickThrottle.due(event.player))
				return;
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;

		if (!TickThrottle.rollDenomEvent(150_000)) {
			return;
		}
		if (entity instanceof LivingEntity living && !living.level().isClientSide()) {
			if (!living.hasEffect(EventjarModMobEffects.CONSCIENCE.get())) {
				living.addEffect(new MobEffectInstance(EventjarModMobEffects.CONSCIENCE.get(), 6000, 1, false, false));
			}
		}
	}
}
