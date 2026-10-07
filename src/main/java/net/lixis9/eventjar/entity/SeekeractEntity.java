package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

import net.lixis9.eventjar.procedures.SeekerCatchProcedure;
import net.lixis9.eventjar.procedures.SeekeractPriObnovlieniiTikaSushchnostiProcedure;
import net.lixis9.eventjar.procedures.SeekeractPriNachalnomPrizyvieSushchnostiProcedure;
import net.lixis9.eventjar.network.SeekerCatchShakePacket;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

public class SeekeractEntity extends Spider {
    public SeekeractEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EventjarModEntities.SEEKERACT.get(), world);
    }

    public SeekeractEntity(EntityType<SeekeractEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(0.7f);
        xpReward = 0;
        setNoAi(false);
        setCustomName(Component.literal("§k§4seeker"));
        setCustomNameVisible(false);
        setPersistenceRequired();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.getNavigation().getNodeEvaluator().setCanOpenDoors(true);

        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 2.15D, true) {
            @Override
            protected double getAttackReachSqr(net.minecraft.world.entity.LivingEntity entity) {
                return (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) * 3.5D;
            }
        });
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, ServerPlayer.class, 48f));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 48f));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(7, new FloatGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ServerPlayer.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundEvent stepSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:steps_seeker"));
        if (stepSound != null) {
            this.playSound(stepSound, 0.25F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }
    }

    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        if (damagesource.is(DamageTypes.IN_FIRE)) return false;
        if (damagesource.getDirectEntity() instanceof AbstractArrow) return false;
        if (damagesource.getDirectEntity() instanceof Player) return false;
        if (damagesource.getDirectEntity() instanceof ThrownPotion || damagesource.getDirectEntity() instanceof AreaEffectCloud)
            return false;
        if (damagesource.is(DamageTypes.FALL)) return false;
        if (damagesource.is(DamageTypes.CACTUS)) return false;
        if (damagesource.is(DamageTypes.DROWN)) return false;
        if (damagesource.is(DamageTypes.LIGHTNING_BOLT)) return false;
        if (damagesource.is(DamageTypes.EXPLOSION) || damagesource.is(DamageTypes.PLAYER_EXPLOSION)) return false;
        if (damagesource.is(DamageTypes.TRIDENT)) return false;
        if (damagesource.is(DamageTypes.FALLING_ANVIL)) return false;
        if (damagesource.is(DamageTypes.DRAGON_BREATH)) return false;
        if (damagesource.is(DamageTypes.WITHER) || damagesource.is(DamageTypes.WITHER_SKULL)) return false;
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        SeekeractPriNachalnomPrizyvieSushchnostiProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
        return retval;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        SeekeractPriObnovlieniiTikaSushchnostiProcedure.execute(this);
    }

    @Override
    public void playerTouch(Player sourceentity) {
        super.playerTouch(sourceentity);
        SeekerCatchProcedure.execute(this, sourceentity);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (target instanceof Player player) {
            SeekerCatchProcedure.execute(this, player);
            return true;
        }
        return super.doHurtTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            Player nearest = this.level().getNearestPlayer(this, 128.0D);
            if (nearest != null && nearest.isAlive() && !nearest.isSpectator()) {
                this.setTarget(nearest);
                double dist = this.distanceTo(nearest);
                double speed = dist > 16.0D ? 2.35D : dist > 8.0D ? 2.05D : 1.85D;
                this.getNavigation().moveTo(nearest, speed);
                this.getLookControl().setLookAt(nearest, 40.0F, 40.0F);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            SeekerCatchShakePacket.sendToAll(serverLevel, false);
        }
        super.remove(reason);
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 1.55);
        builder = builder.add(Attributes.MAX_HEALTH, 120);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 40);
        builder = builder.add(Attributes.FOLLOW_RANGE, 2000);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
        return builder;
    }
}
