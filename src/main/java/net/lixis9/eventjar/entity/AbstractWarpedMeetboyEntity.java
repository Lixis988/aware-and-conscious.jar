package net.lixis9.eventjar.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractWarpedMeetboyEntity extends Monster {

	public static final String HUNT_TAG = "warped_meetboy_hunt";
	private static final double RUSH_SPEED = 1.35D;

	private static final double FLEE_DESPAWN_DIST = 48.0D;

	protected AbstractWarpedMeetboyEntity(EntityType<? extends AbstractWarpedMeetboyEntity> type, Level world, String displayName) {
		super(type, world);
		setMaxUpStep(1.2f);
		xpReward = 0;
		setNoAi(false);
		setCustomName(Component.literal(displayName));
		setCustomNameVisible(true);
		setPersistenceRequired();
	}

	public void markAsHunt() {
		this.getPersistentData().putBoolean(HUNT_TAG, true);
	}

	public boolean isHunt() {
		return this.getPersistentData().getBoolean(HUNT_TAG);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.8D, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) * 4.0D;
			}
		});
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 48.0F));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return isHunt() && distanceToClosestPlayer > FLEE_DESPAWN_DIST;
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
		if (damagesource.is(DamageTypes.CACTUS))
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
	public void tick() {
		super.tick();

		if (!this.level().isClientSide) {
			if (isHunt() && this.tickCount % 10 == 0) {
				Player nearest = this.level().getNearestPlayer(this, FLEE_DESPAWN_DIST + 8.0D);
				if (nearest == null || !nearest.isAlive() || this.distanceTo(nearest) > FLEE_DESPAWN_DIST) {
					this.discard();
					return;
				}
			}

			LivingEntity target = this.getTarget();
			if (target == null || !target.isAlive()) {
				Player nearest = this.level().getNearestPlayer(this, 64.0D);
				if (nearest != null && nearest.isAlive()) {
					this.setTarget(nearest);
					target = nearest;
				} else if (isHunt()) {
					this.discard();
					return;
				}
			}
			if (target != null && target.isAlive()) {
				this.getLookControl().setLookAt(target, 50.0F, 50.0F);
				this.getNavigation().moveTo(target, 1.8D);

				double dx = target.getX() - this.getX();
				double dy = target.getY() - this.getY();
				double dz = target.getZ() - this.getZ();
				double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
				if (dist > 0.5D) {
					Vec3 rush = new Vec3(dx, dy * 0.35D, dz).normalize().scale(RUSH_SPEED * 0.35D);
					this.setDeltaMovement(this.getDeltaMovement().add(rush));
					this.hurtMarked = true;
				}
			}
		} else if (this.tickCount % 2 == 0) {
			float snap = ((this.tickCount + this.getId()) % 41 < 3) ? 10.0F : 2.5F;
			this.yHeadRot = this.getYRot() + (this.getRandom().nextFloat() - 0.5F) * snap;
		}
	}

	public static AttributeSupplier.Builder createWarpedAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 1.15D);
		builder = builder.add(Attributes.MAX_HEALTH, 40);
		builder = builder.add(Attributes.ARMOR, 4);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1.5D);
		builder = builder.add(Attributes.ATTACK_SPEED, 2.5D);
		builder = builder.add(Attributes.FOLLOW_RANGE, 80);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
		return builder;
	}
}
