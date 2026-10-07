package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeTeleportUtil;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
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
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OverworldNotexturemanEntity extends net.minecraft.world.entity.PathfinderMob {

	public static final String MANAGED_TAG = "outofbound_overworld_notextureman_managed";

	private static final int LIFETIME_TICKS = 3600;
	private static final int MIN_MAZE_INDEX = 1;
	private static final int MAX_MAZE_INDEX = 1000;

	private static final float WIDTH = 0.9F;
	private static final float HEIGHT = 2.0F;
	private static final EntityDimensions SIZE = EntityDimensions.scalable(WIDTH, HEIGHT);

	@Nullable
	private UUID targetPlayer;
	private int age;

	public OverworldNotexturemanEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundExtraEntities.OVERWORLD_NOTEXTUREMAN.get(), world);
	}

	public OverworldNotexturemanEntity(EntityType<? extends OverworldNotexturemanEntity> type, Level world) {
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

	@Nullable
	public UUID getTargetPlayer() {
		return targetPlayer;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide) {
			return;
		}
		age++;
		if (age >= LIFETIME_TICKS) {
			discard();
		}
	}

	public void acceptAndTeleport(ServerPlayer player) {
		if (targetPlayer == null || !targetPlayer.equals(player.getUUID())) {
			return;
		}
		int index = ThreadLocalRandom.current().nextInt(MIN_MAZE_INDEX, MAX_MAZE_INDEX + 1);
		MazeTeleportUtil.teleportToMaze(player, index);
		discard();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (targetPlayer != null) {
			tag.putUUID("targetPlayer", targetPlayer);
		}
		tag.putInt("age", age);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.hasUUID("targetPlayer")) {
			targetPlayer = tag.getUUID("targetPlayer");
		}
		if (tag.contains("age")) {
			age = tag.getInt("age");
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.0D)
				.add(Attributes.FOLLOW_RANGE, 32.0D);
	}
}
