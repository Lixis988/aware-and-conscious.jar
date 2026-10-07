package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.EntityAnchorArgument;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.network.EventjarModVariables;
import net.lixis9.eventjar.EventjarMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class MeatboydistortedCutsceneProcedure {

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		Level level = player.level();
		if (level.isClientSide() || level.dimension() != Level.OVERWORLD) {
			return;
		}
		if (hasCutsceneBeenShown(level)) {
			return;
		}
		if (level instanceof ServerLevel serverLevel) {
			boolean hasMeatboydistorted = !serverLevel.getEntitiesOfClass(
					net.lixis9.eventjar.entity.MeatboydistortedEntity.class,
					player.getBoundingBox().inflate(100.0),
					entity -> true
			).isEmpty();
			if (hasMeatboydistorted) {
				markCutsceneAsShown(level);
				return;
			}
		}
		execute(null, level, player.getX(), player.getY(), player.getZ(), player);
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null || !(entity instanceof Player player)) {
			return;
		}
		if (hasCutsceneBeenShown(world)) {
			return;
		}
		markCutsceneAsShown(world);

		float yaw = player.getYRot();
		double spawnX = x + Math.sin(Math.toRadians(-yaw)) * 3.0;
		double spawnY = y;
		double spawnZ = z + Math.cos(Math.toRadians(-yaw)) * 3.0;

		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EventjarModEntities.MEATBOYDISTORTED.get().spawn(
					_level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
				EventjarMod.queueServerWork(20, () -> startCutscene(world, spawnX, spawnY, spawnZ, entityToSpawn, player));
			}
		}
	}

	private static void startCutscene(LevelAccessor world, double x, double y, double z, Entity entity, Player player) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:asyoubreathe")),
						SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z,
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:asyoubreathe")),
						SoundSource.NEUTRAL, 1, 1, false);
			}
		}

		EventjarMod.queueServerWork(1, () -> {
			player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y + 1, z));
			for (int tick = 2; tick <= 400; tick += 2) {
				final int currentTick = tick;
				EventjarMod.queueServerWork(currentTick, () -> {
					if (player.isAlive() && !player.level().isClientSide()) {
						player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y + 1, z));
						double dx = x - player.getX();
						double dy = (y + 1) - player.getY();
						double dz = z - player.getZ();
						double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
						if (distance > 2.0) {
							double pullStrength = 0.1;
							player.setDeltaMovement(
									player.getDeltaMovement().x + (dx / distance) * pullStrength,
									player.getDeltaMovement().y + (dy / distance) * pullStrength,
									player.getDeltaMovement().z + (dz / distance) * pullStrength
							);
						}
						player.setDeltaMovement(0, 0, 0);
						player.teleportTo(player.getX(), player.getY(), player.getZ());
					}
				});
			}
			EventjarMod.queueServerWork(400, () -> {
				if (!entity.level().isClientSide()) {
					entity.discard();
				}
			});
		});
	}

	private static boolean hasCutsceneBeenShown(LevelAccessor world) {
		return EventjarModVariables.MapVariables.get(world).meatboyCutsceneShown;
	}

	private static void markCutsceneAsShown(LevelAccessor world) {
		EventjarModVariables.MapVariables data = EventjarModVariables.MapVariables.get(world);
		if (data.meatboyCutsceneShown) {
			return;
		}
		data.meatboyCutsceneShown = true;
		data.syncData(world);
	}

	public static void resetCutsceneFlag(LevelAccessor world) {
		EventjarModVariables.MapVariables data = EventjarModVariables.MapVariables.get(world);
		data.meatboyCutsceneShown = false;
		data.syncData(world);
	}
}
