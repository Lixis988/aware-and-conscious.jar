package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.procedures.RageThen60sPriObnovlieniiTikaSushchnostiProcedure;
import net.lixis9.eventjar.procedures.RageThen60sPriNachalnomPrizyvieSushchnostiProcedure;
import net.lixis9.eventjar.procedures.RageThen60sPriGibieliOtEtoiSushchnostiDrughoiProcedure;
import net.lixis9.eventjar.init.EventjarModEntities;

import javax.annotation.Nullable;

import java.util.EnumSet;

public class RageThen60sEntity extends Monster {
	public RageThen60sEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(EventjarModEntities.RAGE_THEN_60S.get(), world);
	}

	public RageThen60sEntity(EntityType<RageThen60sEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(0.5f);
		xpReward = 0;
		setNoAi(false);
		setCustomName(Component.literal("err_null"));
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.goalSelector.addGoal(2, new FloatGoal(this));
		this.goalSelector.addGoal(3, new Goal() {
			{
				this.setFlags(EnumSet.of(Goal.Flag.MOVE));
			}

			public boolean canUse() {
				if (RageThen60sEntity.this.getTarget() != null && !RageThen60sEntity.this.getMoveControl().hasWanted()) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public boolean canContinueToUse() {
				return RageThen60sEntity.this.getMoveControl().hasWanted() && RageThen60sEntity.this.getTarget() != null && RageThen60sEntity.this.getTarget().isAlive();
			}

			@Override
			public void start() {
				LivingEntity livingentity = RageThen60sEntity.this.getTarget();
				Vec3 vec3d = livingentity.getEyePosition(1);
				RageThen60sEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 3);
			}

			@Override
			public void tick() {
				LivingEntity livingentity = RageThen60sEntity.this.getTarget();
				if (RageThen60sEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
					RageThen60sEntity.this.doHurtTarget(livingentity);
				} else {
					double d0 = RageThen60sEntity.this.distanceToSqr(livingentity);
					if (d0 < 16) {
						Vec3 vec3d = livingentity.getEyePosition(1);
						RageThen60sEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 3);
					}
				}
			}
		});
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 100, 20) {
			@Override
			protected Vec3 getPosition() {
				RandomSource random = RageThen60sEntity.this.getRandom();
				double dir_x = RageThen60sEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_y = RageThen60sEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
				double dir_z = RageThen60sEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
				return new Vec3(dir_x, dir_y, dir_z);
			}
		});
		this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Player.class, false, false));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, (float) 6));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, ServerPlayer.class, (float) 6));
		this.goalSelector.addGoal(9, new RemoveBlockGoal(Blocks.STONE, this, 1000, (int) 1));
		this.goalSelector.addGoal(10, new RemoveBlockGoal(Blocks.GRASS_BLOCK, this, 1000, (int) 3));
		this.goalSelector.addGoal(11, new RemoveBlockGoal(Blocks.DIRT, this, 1000, (int) 3));
		this.goalSelector.addGoal(12, new RemoveBlockGoal(Blocks.DEEPSLATE, this, 1000, (int) 3));
		this.goalSelector.addGoal(13, new RemoveBlockGoal(Blocks.OAK_LEAVES, this, 1000, (int) 3));
		this.goalSelector.addGoal(14, new RemoveBlockGoal(Blocks.OAK_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(15, new RemoveBlockGoal(Blocks.SPRUCE_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(16, new RemoveBlockGoal(Blocks.BIRCH_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(17, new RemoveBlockGoal(Blocks.JUNGLE_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(18, new RemoveBlockGoal(Blocks.ACACIA_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(19, new RemoveBlockGoal(Blocks.DARK_OAK_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(20, new RemoveBlockGoal(Blocks.MANGROVE_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(21, new RemoveBlockGoal(Blocks.CHERRY_WOOD, this, 1000, (int) 3));
		this.goalSelector.addGoal(22, new RemoveBlockGoal(Blocks.OAK_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(23, new RemoveBlockGoal(Blocks.SPRUCE_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(24, new RemoveBlockGoal(Blocks.BIRCH_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(25, new RemoveBlockGoal(Blocks.JUNGLE_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(26, new RemoveBlockGoal(Blocks.ACACIA_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(27, new RemoveBlockGoal(Blocks.DARK_OAK_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(28, new RemoveBlockGoal(Blocks.CRIMSON_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(29, new RemoveBlockGoal(Blocks.WARPED_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(30, new RemoveBlockGoal(Blocks.MANGROVE_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(31, new RemoveBlockGoal(Blocks.CHERRY_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(32, new RemoveBlockGoal(Blocks.BAMBOO_PLANKS, this, 1000, (int) 3));
		this.goalSelector.addGoal(33, new RemoveBlockGoal(Blocks.GLASS, this, 1000, (int) 3));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
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
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		RageThen60sPriNachalnomPrizyvieSushchnostiProcedure.execute(world, this);
		return retval;
	}

	@Override
	public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
		super.awardKillScore(entity, score, damageSource);
		RageThen60sPriGibieliOtEtoiSushchnostiDrughoiProcedure.execute(entity);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		RageThen60sPriObnovlieniiTikaSushchnostiProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	public void aiStep() {
		super.aiStep();
		this.setNoGravity(true);
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 1000);
		builder = builder.add(Attributes.FLYING_SPEED, 0.5);
		return builder;
	}
}
