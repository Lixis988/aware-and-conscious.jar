package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.EventjarMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SonOfgodCutsceneProcedure {
	private static final String CUTSCENE_TAG = "sonofgod_cutscene_shown";

	@SubscribeEvent
	public static void onPlayerTick(LivingEvent.LivingTickEvent event) {

		if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {

			Level level = serverPlayer.level();
			ResourceKey<Level> eyesworldDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("eventjar:eyesworld"));
			if (level.dimension() != eyesworldDimension) {
				return;
			}

			if (hasCutsceneBeenShown(serverPlayer)) {
				return;
			}

			if (level instanceof ServerLevel serverLevel) {

				boolean hasSonOfgod = serverLevel.getEntitiesOfClass(
					net.lixis9.eventjar.entity.SonOfgodEntity.class,
					serverPlayer.getBoundingBox().inflate(100.0),
					entity -> true
				).size() > 0;

				if (hasSonOfgod) {
					return;
				}
			}

			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null || !(entity instanceof Player player))
			return;

		markCutsceneAsShown(player);

		float yaw = player.getYRot();
		double spawnX = x + Math.sin(Math.toRadians(-yaw)) * 3.0;
		double spawnY = y;
		double spawnZ = z + Math.cos(Math.toRadians(-yaw)) * 3.0;

		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EventjarModEntities.SON_OFGOD.get().spawn(_level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);

				EventjarMod.queueServerWork(20, () -> {
					startCutscene(world, spawnX, spawnY, spawnZ, entityToSpawn, player);
				});
			}
		}
	}

	private static void startCutscene(LevelAccessor world, double x, double y, double z, Entity entity, Player player) {

		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:sonofgodvoice")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:sonofgodvoice")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}

		EventjarMod.queueServerWork(320, () -> {
			if (!entity.level().isClientSide()) {
				entity.discard();
			}
		});
	}

	private static boolean hasCutsceneBeenShown(Player player) {
		CompoundTag persistentData = player.getPersistentData();
		return persistentData.getBoolean(CUTSCENE_TAG);
	}

	private static void markCutsceneAsShown(Player player) {
		CompoundTag persistentData = player.getPersistentData();
		persistentData.putBoolean(CUTSCENE_TAG, true);
	}

	public static void resetCutsceneFlag(Player player) {
		CompoundTag persistentData = player.getPersistentData();
		persistentData.putBoolean(CUTSCENE_TAG, false);
	}
}
