
package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.procedures.GodPriStolknovieniiIghrokaSSushchnostiuProcedure;
import net.lixis9.eventjar.procedures.GodPriObnovlieniiTikaSushchnostiProcedure;
import net.lixis9.eventjar.procedures.GodPriNachalnomPrizyvieSushchnostiProcedure;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.client.event.FaultGlitchHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class GodEntity extends Creeper {
    private boolean isAggressive = false;
    @Nullable
    private UUID targetPlayerId = null;

    // Радиус активации взглядом увеличен до 48 блоков
    private static final int ACTIVATION_RANGE = 48;
    
    // Радиус ломания блоков
    private static final int BLOCK_BREAK_RANGE = 4;
    
    // Частота проверки блоков для ломания
    private static final int BLOCK_CHECK_INTERVAL = 5;

	public GodEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(EventjarModEntities.GOD.get(), world);
	}

	public GodEntity(EntityType<? extends GodEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(1.0f); // Увеличена высота шага для лучшей навигации
		xpReward = 0;
		setNoAi(false);
		setCustomName(Component.literal("the god"));
		setCustomNameVisible(true);
		setPersistenceRequired();
		this.moveControl = new FlyingMoveControl(this, 10, true);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		
		// Цель атаки (активна только в агрессивном режиме)
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, false) {  // Увеличена скорость атаки
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				// Увеличена дальность атаки до 6 блоков
				return (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) * 6;
			}
			
			@Override
			public boolean canUse() {
				return ((GodEntity) this.mob).isAggressive && super.canUse();
			}
			
			@Override
			public boolean canContinueToUse() {
				return ((GodEntity) this.mob).isAggressive && super.canContinueToUse();
			}
		});
		
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(4, new FloatGoal(this));
		
		// Цель для атаки игрока
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false) {
			@Override
			public boolean canUse() {
				return ((GodEntity) this.mob).isAggressive && super.canUse();
			}
		});
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chaise"));
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:steps_seeker")), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource damagesource, float amount) {
		if (damagesource.is(DamageTypes.IN_FIRE))
			return false;
		if (damagesource.getDirectEntity() instanceof AbstractArrow)
			return false;
		if (damagesource.getDirectEntity() instanceof Player)
			return false;
		if (damagesource.getDirectEntity() instanceof ThrownPotion || damagesource.getDirectEntity() instanceof AreaEffectCloud)
			return false;
		if (damagesource.is(DamageTypes.FALL))
			return false;
		if (damagesource.is(DamageTypes.CACTUS))
			return false;
		if (damagesource.is(DamageTypes.DROWN))
			return false;
		if (damagesource.is(DamageTypes.LIGHTNING_BOLT))
			return false;
		if (damagesource.is(DamageTypes.EXPLOSION) || damagesource.is(DamageTypes.PLAYER_EXPLOSION))
			return false;
		if (damagesource.is(DamageTypes.TRIDENT))
			return false;
		if (damagesource.is(DamageTypes.FALLING_ANVIL))
			return false;
		if (damagesource.is(DamageTypes.DRAGON_BREATH))
			return false;
		if (damagesource.is(DamageTypes.WITHER) || damagesource.is(DamageTypes.WITHER_SKULL))
			return false;
		return super.hurt(damagesource, amount);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false; // Сущность может проходить сквозь другие сущности
	}

	@Override
	public boolean isPushable() {
		return false; // Сущность не может быть оттолкнута
	}
	
	public boolean isAggressive() {
		return this.isAggressive;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		GodPriNachalnomPrizyvieSushchnostiProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
		return retval;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		GodPriObnovlieniiTikaSushchnostiProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
	}

	@Override
	public void playerTouch(Player sourceentity) {
		super.playerTouch(sourceentity);
		GodPriStolknovieniiIghrokaSSushchnostiuProcedure.execute(this.level(), this);
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	@Override
	public void tick() {
		super.tick();
		this.setNoGravity(true);
		
		if (!this.level().isClientSide) {
			// Удаляем сущность если целевой игрок мёртв
			if (targetPlayerId != null) {
				Player targetPlayer = this.level().getPlayerByUUID(targetPlayerId);
				if (targetPlayer == null || !targetPlayer.isAlive()) {
					// Звук исчезновения
					this.level().playSound(null, this.blockPosition(), 
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chaise")), 
						this.getSoundSource(), 0.6f, 0.8f);
					this.discard();
					return;
				}
			}
			// Проверка взгляда каждые 15 тиков (0.75 секунды) - увеличенная частота
			else if (this.tickCount % 15 == 0 && !isAggressive) {
				// Используем увеличенный радиус активации
				Player nearestPlayer = this.level().getNearestPlayer(this, ACTIVATION_RANGE);
				if (nearestPlayer != null && nearestPlayer.hasLineOfSight(this)) {
					activateAggression(nearestPlayer);
				}
			}
			
			// Случайные звуки дыхания в неактивном состоянии каждые 300 тиков (15 секунд)
			if (!isAggressive && this.tickCount % 300 == 0 && random.nextFloat() < 0.5f) {
				SoundEvent breathSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chaise"));
				this.level().playSound(null, this.blockPosition(), breathSound, 
					this.getSoundSource(), 0.6f, 1.0f + random.nextFloat() * 0.2f);
			}
			
			// В агрессивном режиме постоянно корректируем движение к цели и ломаем блоки
			if (isAggressive && this.getTarget() != null) {
				if (this.tickCount % 2 == 0) { // Увеличена частота обновления движения
					this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
					
					// Прямое движение к цели с улучшенной навигацией
					double dx = this.getTarget().getX() - this.getX();
					double dy = this.getTarget().getY() - this.getY();
					double dz = this.getTarget().getZ() - this.getZ();
					double distance = Math.sqrt(dx * dx + dz * dz);
					
					if (distance > 1.0) { // Если цель достаточно далеко
						// Нормализуем направление и двигаемся прямо
						dx /= distance;
						dz /= distance;
						
						// Устанавливаем скорость движения с учетом высоты
						double moveSpeed = 1.2; // Увеличена скорость
						this.setDeltaMovement(dx * moveSpeed, this.getDeltaMovement().y, dz * moveSpeed);
						
						// Обновляем навигацию для обхода препятствий
						this.getNavigation().moveTo(this.getTarget(), moveSpeed);
						
						// Принудительно двигаемся к цели
						this.moveTo(this.getX() + dx * 0.1, this.getY(), this.getZ() + dz * 0.1);
						
						// Если игрок слишком далеко, телепортируемся ближе
						if (distance > 20.0) {
							double teleportDistance = 15.0;
							double teleportX = this.getTarget().getX() - dx * teleportDistance;
							double teleportZ = this.getTarget().getZ() - dz * teleportDistance;
							this.teleportTo(teleportX, this.getY(), teleportZ);
						}
					}
				}
				
				// Звуки преследования каждые 60 тиков (3 секунды)
				if (this.tickCount % 60 == 0) {
					SoundEvent chaseSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chaise"));
					this.level().playSound(null, this.blockPosition(), chaseSound, 
						this.getSoundSource(), 0.8f, 0.9f + random.nextFloat() * 0.2f);
				}
				
				// Ломаем блоки на пути каждые BLOCK_CHECK_INTERVAL тиков
				if (this.tickCount % BLOCK_CHECK_INTERVAL == 0) {
					breakBlocksInPath();
				}
			}
		}
	}

	private void breakBlocksInPath() {
		if (this.getTarget() == null) return;
		
		// Направление к цели
		double dx = this.getTarget().getX() - this.getX();
		double dz = this.getTarget().getZ() - this.getZ();
		double distance = Math.sqrt(dx * dx + dz * dz);
		
		if (distance > BLOCK_BREAK_RANGE) return;
		
		// Нормализуем направление
		dx /= distance;
		dz /= distance;
		
		// Проверяем блоки на пути к игроку
		for (int i = 1; i <= BLOCK_BREAK_RANGE; i++) {
			BlockPos checkPos = new BlockPos(
				(int)(this.getX() + dx * i),
				(int)this.getY(),
				(int)(this.getZ() + dz * i)
			);
			
			// Проверяем только блоки на уровне сущности и чуть выше/ниже
			for (int y = -1; y <= 2; y++) {
				BlockPos pos = checkPos.above(y);
				BlockState state = this.level().getBlockState(pos);
				
				// Проверяем, мешает ли блок проходу
				if (isBlockObstructingPath(pos, state)) {
					// Ломаем только блоки, которые действительно мешают
					this.level().destroyBlock(pos, false);
					this.level().playSound(null, pos, SoundEvents.STONE_BREAK, 
						this.getSoundSource(), 0.3f, 1.0f);
					
					// Добавляем частицы разрушения
					this.level().addDestroyBlockEffect(pos, state);
				}
			}
		}
	}
	
	private boolean isBlockObstructingPath(BlockPos pos, BlockState state) {
		// Не ломаем воздух
		if (state.isAir()) return false;
		
		// Не ломаем очень твердые блоки
		if (state.getBlock() == Blocks.BEDROCK || 
			state.getBlock() == Blocks.BARRIER ||
			state.getBlock() == Blocks.OBSIDIAN ||
			state.getBlock() == Blocks.CRYING_OBSIDIAN ||
			state.getDestroySpeed(this.level(), pos) >= 50.0f) {
			return false;
		}
		
		// Проверяем, действительно ли блок мешает проходу
		// Ломаем только если блок находится на пути движения
		double dx = this.getTarget().getX() - this.getX();
		double dz = this.getTarget().getZ() - this.getZ();
		double distance = Math.sqrt(dx * dx + dz * dz);
		
		if (distance < 2.0) return false; // Не ломаем блоки слишком близко к игроку
		
		// Проверяем, находится ли блок на прямой линии к игроку
		double blockDistance = Math.sqrt(
			Math.pow(pos.getX() - this.getX(), 2) + 
			Math.pow(pos.getZ() - this.getZ(), 2)
		);
		
		// Ломаем только блоки на пути движения
		return blockDistance <= BLOCK_BREAK_RANGE && 
			   Math.abs(pos.getY() - this.getY()) <= 2; // Только блоки на уровне сущности
	}

	private void activateAggression(Player player) {
		this.isAggressive = true;
		this.targetPlayerId = player.getUUID();
		this.setTarget(player);
		
		// Увеличена скорость передвижения
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1.5);
		
		// Увеличена скорость атаки
		if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
			this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(18); // 9 сердец урона
		}
		
		// Активируем глитч-эффект на клиенте
		if (this.level().isClientSide) {
			FaultGlitchHandler.triggerActivationGlitch();
		}
		
		// Звук появления
		this.level().playSound(null, this.blockPosition(), 
			ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chaise")), 
			this.getSoundSource(), 1.0f, 0.8f);
	}

	public static void init() {
		// Отключаем естественный спавн - сущность будет появляться только через команды или спавн-яйца
		// SpawnPlacements.register(EventjarModEntities.GOD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
		//         (entityType, world, reason, pos, random) -> false);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		// Начальная скорость = 0 (сущность не двигается)
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
		builder = builder.add(Attributes.MAX_HEALTH, 15); // Увеличено здоровье
		builder = builder.add(Attributes.ARMOR, 5); // Добавлена броня
		builder = builder.add(Attributes.ATTACK_DAMAGE, 16); // Изначально 8 сердец
		builder = builder.add(Attributes.FOLLOW_RANGE, 80); // Увеличенный радиус преследования до 80 блоков
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 2.0); // Усиленное отбрасывание при атаке
		builder = builder.add(Attributes.ATTACK_SPEED, 3.0); // Увеличена скорость атаки
		builder = builder.add(Attributes.FLYING_SPEED, 0.3);
		return builder;
	}
	
	// Отключаем естественный спавн
	public static boolean checkSpawnRules(EntityType<GodEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
		return false; // Никогда не спавнимся естественным образом
	}
}
