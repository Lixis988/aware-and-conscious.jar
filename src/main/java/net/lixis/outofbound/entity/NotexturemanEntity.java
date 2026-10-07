package net.lixis.outofbound.entity;

import net.lixis.outofbound.NotexturemanOfferPacket;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.MazeTeleportUtil;
import net.lixis.outofbound.entity.ai.NotexturemanApproachGoal;
import net.lixis.outofbound.entity.ai.NotexturemanFleeGoal;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class NotexturemanEntity extends net.minecraft.world.entity.PathfinderMob {

	public static final String MANAGED_TAG = "outofbound_notextureman_managed";

	public static final int STATE_APPROACH = 0;
	public static final int STATE_WAITING = 1;
	public static final int STATE_FLEEING = 2;

	private static final int OFFER_TIMEOUT_TICKS = 600;
	private static final double FLEE_DESPAWN_DISTANCE = 40.0D;

	private static final float WIDTH = 0.9F;
	private static final float HEIGHT = 2.0F;
	private static final EntityDimensions SIZE = EntityDimensions.scalable(WIDTH, HEIGHT);

	private int behaviorState = STATE_APPROACH;
	@Nullable
	private UUID targetPlayer;
	private int offerLevel;
	private int waitingTicks;
	private int fleeTicks;

	public NotexturemanEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundExtraEntities.NOTEXTUREMAN.get(), world);
	}

	public NotexturemanEntity(EntityType<? extends NotexturemanEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setPersistenceRequired();
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

	public void setTargetPlayer(UUID uuid) {
		this.targetPlayer = uuid;
	}

	public int getBehaviorState() {
		return behaviorState;
	}

	public void setBehaviorState(int state) {
		this.behaviorState = state;
	}

	public int getOfferLevel() {
		return offerLevel;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new NotexturemanApproachGoal(this));
		this.goalSelector.addGoal(2, new NotexturemanFleeGoal(this));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
	}

	@Nullable
	public ServerPlayer resolveTargetPlayer() {
		if (level().isClientSide || !(level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
			return null;
		}
		if (targetPlayer != null) {
			ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(targetPlayer);
			if (player != null && player.isAlive() && !player.isSpectator()) {
				return player;
			}
		}
		ServerPlayer nearest = null;
		double nearestSq = Double.MAX_VALUE;
		for (ServerPlayer player : serverLevel.players()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			double distSq = distanceToSqr(player);
			if (distSq < nearestSq) {
				nearestSq = distSq;
				nearest = player;
			}
		}
		return nearest;
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide) {
			return;
		}

		ServerPlayer target = resolveTargetPlayer();
		switch (behaviorState) {
			case STATE_APPROACH -> tickApproach(target);
			case STATE_WAITING -> tickWaiting(target);
			case STATE_FLEEING -> tickFleeing(target);
			default -> {
			}
		}
	}

	private void tickApproach(@Nullable ServerPlayer target) {
		if (!isValidMazeTarget(target)) {
			beginFleeing();
			return;
		}
		if (target != null && distanceTo(target) <= MazeConfig.notexturemanTriggerDistance) {
			beginWaiting(target);
		}
	}

	private void tickWaiting(@Nullable ServerPlayer target) {
		if (!isValidMazeTarget(target)) {
			beginFleeing();
			return;
		}
		if (target != null) {
			this.getLookControl().setLookAt(target, 30.0F, 30.0F);
		}
		waitingTicks++;
		if (waitingTicks >= OFFER_TIMEOUT_TICKS) {
			beginFleeing();
		}
	}

	private void tickFleeing(@Nullable ServerPlayer target) {
		fleeTicks++;
		if (fleeTicks > MazeConfig.notexturemanFleeDurationTicks) {
			discard();
			return;
		}
		if (target != null && distanceTo(target) > FLEE_DESPAWN_DISTANCE) {
			discard();
		}
	}

	private boolean isValidMazeTarget(@Nullable ServerPlayer target) {
		if (target == null || !target.isAlive() || target.isSpectator()) {
			return false;
		}
		return MazeDimensions.isMazeDimension(target.level().dimension().location());
	}

	private void beginWaiting(ServerPlayer target) {
		behaviorState = STATE_WAITING;
		waitingTicks = 0;
		targetPlayer = target.getUUID();
		offerLevel = ThreadLocalRandom.current().nextInt(1, MazeConfig.notexturemanMaxLevel + 1);
		getNavigation().stop();
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> target),
				new NotexturemanOfferPacket(getId(), offerLevel));
	}

	public void beginFleeing() {
		behaviorState = STATE_FLEEING;
		fleeTicks = 0;
		getNavigation().stop();
	}

	public void handlePlayerResponse(ServerPlayer player, boolean accept) {
		if (behaviorState != STATE_WAITING) {
			return;
		}
		if (targetPlayer == null || !targetPlayer.equals(player.getUUID())) {
			return;
		}
		if (accept) {
			MazeTeleportUtil.teleportToMaze(player, offerLevel);
			discard();
			return;
		}
		beginFleeing();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("behaviorState", behaviorState);
		if (targetPlayer != null) {
			tag.putUUID("targetPlayer", targetPlayer);
		}
		tag.putInt("offerLevel", offerLevel);
		tag.putInt("waitingTicks", waitingTicks);
		tag.putInt("fleeTicks", fleeTicks);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("behaviorState")) {
			behaviorState = tag.getInt("behaviorState");
		}
		if (tag.hasUUID("targetPlayer")) {
			targetPlayer = tag.getUUID("targetPlayer");
		}
		if (tag.contains("offerLevel")) {
			offerLevel = tag.getInt("offerLevel");
		}
		if (tag.contains("waitingTicks")) {
			waitingTicks = tag.getInt("waitingTicks");
		}
		if (tag.contains("fleeTicks")) {
			fleeTicks = tag.getInt("fleeTicks");
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.FOLLOW_RANGE, 32.0D);
	}
}
