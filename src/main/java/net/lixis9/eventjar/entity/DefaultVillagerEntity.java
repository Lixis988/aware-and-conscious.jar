package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;

import net.lixis9.eventjar.procedures.DefaultVillagerPriObnovlieniiTikaSushchnostiProcedure;
import net.lixis9.eventjar.procedures.DefaultVillagerPriGibieliSushchnostiProcedure;
import net.lixis9.eventjar.init.EventjarModEntities;

public class DefaultVillagerEntity extends Monster {
    public DefaultVillagerEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EventjarModEntities.DEFAULT_VILLAGER.get(), world);
    }

    public DefaultVillagerEntity(EntityType<DefaultVillagerEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(0.6f);
        xpReward = 0;
        setNoAi(false);
        setCustomName(Component.literal("Default Villager ;3"));
        setCustomNameVisible(true);
        setPersistenceRequired();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.target != null;
            }
        });

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.5, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 9.0;
            }

            @Override
            public boolean canUse() {
                return this.mob.getTarget() != null && this.mob.distanceToSqr(this.mob.getTarget()) < 256;
            }
        });

        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.3) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.getTarget() == null;
            }
        });

        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FloatGoal(this));
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
    public void tick() {
        super.tick();

        Player nearestPlayer = this.level().getNearestPlayer(this, 64);
        if (nearestPlayer != null && this.getTarget() == null) {

            this.getLookControl().setLookAt(nearestPlayer, 30.0F, 30.0F);
            if (this.distanceToSqr(nearestPlayer) > 16.0) {
                double dx = nearestPlayer.getX() - this.getX();
                double dz = nearestPlayer.getZ() - this.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);

                if (distance > 0) {

                    this.setDeltaMovement(
                        this.getDeltaMovement().add(dx / distance * 0.03, 0, dz / distance * 0.03)
                    );
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

    @Override
    public void die(DamageSource source) {
        super.die(source);
        DefaultVillagerPriGibieliSushchnostiProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        DefaultVillagerPriObnovlieniiTikaSushchnostiProcedure.execute(this);
    }

    @Override
    public void playerTouch(Player sourceentity) {
        super.playerTouch(sourceentity);
        DefaultVillagerPriGibieliSushchnostiProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 50);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 0.1);
        return builder;
    }
}
