package net.lixis9.eventjar.entity;

import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

public class MimicTendrilEntity extends Monster {

	private int lifeTicks;

	public MimicTendrilEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(EventjarModEntities.MIMIC_TENDRIL.get(), world);
	}

	public MimicTendrilEntity(EntityType<? extends MimicTendrilEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(1.0f);
		xpReward = 0;
		setNoAi(false);
		setPersistenceRequired();
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.6D, true));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 32.0F));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)
				|| source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)
				|| source.getDirectEntity() instanceof Player) {
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	public void tick() {
		super.tick();
		lifeTicks++;
		if (!this.level().isClientSide) {
			if (lifeTicks > 200) {
				this.discard();
				return;
			}
			LivingEntity target = this.getTarget();
			if (target == null || !target.isAlive()) {
				Player nearest = this.level().getNearestPlayer(this, 48.0D);
				if (nearest != null) {
					this.setTarget(nearest);
					target = nearest;
				}
			}
			if (target != null && target.isAlive()) {
				this.getNavigation().moveTo(target, 1.7D);
				double dx = target.getX() - this.getX();
				double dy = target.getY() - this.getY();
				double dz = target.getZ() - this.getZ();
				double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
				if (dist > 0.4D) {
					Vec3 push = new Vec3(dx, dy * 0.2D, dz).normalize().scale(0.28D);
					this.setDeltaMovement(this.getDeltaMovement().add(push));
					this.hurtMarked = true;
				}
			}
		}
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 1.05D)
				.add(Attributes.MAX_HEALTH, 80.0D)
				.add(Attributes.ATTACK_DAMAGE, 20.0D)
				.add(Attributes.FOLLOW_RANGE, 64.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}
}
