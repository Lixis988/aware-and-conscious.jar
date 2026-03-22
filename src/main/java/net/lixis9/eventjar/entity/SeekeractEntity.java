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
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
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

import net.lixis9.eventjar.procedures.SeekeractPriStolknovieniiIghrokaSSushchnostiuProcedure;
import net.lixis9.eventjar.procedures.SeekeractPriObnovlieniiTikaSushchnostiProcedure;
import net.lixis9.eventjar.procedures.SeekeractPriNachalnomPrizyvieSushchnostiProcedure;
import net.lixis9.eventjar.init.EventjarModEntities;

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
        setCustomName(Component.literal("Seeker"));
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
        // Разрешаем открывать двери (если нужно)
        this.getNavigation().getNodeEvaluator().setCanOpenDoors(true);

        // Агрессивное ближнее столкновение — высокая скорость атаки
        // Приоритеты: 0 - самый высокий. MeleeAttackGoal должен быть одним из первых,
        // чтобы сущность шла прямо в бой.
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.9D, true)); // скорость подхода 1.9, longMemory=true

        // Смотрит на игрока (меньший приоритет, чтобы не мешать атаке)
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, ServerPlayer.class, 8f));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8f));

        // Двери/возврат в деревню/плавать — более низкий приоритет
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(6, new MoveBackToVillageGoal(this, 0.8, false));
        this.goalSelector.addGoal(7, new FloatGoal(this));

        // Цели: реагировать на атаку (HurtByTarget) и целиться в игрока
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

    /**
     * Override для кастомного звука шагов.
     * Предполагается, что steps_seeker.ogg зарегистрирован как
     * sound event с идентификатором "eventjar:steps_seeker".
     */
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundEvent stepSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:steps_seeker"));
        if (stepSound != null) {
            this.playSound(stepSound, 0.25F, 1.0F); // немного громче шаги
        } else {
            super.playStepSound(pos, state);
        }
    }

    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        // Сохраняем основные иммунитеты, но разрешаем урон от игрока — чтобы игрок мог драться с сущностью.
        if (damagesource.is(DamageTypes.IN_FIRE)) return false;
        if (damagesource.getDirectEntity() instanceof AbstractArrow) return false;
        // removed player immunity so entity can be damaged by players and react (agressive)
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
        SeekeractPriStolknovieniiIghrokaSSushchnostiuProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        // Очень быстрая сущность:
        builder = builder.add(Attributes.MOVEMENT_SPEED, 1.2); // можно уменьшить до 0.8–1.0, если слишком быстро
        builder = builder.add(Attributes.MAX_HEALTH, 20);     // немного больше HP
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 112); // оставил вашу большую атаку
        builder = builder.add(Attributes.FOLLOW_RANGE, 2000); // длинный радиус слежения
        return builder;
    }
}
