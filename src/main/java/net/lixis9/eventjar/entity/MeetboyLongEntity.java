package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;

import net.lixis9.eventjar.init.EventjarModEntities;

public class MeetboyLongEntity extends Monster {

	private static final EntityDataAccessor<Boolean> CHASING =
			SynchedEntityData.defineId(MeetboyLongEntity.class, EntityDataSerializers.BOOLEAN);
	private static final double LOOK_DOT = 0.92D;
	private static final double WATCH_RANGE = 64.0D;
	private static final double CHASE_SPEED = 0.42D;

	public MeetboyLongEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(EventjarModEntities.MEETBOY_LONG.get(), world);
	}

	public MeetboyLongEntity(EntityType<MeetboyLongEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(0.6f);
		xpReward = 0;
		setNoAi(false);
		setCustomName(Component.literal("Hello :3"));
		setCustomNameVisible(true);
		setPersistenceRequired();
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(CHASING, false);
	}

	public boolean isChasing() {
		return this.entityData.get(CHASING);
	}

	public void startChase(Player player) {
		if (player == null || isChasing()) {
			return;
		}
		this.entityData.set(CHASING, true);
		setTarget(player);
		setMaxUpStep(1.2f);
		if (getAttribute(Attributes.MOVEMENT_SPEED) != null) {
			getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(CHASE_SPEED);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("Chasing", isChasing());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.getBoolean("Chasing")) {
			this.entityData.set(CHASING, true);
			setMaxUpStep(1.2f);
			if (getAttribute(Attributes.MOVEMENT_SPEED) != null) {
				getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(CHASE_SPEED);
			}
		}
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.9D, true) {
			@Override
			public boolean canUse() {
				return isChasing() && super.canUse();
			}

			@Override
			public boolean canContinueToUse() {
				return isChasing() && super.canContinueToUse();
			}

			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) * 3.0D;
			}
		});
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, ServerPlayer.class, 64.0F));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 64.0F));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true) {
			@Override
			public boolean canUse() {
				return isChasing() && super.canUse();
			}
		});
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		if (isChasing()) {
			LivingEntity target = getTarget();
			if (target instanceof Player player && player.isAlive() && !player.isSpectator()) {
				getLookControl().setLookAt(player, 40.0F, 40.0F);
				if (distanceTo(player) > 8.0D) {
					getNavigation().moveTo(player, 1.85D);
				}
			} else {
				Player nearest = level().getNearestPlayer(this, WATCH_RANGE);
				if (nearest != null && nearest.isAlive() && !nearest.isSpectator()) {
					setTarget(nearest);
				}
			}
			return;
		}
		if (level().isDay() && tickCount > 40) {
			discard();
			return;
		}
		Player nearest = level().getNearestPlayer(this, WATCH_RANGE);
		if (nearest == null || nearest.isSpectator() || !nearest.isAlive()) {
			return;
		}
		getLookControl().setLookAt(nearest, 40.0F, 40.0F);
		if (StalkerLookUtil.isPlayerLookingAt(nearest, this, LOOK_DOT)) {
			startChase(nearest);
		}
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
	public double getMyRidingOffset() {
		return -0.35D;
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

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		return builder;
	}
}
