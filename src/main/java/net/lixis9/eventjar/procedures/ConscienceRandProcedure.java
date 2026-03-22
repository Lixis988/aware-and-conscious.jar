package net.lixis9.eventjar.procedures;

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
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		// Увеличиваем вероятность срабатывания с 1/150000 до 1/10000 (в 15 раз чаще)
		// Это означает примерно 1 раз в 8 минут игрового времени
		if (Math.random() < (1) / ((float) 10000)) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
				// Проверяем, что у игрока еще нет эффекта conscience
				if (!_entity.hasEffect(EventjarModMobEffects.CONSCIENCE.get())) {
					_entity.addEffect(new MobEffectInstance(EventjarModMobEffects.CONSCIENCE.get(), 6000, 1, false, false));
				}
			}
		}
	}
}
