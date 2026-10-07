package net.lixis.outofbound.entity;

import net.lixis.outofbound.entity.ai.ChaseNearestPlayerGoal;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis.outofbound.registry.OutofboundExtraSounds;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

public class BlackSquareEntity extends Monster {

	public static final String MANAGED_TAG = "outofbound_black_square_managed";

	private static final double CATCH_DISTANCE = 1.2D;
	private static final int SOUND_INTERVAL = 40;

	private int soundCooldown;
	private boolean catchTriggered;

	public BlackSquareEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(OutofboundExtraEntities.BLACK_SQUARE.get(), world);
	}

	public BlackSquareEntity(EntityType<? extends BlackSquareEntity> type, Level world) {
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
		triggerCatch();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide) {
			return;
		}

		if (this.tickCount % 10 == 0) {
			Player nearest = findNearestPlayer();
			if (nearest != null && this.distanceTo(nearest) <= CATCH_DISTANCE) {
				triggerCatch();
				return;
			}
		}

		if (soundCooldown > 0) {
			soundCooldown--;
			return;
		}
		soundCooldown = SOUND_INTERVAL;
		SoundEvent sound = OutofboundExtraSounds.dimensionNoiseTrack(this.tickCount);
		this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, 0.9F, 0.85F + this.random.nextFloat() * 0.3F);
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
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.95);
		builder = builder.add(Attributes.MAX_HEALTH, 40);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 500);
		return builder;
	}

	private void triggerCatch() {
		if (this.level().isClientSide || this.isRemoved() || catchTriggered) {
			return;
		}
		catchTriggered = true;
		BlackSquareTeleportHandler.teleportAllPlayers(this.level().getServer());
		this.discard();
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
