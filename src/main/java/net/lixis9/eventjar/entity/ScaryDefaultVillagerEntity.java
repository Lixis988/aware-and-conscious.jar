package net.lixis9.eventjar.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;

import net.lixis9.eventjar.init.EventjarModEntities;

public class ScaryDefaultVillagerEntity extends Monster {
    private boolean shouldDisappear = false;
    private int disappearTimer = 0;
    private int attackCooldown = 0;
    private int messageTimer = 0;

    public ScaryDefaultVillagerEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EventjarModEntities.SCARY_DEFAULT_VILLAGER.get(), world);
    }

    public ScaryDefaultVillagerEntity(EntityType<ScaryDefaultVillagerEntity> type, Level world) {
        super(type, world);
        setMaxUpStep(1.2f);
        xpReward = 0;
        setNoAi(false);
        setCustomName(Component.literal("Resource packs"));
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

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.5, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth() + 3.0;
            }

            @Override
            protected void checkAndPerformAttack(LivingEntity target, double distance) {
                double reach = this.getAttackReachSqr(target);
                if (distance <= reach && this.getTicksUntilNextAttack() <= 0) {
                    this.resetAttackCooldown();
                    this.mob.doHurtTarget(target);

                    if (!this.mob.level().isClientSide()) {
                        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mimicevent", "scarysoundmimic"));
                        if (sound != null) {
                            this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                                sound, SoundSource.HOSTILE, 1.0F, 1.0F);
                        }
                    }

                    if (target instanceof Player && !target.isAlive()) {
                        ((ScaryDefaultVillagerEntity) this.mob).markForDisappearance();
                    }
                }
            }
        });

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (!this.level().isClientSide() && this.isAlive() && !shouldDisappear) {
            messageTimer++;
            if (messageTimer >= 5) {
                messageTimer = 0;
                sendRandomChatMessage();
            }
        }

        if (shouldDisappear) {
            disappearTimer++;
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

            if (disappearTimer >= 20) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
        } else {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.getLookControl().setLookAt(target, 30.0F, 30.0F);

                if (this.distanceToSqr(target) > 4.0) {
                    double dx = target.getX() - this.getX();
                    double dz = target.getZ() - this.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    if (distance > 0) {
                        double speedMultiplier = 1.0;
                        this.setDeltaMovement(
                            this.getDeltaMovement().add(
                                dx / distance * 0.1 * speedMultiplier,
                                0,
                                dz / distance * 0.1 * speedMultiplier
                            )
                        );
                    }
                }
            }
        }
    }

    private void sendRandomChatMessage() {
        Player nearestPlayer = this.level().getNearestPlayer(this, 32);
        if (nearestPlayer != null) {
            String[] messageTexts = {"YOUR FAULT", "WHY", "YOU LEAVE ME", "HELP"};
            String text = messageTexts[this.random.nextInt(messageTexts.length)];

            Component message;
            int style = this.random.nextInt(6);

            switch (style) {
                case 0:
                    message = Component.literal("§c§l" + text);
                    break;
                case 1:
                    message = Component.literal("§4§l" + text);
                    break;
                case 2:
                    message = Component.literal("§c§n" + text);
                    break;
                case 3:
                    message = Component.literal("§4§o" + text);
                    break;
                case 4:
                    message = Component.literal("§c§k" + text);
                    break;
                default:
                    message = Component.literal("§c" + text);
                    break;
            }

            nearestPlayer.displayClientMessage(message, false);
        }
    }

    public void markForDisappearance() {
        this.shouldDisappear = true;
        this.disappearTimer = 0;
        this.goalSelector.getRunningGoals().forEach(g -> g.stop());
        this.targetSelector.getRunningGoals().forEach(g -> g.stop());
        this.setTarget(null);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);

        if (result && attackCooldown <= 0) {
            if (!this.level().isClientSide()) {
                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("mimicevent", "scarysoundmimic"));
                if (sound != null) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        sound, SoundSource.HOSTILE, 1.0F, 1.0F);
                }
            }
            attackCooldown = 20;
        }

        if (result && target instanceof Player && !target.isAlive()) {
            this.markForDisappearance();
        }

        return result;
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

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.6);
        builder = builder.add(Attributes.MAX_HEALTH, 100);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
        builder = builder.add(Attributes.FOLLOW_RANGE, 512);
        builder = builder.add(Attributes.ATTACK_KNOCKBACK, 2.0);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.9);
        return builder;
    }
}
