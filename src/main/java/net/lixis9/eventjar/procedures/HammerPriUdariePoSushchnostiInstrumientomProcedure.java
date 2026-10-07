package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis9.eventjar.network.EventjarModVariables;
import net.lixis9.eventjar.init.EventjarModParticleTypes;

public class HammerPriUdariePoSushchnostiInstrumientomProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null || entity.level().isClientSide()) {
			return;
		}

		EventjarModVariables.WorldVariables vars = EventjarModVariables.WorldVariables.get(world);
		vars.murder = vars.murder + 1;
		vars.syncData(world);

		if (entity instanceof ServerPlayer victim) {
			killPlayerCleanly(victim);
		} else if (entity instanceof Player player) {

			player.hurt(player.damageSources().genericKill(), Float.MAX_VALUE);
			if (player.isAlive()) {
				player.kill();
			}
		} else {
			entity.discard();
		}

		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:hammer")),
					SoundSource.NEUTRAL, 1, 1);
		}
		if (world instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles((SimpleParticleType) EventjarModParticleTypes.MEET_1.get(), x, y, z, 5, 1, 1, 1, 1);
			serverLevel.sendParticles((SimpleParticleType) EventjarModParticleTypes.MEET_2.get(), x, y, z, 10, 1, 1, 1, 1);
			serverLevel.sendParticles((SimpleParticleType) EventjarModParticleTypes.MEET_3.get(), x, y, z, 5, 1, 1, 1, 1);
			serverLevel.sendParticles((SimpleParticleType) EventjarModParticleTypes.MEET_4.get(), x, y, z, 5, 1, 1, 1, 1);
			serverLevel.sendParticles((SimpleParticleType) EventjarModParticleTypes.MEET_5.get(), x, y, z, 5, 1, 1, 1, 1);
			serverLevel.sendParticles((SimpleParticleType) EventjarModParticleTypes.MEET_6.get(), x, y, z, 5, 1, 1, 1, 1);
			serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, x, y, z, 2));

			if (vars.murder >= 50) {
				BlockPos spawnPos = BlockPos.containing(x + 2, y + 1, z + 2);

				Entity entityToSpawn = OutofboundExtraEntities.SERAPH_WRATH.get().spawn(serverLevel, spawnPos, MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn == null) {
					entityToSpawn = net.lixis9.eventjar.init.EventjarModEntities.GOD.get().spawn(serverLevel, spawnPos, MobSpawnType.MOB_SUMMONED);
				}
				if (entityToSpawn != null) {
					entityToSpawn.setDeltaMovement(0, 0, 0);
				}
				vars.murder = 0;
				vars.syncData(world);
			}
		}
	}

	private static void killPlayerCleanly(ServerPlayer victim) {
		victim.setInvulnerable(false);
		if (victim.isCreative() || victim.isSpectator()) {
			return;
		}

		victim.setAbsorptionAmount(0.0F);
		victim.removeAllEffects();
		victim.hurt(victim.damageSources().genericKill(), Float.MAX_VALUE);
		if (victim.isAlive()) {
			victim.kill();
		}
	}
}
