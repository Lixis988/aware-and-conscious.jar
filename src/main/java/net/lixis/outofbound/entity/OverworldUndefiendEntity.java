package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.dimension.MazeTeleportUtil;
import net.lixis.outofbound.entity.ai.ChaseLockedPlayerGoal;
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
import net.minecraft.sounds.SoundEvent;
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

public class OverworldUndefiendEntity extends Monster implements OverworldStalker {

	public static final String MANAGED_TAG = "outofbound_overworld_undefiend_managed";

	private static final float SCALE = 2.0F;
	private static final float WIDTH = 0.9F * SCALE;
	private static final float HEIGHT = 2.0F * SCALE;
	private static final EntityDimensions SIZE = EntityDimensions.scalable(WIDTH, HEIGHT);

	private static final int GAZE_CHECK_INTERVAL = 5;
	private static final int WATCH_INTERVAL = 10;
	private static final double WATCH_RANGE = 256.0D;
	private static final double LOOK_RANGE = 128.0D;

	private static final EntityDataAccessor<Boolean> ACTIVATED = SynchedEntityData.defineId(OverworldUndefiendEntity.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<String> LOCKED_TARGET = SynchedEntityData.defineId(OverworldUndefiendEntity.class,
			EntityDataSerializers.STRING);

	private int alternateStep;
	private float stepAccumulator;
	private long spawnGameTime;

	public OverworldUndefiendEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundModEntities.OVERWORLD_UNDEFIEND.get(), world);
	}

	public OverworldUndefiendEntity(EntityType<? extends OverworldUndefiendEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(2.0F);
		xpReward = 0;
		setNoAi(false);
		this.refreshDimensions();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
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
		this.goalSelector.addGoal(1, new ChaseLockedPlayerGoal(this, this::getLockedTarget));
	}

	public void markSpawned(long gameTime) {
		this.spawnGameTime = gameTime;
	}

	public void extendDormantLifespan(long gameTime, int extensionTicks) {
		if (spawnGameTime <= 0L || extensionTicks <= 0 || isActivated()) {
			return;
		}
		this.spawnGameTime = Math.min(gameTime, spawnGameTime + extensionTicks);
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
		this.setTarget(player);
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
	public boolean doHurtTarget(Entity target) {
		if (level().isClientSide || !(target instanceof ServerPlayer player)) {
			return false;
		}
		MazeTeleportUtil.teleportToRandomMaze(player, -1L);
		this.discard();
		return true;
	}

	@Override
	public void playerTouch(Player player) {
		if (isActivated() && player instanceof ServerPlayer serverPlayer) {
			this.doHurtTarget(serverPlayer);
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
		if (level().isClientSide) {
			return;
		}

		tickLifespan();
		if (!isActivated()) {
			if (tickCount % GAZE_CHECK_INTERVAL == 0) {
				tickGazeActivation();
			}
			if (tickCount % WATCH_INTERVAL == 0) {
				watchNearestPlayer();
			}
			return;
		}

		if (getLockedTarget() == null) {
			this.discard();
			return;
		}

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

	private void tickLifespan() {
		if (isActivated() || spawnGameTime <= 0L) {
			return;
		}
		if (level().getGameTime() - spawnGameTime > MazeConfig.overworldUndefiendLifespanTicks) {
			this.discard();
		}
	}

	private void tickGazeActivation() {
		double dotThreshold = MazeConfig.overworldUndefiendLookDotThreshold;
		for (ServerPlayer player : level().getServer().getPlayerList().getPlayers()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			if (player.level().dimension() != level().dimension()) {
				continue;
			}
			if (!player.hasLineOfSight(this)) {
				continue;
			}
			if (!PlayerLookUtil.isLookingAt(player, this, LOOK_RANGE, dotThreshold)) {
				continue;
			}
			activate(player);
			return;
		}
	}

	private void watchNearestPlayer() {
		ServerPlayer nearest = null;
		double nearestSq = WATCH_RANGE * WATCH_RANGE;
		for (ServerPlayer player : level().getServer().getPlayerList().getPlayers()) {
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
		if (nearest == null) {
			return;
		}

		Vec3 delta = nearest.position().subtract(this.position());
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

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		SoundEvent step = (alternateStep++ & 1) == 0 ? OutofboundModSounds.STEP1.get() : OutofboundModSounds.STEP2.get();
		this.playSound(step, 1.0F, 1.0F);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("activated", isActivated());
		tag.putString("lockedTarget", this.entityData.get(LOCKED_TARGET));
		tag.putLong("spawnGameTime", spawnGameTime);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("activated")) {
			this.entityData.set(ACTIVATED, tag.getBoolean("activated"));
		}
		if (tag.contains("lockedTarget")) {
			this.entityData.set(LOCKED_TARGET, tag.getString("lockedTarget"));
		}
		if (tag.contains("spawnGameTime")) {
			spawnGameTime = tag.getLong("spawnGameTime");
		}
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
