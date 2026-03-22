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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.EventjarMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class MeatboydistortedCutsceneProcedure {
	private static final String CUTSCENE_TAG = "meatboydistorted_cutscene_shown";
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		// Проверяем, что это первый вход в мир, а не респавн после смерти
		if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
			// Проверяем, что игрок находится в реальном мире (overworld)
			Level level = serverPlayer.level();
			if (level.dimension() != Level.OVERWORLD) {
				return; // Катсцена только для реального мира
			}
			
			// Проверяем, была ли уже показана катсцена для этого игрока
			if (hasCutsceneBeenShown(serverPlayer)) {
				return; // Катсцена уже была показана
			}
			
			// Проверяем, есть ли уже meatboydistorted в мире
			if (level instanceof ServerLevel serverLevel) {
				// Ищем существующих meatboydistorted в радиусе 100 блоков от игрока
				boolean hasMeatboydistorted = serverLevel.getEntitiesOfClass(
					net.lixis9.eventjar.entity.MeatboydistortedEntity.class,
					serverPlayer.getBoundingBox().inflate(100.0),
					entity -> true
				).size() > 0;
				
				if (hasMeatboydistorted) {
					return; // Уже есть meatboydistorted в мире
				}
			}
		}
		
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null || !(entity instanceof Player player))
			return;
		
		// Отмечаем, что катсцена была показана
		markCutsceneAsShown(player);
		
		// Спавним сущность в 3 блоках от игрока параллельно (впереди по направлению взгляда)
		// Получаем направление взгляда игрока
		float yaw = player.getYRot();
		double spawnX = x + Math.sin(Math.toRadians(-yaw)) * 3.0;
		double spawnY = y; // Та же высота что и у игрока
		double spawnZ = z + Math.cos(Math.toRadians(-yaw)) * 3.0;
		
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = EventjarModEntities.MEATBOYDISTORTED.get().spawn(_level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setDeltaMovement(0, 0, 0);
				
				// Запускаем катсцену через небольшую задержку
				EventjarMod.queueServerWork(20, () -> {
					startCutscene(world, spawnX, spawnY, spawnZ, entityToSpawn, player);
				});
			}
		}
	}
	
	private static void startCutscene(LevelAccessor world, double x, double y, double z, Entity entity, Player player) {
		// Проигрываем аудио
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:asyoubreathe")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:asyoubreathe")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}
		
		// Запускаем катсцену на 20 секунд (400 тиков)
		EventjarMod.queueServerWork(1, () -> {
			// Поворачиваем игрока к сущности
			player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y + 1, z));
			
			// Продолжаем катсцену каждые 2 тика в течение 20 секунд
			for (int tick = 2; tick <= 400; tick += 2) {
				final int currentTick = tick;
				EventjarMod.queueServerWork(currentTick, () -> {
					if (player.isAlive() && !player.level().isClientSide()) {
						// Принудительно поворачиваем игрока к сущности
						player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y + 1, z));
						
						// Примагничиваем игрока к сущности (мягкое притяжение)
						double dx = x - player.getX();
						double dy = (y + 1) - player.getY();
						double dz = z - player.getZ();
						double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
						
						// Если игрок слишком далеко, притягиваем его ближе
						if (distance > 2.0) {
							double pullStrength = 0.1; // Сила притяжения
							player.setDeltaMovement(
								player.getDeltaMovement().x + (dx / distance) * pullStrength,
								player.getDeltaMovement().y + (dy / distance) * pullStrength,
								player.getDeltaMovement().z + (dz / distance) * pullStrength
							);
						}
						
						// Блокируем движение игрока (устанавливаем скорость в 0)
						player.setDeltaMovement(0, 0, 0);
						
						// Принудительно возвращаем игрока в позицию лицом к сущности
						player.teleportTo(player.getX(), player.getY(), player.getZ());
					}
				});
			}
			
			// Удаляем сущность после окончания катсцены (400 тиков)
			EventjarMod.queueServerWork(400, () -> {
				if (!entity.level().isClientSide()) {
					entity.discard();
				}
			});
		});
	}
	
	// Метод для проверки, была ли показана катсцена
	private static boolean hasCutsceneBeenShown(Player player) {
		CompoundTag persistentData = player.getPersistentData();
		return persistentData.getBoolean(CUTSCENE_TAG);
	}
	
	// Метод для отметки, что катсцена была показана
	private static void markCutsceneAsShown(Player player) {
		CompoundTag persistentData = player.getPersistentData();
		persistentData.putBoolean(CUTSCENE_TAG, true);
	}
	
	// Метод для сброса флага катсцены (для тестирования)
	public static void resetCutsceneFlag(Player player) {
		CompoundTag persistentData = player.getPersistentData();
		persistentData.putBoolean(CUTSCENE_TAG, false);
	}
}
