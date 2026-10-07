package net.lixis.outofbound.entity;

import net.lixis.outofbound.entity.ai.ChaseNearestPlayerGoal;
import net.lixis.outofbound.init.OutofboundModSounds;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TeethmanEntity extends Monster implements ChaserEntity {

	private static final float SCALE = 2.0F;
	private static final float WIDTH = 0.9F * SCALE;
	private static final float HEIGHT = 2.0F * SCALE;
	private static final EntityDimensions SIZE = EntityDimensions.scalable(WIDTH, HEIGHT);

	private int alternateStep;
	private float stepAccumulator;

	public TeethmanEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundExtraEntities.TEETHMAN.get(), world);
	}

	public TeethmanEntity(EntityType<TeethmanEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(2.0F);
		xpReward = 0;
		setNoAi(false);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return SIZE;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.875F;
	}

	@Override
	public boolean isPersistenceRequired() {
		return true;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ChaseNearestPlayerGoal(this));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	public void playerTouch(Player player) {
		ChaserRevealHandler.markRevealed(this);
		super.playerTouch(player);
		this.doHurtTarget(player);
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
		SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
		this.refreshDimensions();
		this.setBoundingBox(this.makeBoundingBox());
		return result;
	}

	@Override
	public boolean onClimbable() {
		return this.horizontalCollision;
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
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
	public void travel(Vec3 travelVector) {
		if (!this.level().isClientSide) {
			Vec3 chase = ChaserMovement.chaseTravelInput(this);
			if (chase != null) {
				super.travel(chase);
				ChaserMovement.afterChaseTravel(this);
				ChaserMovement.tryMelee(this);
				return;
			}
		}
		super.travel(travelVector);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide || !this.onGround()) {
			return;
		}

		Vec3 delta = this.position().subtract(this.xOld, this.yOld, this.zOld);
		double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
		if (horizontal < 0.01D) {
			return;
		}

		stepAccumulator += (float) horizontal;
		if (stepAccumulator >= 1.35F) {
			stepAccumulator = 0.0F;
			BlockPos pos = BlockPos.containing(this.getX(), this.getY() - 0.2D, this.getZ());
			this.playStepSound(pos, this.level().getBlockState(pos));
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		SoundEvent step = (alternateStep++ & 1) == 0 ? OutofboundModSounds.STEP1.get() : OutofboundModSounds.STEP2.get();
		this.playSound(step, 1.0F, 1.0F);
	}

	public static void init() {
		SpawnPlacements.register(OutofboundExtraEntities.TEETHMAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.0);
		builder = builder.add(Attributes.MAX_HEALTH, 40);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 500);
		return builder;
	}
}
