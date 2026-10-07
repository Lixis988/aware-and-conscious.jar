package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeTeleportUtil;
import net.lixis.outofbound.entity.ai.ChaseNearestPlayerGoal;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis.outofbound.util.ScareSoundUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

public class UnknownEntity extends Monster {

	public static final String MANAGED_TAG = "outofbound_unknown_managed";

	private static final double CATCH_DISTANCE = 1.5D;
	private static final int SOUND_INTERVAL = 60;
	private static final int LIFETIME_TICKS = 12_000;

	private int soundCooldown;
	private int age;
	private boolean catchTriggered;

	public UnknownEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundExtraEntities.UNKNOWN.get(), world);
	}

	public UnknownEntity(EntityType<? extends UnknownEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(2.0F);
		xpReward = 0;
		setNoAi(false);
		setPersistenceRequired();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ChaseNearestPlayerGoal(this));
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
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public void playerTouch(Player player) {
		triggerCatch(player);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide) {
			return;
		}

		age++;
		if (age >= LIFETIME_TICKS) {
			discard();
			return;
		}

		if (this.tickCount % 10 == 0) {
			Player nearest = findNearestPlayer();
			if (nearest != null && this.distanceTo(nearest) <= CATCH_DISTANCE) {
				triggerCatch(nearest);
				return;
			}
		}

		if (soundCooldown > 0) {
			soundCooldown--;
			return;
		}
		soundCooldown = SOUND_INTERVAL;
		Player nearest = findNearestPlayer();
		if (nearest instanceof ServerPlayer serverPlayer) {
			ScareSoundUtil.playRandomNoise(serverPlayer);
		}
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (!this.level().isClientSide) {
			Vec3 chase = ChaserMovement.chaseTravelInput(this);
			if (chase != null) {
				super.travel(chase);
				ChaserMovement.afterChaseTravel(this);
				return;
			}
		}
		super.travel(travelVector);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.95)
				.add(Attributes.MAX_HEALTH, 40)
				.add(Attributes.ARMOR, 0)
				.add(Attributes.ATTACK_DAMAGE, 0)
				.add(Attributes.FOLLOW_RANGE, 500);
	}

	private void triggerCatch(Player player) {
		if (this.level().isClientSide || this.isRemoved() || catchTriggered) {
			return;
		}
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		catchTriggered = true;
		long currentIndex = serverPlayer.getPersistentData().getLong("outofbound_maze_index");
		MazeTeleportUtil.teleportToRandomMaze(serverPlayer, currentIndex);
		discard();
	}

	private Player findNearestPlayer() {
		if (!(this.level() instanceof ServerLevel serverLevel)) {
			return this.level().getNearestPlayer(this, 500.0D);
		}
		ServerPlayer nearest = null;
		double nearestSq = Double.MAX_VALUE;
		for (ServerPlayer player : serverLevel.players()) {
			if (!player.isAlive() || player.isSpectator()) {
				continue;
			}
			double distSq = this.distanceToSqr(player);
			if (distSq < nearestSq) {
				nearestSq = distSq;
				nearest = player;
			}
		}
		return nearest;
	}
}
