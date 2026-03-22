package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.procedures.ErrundefinePriStolknovieniiIghrokaSSushchnostiuProcedure;
import net.lixis9.eventjar.procedures.ErrundefinePriNachalnomPrizyvieSushchnostiProcedure;
import net.lixis9.eventjar.procedures.ErrundefinePriGibieliOtEtoiSushchnostiDrughoiProcedure;
import net.lixis9.eventjar.init.EventjarModEntities;

import javax.annotation.Nullable;

public class ErrundefineEntity extends Creeper {
    public ErrundefineEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EventjarModEntities.ERRUNDEFINE.get(), world);
    }

    public ErrundefineEntity(EntityType<ErrundefineEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(0.6f);
        xpReward = 0;
        setNoAi(false);
        setCustomName(Component.literal("err.undefine"));
        setCustomNameVisible(true);
        setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 20, true); // Увеличена скорость полета
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, world);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Агрессивные цели
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true)); // Всегда атакует игроков
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        
        // Цели поведения
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0D, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 6.0; // Увеличенный радиус атаки
            }
        });
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.5D, 20) {
            @Override
            public boolean canUse() {
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new FloatGoal(this));
        
        // Удаляем цели по удалению блоков, так как они не подходят для летающей сущности
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED; // Изменено на UNDEFINED для большей таинственности
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:heartbeat"));
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:steps_seeker")), 0.15f, 1);
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
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        // Сделаем сущность уязвимой только к магическому урону
        if (damagesource.is(DamageTypes.MAGIC)) {
            return super.hurt(damagesource, amount * 2.0F); // Двойной урон от магии
        }
        if (damagesource.is(DamageTypes.INDIRECT_MAGIC)) {
            return super.hurt(damagesource, amount);
        }
        // Устойчивость к другим типам урона
        return super.hurt(damagesource, amount * 0.2F); // 80% сопротивления к остальным типам урона
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
        ErrundefinePriNachalnomPrizyvieSushchnostiProcedure.execute(world, this.getX(), this.getY(), this.getZ(), this);
        return retval;
    }

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
        super.awardKillScore(entity, score, damageSource);
        ErrundefinePriGibieliOtEtoiSushchnostiDrughoiProcedure.execute(entity);
    }

    @Override
    public void playerTouch(Player sourceentity) {
        super.playerTouch(sourceentity);
        ErrundefinePriStolknovieniiIghrokaSSushchnostiuProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
        
        // Случайные частицы для эффекта нестабильности
        if (this.level().isClientSide && this.random.nextInt(5) == 0) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.PORTAL,
                    this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                    this.getY() + this.random.nextDouble() * this.getBbHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                    (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(),
                    (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // Проверяем, есть ли живые игроки в мире
            boolean hasLivingPlayers = false;
            for (Player player : this.level().players()) {
                if (player.isAlive() && !player.isDeadOrDying()) {
                    hasLivingPlayers = true;
                    break;
                }
            }
            
            // Если нет живых игроков, удаляем сущность
            if (!hasLivingPlayers) {
                // Звук исчезновения
                SoundEvent deathSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:afterdeath"));
                if (deathSound != null) {
                    this.level().playSound(null, this.blockPosition(), deathSound, this.getSoundSource(), 0.6f, 0.8f);
                }
                this.discard();
                return;
            }
        }
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 1.8); // Значительно увеличена скорость
        builder = builder.add(Attributes.MAX_HEALTH, 50); // Увеличено здоровье
        builder = builder.add(Attributes.ARMOR, 4);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 8); // Увеличен урон
        builder = builder.add(Attributes.FOLLOW_RANGE, 64); // Увеличен радиус преследования
        builder = builder.add(Attributes.FLYING_SPEED, 1.8); // Увеличена скорость полета
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.8); // Устойчивость к отталкиванию
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 1.5); // Отталкивание при атаке
        return builder;
    }
}