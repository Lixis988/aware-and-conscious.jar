package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.network.SeekerCatchShakePacket;
import net.lixis9.eventjar.EventjarMod;

public class SeekerPriNachalnomPrizyvieSushchnostiProcedure {

	private static final int HUNT_DELAY_TICKS = 1200;

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return;
		}

		SeekerTitleHelper.warnAppeared(world);

		broadcastTitle(world, "hide or run");

		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:glitch1")),
						SoundSource.HOSTILE, 1, 1);
			} else {
				_level.playLocalSound(x, y, z,
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:glitch1")),
						SoundSource.HOSTILE, 1, 1, false);
			}
		}

		TimerEventProcedure.execute(world, x, y, z);

		EventjarMod.queueServerWork(HUNT_DELAY_TICKS, () -> {
			if (!entity.level().isClientSide()) {
				entity.discard();
			}

			SeekerTitleHelper.warnHuntStarts(world);

			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = EventjarModEntities.SEEKERACT.get().spawn(
						_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
				SeekerCatchShakePacket.sendToAll(_level, true);
			}

			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z),
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chaiseseeker")),
							SoundSource.HOSTILE, 1, 1);
				} else {
					_level.playLocalSound(x, y, z,
							ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chaiseseeker")),
							SoundSource.HOSTILE, 1, 1, false);
				}
			}
		});
	}

	private static void broadcastTitle(LevelAccessor world, String title) {
		if (!(world instanceof ServerLevel level)) {
			return;
		}
		for (var player : level.players()) {
			player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(10, 60, 10));
			player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(
					net.minecraft.network.chat.Component.literal(title)));
		}
	}
}
