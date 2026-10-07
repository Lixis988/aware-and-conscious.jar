package net.lixis9.eventjar.entity;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.network.VillagerMimicCrashPacket;
import net.lixis9.eventjar.EventjarMod;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class VillagerMimicEntity extends PathfinderMob {

	private static final EntityDataAccessor<Boolean> DATA_GLITCHING =
			SynchedEntityData.defineId(VillagerMimicEntity.class, EntityDataSerializers.BOOLEAN);

	private boolean triggered;
	private int nextGlitchAt;

	public VillagerMimicEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(EventjarModEntities.VILLAGER_MIMIC.get(), world);
	}

	public VillagerMimicEntity(EntityType<? extends VillagerMimicEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(0.6f);
		xpReward = 0;
		setPersistenceRequired();
		this.nextGlitchAt = 80 + this.random.nextInt(400);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_GLITCHING, false);
	}

	public boolean isGlitching() {
		return this.entityData.get(DATA_GLITCHING);
	}

	private void setGlitching(boolean value) {
		this.entityData.set(DATA_GLITCHING, value);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.4D));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
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
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (!this.level().isClientSide && !triggered) {
			trigger(player);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.sidedSuccess(this.level().isClientSide);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.level().isClientSide && !triggered && source.getEntity() instanceof Player player) {
			trigger(player);
			return false;
		}
		return super.hurt(source, amount);
	}

	private void trigger(Player player) {
		if (triggered || this.level().isClientSide) {
			return;
		}
		triggered = true;

		this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
				SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.4F, 0.4F);

		if (this.level() instanceof ServerLevel serverLevel) {
			MimicTendrilEntity tendril = EventjarModEntities.MIMIC_TENDRIL.get().create(serverLevel);
			if (tendril != null) {
				tendril.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
				tendril.setTarget(player);
				serverLevel.addFreshEntity(tendril);
			}
		}

		if (player instanceof ServerPlayer serverPlayer) {
			VillagerMimicCrashPacket.send(serverPlayer);
		}

		this.discard();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide || triggered) {
			return;
		}

		if (this.tickCount >= nextGlitchAt) {
			setGlitching(true);
			EventjarMod.queueServerWork(4 + this.random.nextInt(6), () -> {
				if (this.isAlive()) {
					setGlitching(false);
				}
			});
			nextGlitchAt = this.tickCount + 200 + this.random.nextInt(900);
		}
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.VILLAGER_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.VILLAGER_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.VILLAGER_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.6F;
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.35D)
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.FOLLOW_RANGE, 16.0D)
				.add(Attributes.ARMOR, 0.0D);
	}
}
