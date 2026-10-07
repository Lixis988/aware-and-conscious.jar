package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import net.lixis9.eventjar.init.EventjarModEntities;

import javax.annotation.Nullable;
import java.util.List;

public class EntityCentipedeEntity extends Monster {
    private boolean isActivated = false;
    private static final double ACTIVATION_RANGE = 10.0D;
    private static final double DESTROY_BLOCK_RANGE = 1.5D;

    public EntityCentipedeEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EventjarModEntities.ENTITY_CENTIPEDE.get(), world);
    }

    public EntityCentipedeEntity(EntityType<? extends EntityCentipedeEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(0.1f);
        xpReward = 0;
        setNoAi(false);
        setCustomName(Component.literal("Centipede"));
        setCustomNameVisible(true);
        setPersistenceRequired();
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isActivated) {
            this.checkForActivation();
        } else {
            this.destroyBlocksInPath();
        }
    }

    private void checkForActivation() {
        if (this.level().isClientSide) return;

        List<Player> players = this.level().getEntitiesOfClass(Player.class,
                new AABB(this.getX() - ACTIVATION_RANGE, this.getY() - ACTIVATION_RANGE, this.getZ() - ACTIVATION_RANGE,
                        this.getX() + ACTIVATION_RANGE, this.getY() + ACTIVATION_RANGE, this.getZ() + ACTIVATION_RANGE));

        for (Player player : players) {
            if (player.distanceToSqr(this) <= ACTIVATION_RANGE * ACTIVATION_RANGE) {
                this.activate();
                break;
            }
        }
    }

    private void activate() {
        this.isActivated = true;
        this.setSpeed(1.5F);

        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5D, true));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.6F));
    }

    private void destroyBlocksInPath() {
        if (this.level().isClientSide) return;

        BlockPos headPos = this.blockPosition().relative(this.getDirection(), (int) DESTROY_BLOCK_RANGE);
        BlockState blockState = this.level().getBlockState(headPos);

        if (!blockState.isAir() && blockState.getDestroySpeed(this.level(), headPos) >= 0f &&
                blockState.getDestroySpeed(this.level(), headPos) <= 5.0F) {
            this.level().destroyBlock(headPos, true, this);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (!this.isActivated) {
            this.activate();
        }
        return super.hurt(source, amount);
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        if (this.isActivated) {
            super.travel(travelVector);
        } else {

            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 200)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        return builder;
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world);
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
}
