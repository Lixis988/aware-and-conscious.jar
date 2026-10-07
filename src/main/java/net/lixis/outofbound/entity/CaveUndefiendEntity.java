package net.lixis.outofbound.entity;

import net.lixis.outofbound.init.OutofboundModEntities;
import net.lixis.outofbound.init.OutofboundModSounds;
import net.lixis.outofbound.util.PlayerLookUtil;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CaveUndefiendEntity extends Monster implements OverworldStalker {

	public static final String MANAGED_TAG = "outofbound_cave_undefiend_managed";

	private static final float SCALE = 2.0F;
	private static final float WIDTH = 0.9F * SCALE;
	private static final float HEIGHT = 2.0F * SCALE;
	private static final EntityDimensions SIZE = EntityDimensions.scalable(WIDTH, HEIGHT);

	private static final int GAZE_CHECK_INTERVAL = 2;
	private static final int WATCH_INTERVAL = 10;
	private static final double LOOK_RANGE = 48.0D;
	private static final int CHASE_TIMEOUT_TICKS = 30 * 20;

	private static final EntityDataAccessor<Boolean> DISTORTED = SynchedEntityData.defineId(CaveUndefiendEntity.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> GLITCH_SNAP_SEED = SynchedEntityData.defineId(CaveUndefiendEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> ACTIVATED = SynchedEntityData.defineId(CaveUndefiendEntity.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<String> LOCKED_TARGET = SynchedEntityData.defineId(CaveUndefiendEntity.class,
			EntityDataSerializers.STRING);

	private int alternateStep;
	private float stepAccumulator;
	private int chaseTicks;
	@Nullable
	private UUID pendingWatcherId;

	public CaveUndefiendEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundModEntities.CAVE_UNDEFIEND.get(), world);
	}

	public CaveUndefiendEntity(EntityType<? extends CaveUndefiendEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(2.0F);
		xpReward = 0;
		setNoAi(false);
		this.refreshDimensions();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DISTORTED, false);
		this.entityData.define(GLITCH_SNAP_SEED, 0);
		this.entityData.define(ACTIVATED, false);
		this.entityData.define(LOCKED_TARGET, "");
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
		this.goalSelector.addGoal(1, new net.lixis.outofbound.entity.ai.ChaseLockedPlayerGoal(this, this::getLockedTarget));
	}

	public boolean isDistorted() {
		return this.entityData.get(DISTORTED);
	}

	public int getGlitchSnapSeed() {
		return this.entityData.get(GLITCH_SNAP_SEED);
	}

	private void setDistorted(boolean value) {
		this.entityData.set(DISTORTED, value);
	}

	private void setGlitchSnapSeed(int seed) {
		this.entityData.set(GLITCH_SNAP_SEED, seed);
	}

	@Override
	public boolean isActivated() {
		return this.entityData.get(ACTIVATED);
	}

	@Override
	@Nullable
	public ServerPlayer getLockedTarget() {
		if (!isActivated()) {
			return null;
		}
		String raw = this.entityData.get(LOCKED_TARGET);
		if (raw == null || raw.isEmpty()) {
			return null;
		}
		try {
			UUID uuid = UUID.fromString(raw);
			MinecraftServer server = level().getServer();
			if (server == null) {
				return null;
			}
			ServerPlayer player = server.getPlayerList().getPlayer(uuid);
			if (player == null || !player.isAlive() || player.isSpectator()) {
				return null;
			}
			return player;
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	private void activate(ServerPlayer player) {
		this.entityData.set(ACTIVATED, true);
		this.entityData.set(LOCKED_TARGET, player.getUUID().toString());
		this.setDistorted(false);
		this.setGlitchSnapSeed(0);
		this.chaseTicks = 0;
		this.setTarget(player);
		ChaserRevealHandler.markRevealed(this);
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
		if (isActivated()) {
			ChaserRevealHandler.markRevealed(this);
			super.playerTouch(player);
			this.doHurtTarget(player);
		}
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
	public net.minecraft.sounds.SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public net.minecraft.sounds.SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (!this.level().isClientSide && !isActivated()) {
			this.setDeltaMovement(Vec3.ZERO);
			this.getNavigation().stop();
			return;
		}
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
		if (level().isClientSide) {
			return;
		}

		if (isActivated()) {
			chaseTicks++;
			if (chaseTicks >= CHASE_TIMEOUT_TICKS) {
				this.discard();
				return;
			}
			if (getLockedTarget() == null) {
				this.discard();
				return;
			}
			playStepSoundsIfMoving();
			return;
		}

		if (tickCount % GAZE_CHECK_INTERVAL == 0) {
			tickGazeReaction();
		}
		if (tickCount % WATCH_INTERVAL == 0) {
			watchNearestPlayer();
		}
	}

	private void watchNearestPlayer() {
		ServerPlayer nearest = null;
		double nearestSq = LOOK_RANGE * LOOK_RANGE;
		MinecraftServer server = level().getServer();
		if (server == null) {
			return;
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			if (player.level().dimension() != level().dimension()) {
				continue;
			}
			double distSq = this.distanceToSqr(player);
			if (distSq <= nearestSq) {
				nearestSq = distSq;
				nearest = player;
			}
		}
		if (nearest != null) {
			facePlayer(nearest);
		}
	}

	private void tickGazeReaction() {
		ServerPlayer watcher = findWatcher();
		if (watcher != null) {
			pendingWatcherId = watcher.getUUID();
			if (!isDistorted()) {
				triggerDistortion(watcher);
			}
			this.setDeltaMovement(Vec3.ZERO);
			this.getNavigation().stop();
			facePlayer(watcher);
			return;
		}

		if (isDistorted() && pendingWatcherId != null) {
			MinecraftServer server = level().getServer();
			if (server != null) {
				ServerPlayer previous = server.getPlayerList().getPlayer(pendingWatcherId);
				if (previous != null && previous.isAlive() && !previous.isSpectator()) {
					activate(previous);
				}
			}
			pendingWatcherId = null;
		}
	}

	private void triggerDistortion(ServerPlayer watcher) {
		setDistorted(true);
		setGlitchSnapSeed(ThreadLocalRandom.current().nextInt(1, 1_000_000));
		playGazeScareSound(watcher);
	}

	private void playGazeScareSound(ServerPlayer watcher) {
		float pitch = 0.75F + ThreadLocalRandom.current().nextFloat() * 0.35F;
		level().playSound(null, watcher.getX(), watcher.getY(), watcher.getZ(), SoundEvents.AMBIENT_CAVE.value(), SoundSource.HOSTILE, 1.0F, pitch);
	}

	@Nullable
	private ServerPlayer findWatcher() {
		MinecraftServer server = level().getServer();
		if (server == null) {
			return null;
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			if (player.level().dimension() != level().dimension()) {
				continue;
			}
			if (!player.hasLineOfSight(this)) {
				continue;
			}
			if (PlayerLookUtil.isLookingExactlyAt(player, this, LOOK_RANGE)) {
				return player;
			}
		}
		return null;
	}

	private void facePlayer(ServerPlayer player) {
		Vec3 delta = player.position().subtract(this.position());
		double horizontalDistance = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
		if (horizontalDistance > 0.001D) {
			float yaw = (float) (Mth.atan2(-delta.x, delta.z) * (180.0D / Math.PI));
			this.setYRot(yaw);
			this.yRotO = yaw;
			this.yBodyRot = yaw;
			this.yBodyRotO = yaw;
			this.setYHeadRot(yaw);
		}
	}

	private void playStepSoundsIfMoving() {
		if (!this.onGround()) {
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
		net.minecraft.sounds.SoundEvent step = (alternateStep++ & 1) == 0 ? OutofboundModSounds.STEP1.get() : OutofboundModSounds.STEP2.get();
		this.playSound(step, 1.0F, 1.0F);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("distorted", isDistorted());
		tag.putInt("glitchSnapSeed", getGlitchSnapSeed());
		tag.putBoolean("activated", isActivated());
		tag.putInt("chaseTicks", chaseTicks);
		tag.putString("lockedTarget", this.entityData.get(LOCKED_TARGET));
		if (pendingWatcherId != null) {
			tag.putUUID("pendingWatcher", pendingWatcherId);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("distorted")) {
			setDistorted(tag.getBoolean("distorted"));
		}
		if (tag.contains("glitchSnapSeed")) {
			setGlitchSnapSeed(tag.getInt("glitchSnapSeed"));
		}
		if (tag.contains("activated")) {
			this.entityData.set(ACTIVATED, tag.getBoolean("activated"));
		}
		if (tag.contains("chaseTicks")) {
			chaseTicks = tag.getInt("chaseTicks");
		}
		if (tag.contains("lockedTarget")) {
			this.entityData.set(LOCKED_TARGET, tag.getString("lockedTarget"));
		}
		if (tag.hasUUID("pendingWatcher")) {
			pendingWatcherId = tag.getUUID("pendingWatcher");
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.15);
		builder = builder.add(Attributes.MAX_HEALTH, 40);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		return builder;
	}
}
