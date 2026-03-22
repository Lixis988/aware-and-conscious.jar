package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.network.EventjarModVariables;
import net.lixis9.eventjar.init.EventjarModParticleTypes;
import net.lixis9.eventjar.init.EventjarModEntities;

public class HammerPriUdariePoSushchnostiInstrumientomProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		EventjarModVariables.WorldVariables.get(world).murder = EventjarModVariables.WorldVariables.get(world).murder + 1;
		EventjarModVariables.WorldVariables.get(world).syncData(world);
		// Удаляем сущность только на серверной стороне
		if (!entity.level().isClientSide())
			entity.discard();
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:hammer")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:hammer")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (EventjarModParticleTypes.MEET_1.get()), x, y, z, 5, 1, 1, 1, 1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (EventjarModParticleTypes.MEET_2.get()), x, y, z, 10, 1, 1, 1, 1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (EventjarModParticleTypes.MEET_3.get()), x, y, z, 5, 1, 1, 1, 1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (EventjarModParticleTypes.MEET_4.get()), x, y, z, 5, 1, 1, 1, 1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (EventjarModParticleTypes.MEET_5.get()), x, y, z, 5, 1, 1, 1, 1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (EventjarModParticleTypes.MEET_6.get()), x, y, z, 5, 1, 1, 1, 1);
		if (world instanceof ServerLevel _level)
			_level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 2));
		if (EventjarModVariables.WorldVariables.get(world).murder >= 50) {
			if (world instanceof ServerLevel _level) {
				// Спавним GodEntity на безопасной высоте рядом с игроком
				BlockPos spawnPos = BlockPos.containing(x + 2, y + 1, z + 2);
				Entity entityToSpawn = EventjarModEntities.GOD.get().spawn(_level, spawnPos, MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setDeltaMovement(0, 0, 0);
				}
				// Сбрасываем счетчик убийств после спавна
				EventjarModVariables.WorldVariables.get(world).murder = 0;
				EventjarModVariables.WorldVariables.get(world).syncData(world);
			}
		}
	}
}
