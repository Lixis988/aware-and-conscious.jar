package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.EventjarMod;

public class CantBreathTestProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		entity.setAirSupply(10);
		EventjarMod.queueServerWork(20, () -> {
			entity.setAirSupply(9);
			EventjarMod.queueServerWork(20, () -> {
				entity.setAirSupply(8);
				EventjarMod.queueServerWork(20, () -> {
					entity.setAirSupply(7);
					EventjarMod.queueServerWork(20, () -> {
						entity.setAirSupply(6);
						EventjarMod.queueServerWork(20, () -> {
							entity.setAirSupply(5);
							EventjarMod.queueServerWork(20, () -> {
								entity.setAirSupply(4);
								EventjarMod.queueServerWork(20, () -> {
									entity.setAirSupply(3);
									EventjarMod.queueServerWork(20, () -> {
										entity.setAirSupply(2);
										EventjarMod.queueServerWork(20, () -> {
											entity.setAirSupply(1);
											EventjarMod.queueServerWork(20, () -> {
												entity.setAirSupply(1);
												EventjarMod.queueServerWork(20, () -> {
													entity.setAirSupply(10);
												});
											});
										});
									});
								});
							});
						});
					});
				});
			});
		});
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:breath")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:breath")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}
		TimerBreathEventProcedure.execute(world, x, y, z);
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
	}
}
