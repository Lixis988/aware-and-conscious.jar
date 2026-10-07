package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.init.EventjarModEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class FaultEntity extends Monster {
    private boolean isAggressive = false;
    @Nullable
    private UUID targetPlayerId = null;

    private static final int ACTIVATION_RANGE = 48;

    private static final int BLOCK_BREAK_RANGE = 4;

    private static final int BLOCK_CHECK_INTERVAL = 5;

    public FaultEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EventjarModEntities.FAULT.get(), world);
    }

    public FaultEntity(EntityType<FaultEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(1.0f);
        xpReward = 0;
        setNoAi(false);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {

                return (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) * 6;
            }

            @Override
            public boolean canUse() {
                return ((FaultEntity) this.mob).isAggressive && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return ((FaultEntity) this.mob).isAggressive && super.canContinueToUse();
            }
        });

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new FloatGoal(this));

        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false) {
            @Override
            public boolean canUse() {
                return ((FaultEntity) this.mob).isAggressive && super.canUse();
            }
        });
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public double getMyRidingOffset() {
        return -0.35D;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:breath1"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:afterdeath"));
    }

    @Override
    public SoundEvent getAmbientSound() {

        return null;
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
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public boolean isAggressive() {
        return this.isAggressive;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {

            boolean hasLivingPlayers = false;
            for (Player player : this.level().players()) {
                if (player.isAlive() && !player.isDeadOrDying()) {
                    hasLivingPlayers = true;
                    break;
                }
            }

            if (!hasLivingPlayers) {

                SoundEvent deathSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:afterdeath"));
                if (deathSound != null) {
                    this.level().playSound(null, this.blockPosition(), deathSound, this.getSoundSource(), 0.6f, 0.8f);
                }
                this.discard();
                return;
            }

            if (targetPlayerId != null) {
                Player targetPlayer = this.level().getPlayerByUUID(targetPlayerId);
                if (targetPlayer == null || !targetPlayer.isAlive()) {

                    SoundEvent deathSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:afterdeath"));
                    if (deathSound != null) {
                        this.level().playSound(null, this.blockPosition(), deathSound, this.getSoundSource(), 0.6f, 0.8f);
                    }
                    this.discard();
                    return;
                }
            } else if (!isAggressive) {
                Player nearestPlayer = this.level().getNearestPlayer(this, ACTIVATION_RANGE);
                if (nearestPlayer != null) {
                    this.getLookControl().setLookAt(nearestPlayer, 40.0F, 40.0F);
                    this.setYRot(this.getYHeadRot());
                    if (this.tickCount % 5 == 0 && StalkerLookUtil.isPlayerLookingAt(nearestPlayer, this)) {
                        activateAggression(nearestPlayer);
                    }
                }
            }

            if (!isAggressive && this.tickCount % 300 == 0 && random.nextFloat() < 0.5f) {
                SoundEvent breathSound = random.nextBoolean() ?
                    ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:breath1")) :
                    ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:breath2"));

                if (breathSound != null) {
                    this.level().playSound(null, this.blockPosition(), breathSound,
                        this.getSoundSource(), 0.6f, 1.0f + random.nextFloat() * 0.2f);
                }
            }

            LivingEntity target = this.getTarget();
            if (isAggressive && target != null) {
                if (this.tickCount % 2 == 0) {
                    this.getLookControl().setLookAt(target, 30.0F, 30.0F);

                    double dx = target.getX() - this.getX();
                    double dz = target.getZ() - this.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    if (distance > 1.0) {

                        dx /= distance;
                        dz /= distance;

                        double moveSpeed = 1.2;
                        this.setDeltaMovement(dx * moveSpeed, this.getDeltaMovement().y, dz * moveSpeed);

                        this.getNavigation().moveTo(target, moveSpeed);

                        this.moveTo(this.getX() + dx * 0.1, this.getY(), this.getZ() + dz * 0.1);

                        if (distance > 20.0) {
                            double teleportDistance = 15.0;
                            double teleportX = target.getX() - dx * teleportDistance;
                            double teleportZ = target.getZ() - dz * teleportDistance;
                            this.teleportTo(teleportX, this.getY(), teleportZ);
                        }
                    }
                }

                if (this.tickCount % 60 == 0) {
                    SoundEvent chaseSound = random.nextBoolean() ?
                        ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chase")) :
                        ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:chase2layer"));

                    if (chaseSound != null) {
                        this.level().playSound(null, this.blockPosition(), chaseSound,
                            this.getSoundSource(), 0.8f, 0.9f + random.nextFloat() * 0.2f);
                    }
                }

                if (this.tickCount % BLOCK_CHECK_INTERVAL == 0) {
                    breakBlocksInPath();
                }
            }
        }
    }

    private void breakBlocksInPath() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > BLOCK_BREAK_RANGE) return;

        dx /= distance;
        dz /= distance;

        for (int i = 1; i <= BLOCK_BREAK_RANGE; i++) {
            BlockPos checkPos = new BlockPos(
                (int)(this.getX() + dx * i),
                (int)this.getY(),
                (int)(this.getZ() + dz * i)
            );

            for (int y = -1; y <= 2; y++) {
                BlockPos pos = checkPos.above(y);
                BlockState state = this.level().getBlockState(pos);

                if (isBlockObstructingPath(pos, state)) {

                    this.level().destroyBlock(pos, false);
                    this.level().playSound(null, pos, SoundEvents.STONE_BREAK,
                        this.getSoundSource(), 0.3f, 1.0f);

                    this.level().addDestroyBlockEffect(pos, state);
                }
            }
        }
    }

    private boolean isBlockObstructingPath(BlockPos pos, BlockState state) {

        if (state.isAir()) return false;

        if (state.getBlock() == Blocks.BEDROCK ||
            state.getBlock() == Blocks.BARRIER ||
            state.getBlock() == Blocks.OBSIDIAN ||
            state.getBlock() == Blocks.CRYING_OBSIDIAN ||
            state.getDestroySpeed(this.level(), pos) >= 50.0f) {
            return false;
        }

        LivingEntity target = this.getTarget();
        if (target == null) return false;

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < 2.0) return false;

        double blockDistance = Math.sqrt(
            Math.pow(pos.getX() - this.getX(), 2) +
            Math.pow(pos.getZ() - this.getZ(), 2)
        );

        return blockDistance <= BLOCK_BREAK_RANGE &&
               Math.abs(pos.getY() - this.getY()) <= 2;
    }

    private void activateAggression(Player player) {
        this.isAggressive = true;
        this.targetPlayerId = player.getUUID();
        this.setTarget(player);

        var movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(1.5);
        }

        var attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(18);
        }

        SoundEvent appearanceSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:appearance"));
        if (appearanceSound != null) {
            this.level().playSound(null, this.blockPosition(), appearanceSound,
                this.getSoundSource(), 1.0f, 0.8f);
        }

        net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
                () -> () -> net.lixis9.eventjar.client.event.FaultGlitchHandler.triggerActivationGlitch());
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();

        builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
        builder = builder.add(Attributes.MAX_HEALTH, 15);
        builder = builder.add(Attributes.ARMOR, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 16);
        builder = builder.add(Attributes.FOLLOW_RANGE, 80);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 2.0);
        builder = builder.add(Attributes.ATTACK_SPEED, 3.0);
        return builder;
    }

    public static boolean checkSpawnRules(EntityType<FaultEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {

        return spawnType == MobSpawnType.MOB_SUMMONED || spawnType == MobSpawnType.EVENT || spawnType == MobSpawnType.SPAWN_EGG;
    }
}
