package net.lixis.outofbound.entity;

import net.lixis.outofbound.entity.ai.ChaseNearestPlayerGoal;
import net.lixis.outofbound.init.OutofboundModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractChaserEntity extends Monster implements ChaserEntity {

	private int alternateStep;
	private float stepAccumulator;

	protected AbstractChaserEntity(EntityType<? extends Monster> type, Level world) {
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
		ChaserRevealHandler.markRevealed(this);
		super.playerTouch(player);
		this.doHurtTarget(player);
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

	@Override
	public void travel(Vec3 travelVector) {
		if (!this.level().isClientSide) {
			Vec3 chase = ChaserMovement.chaseTravelInput(this);
			if (chase != null) {
				super.travel(chase);
				ChaserMovement.afterChaseTravel(this);
				ChaserMovement.tryMelee(this);
				return;
			}
		}
		super.travel(travelVector);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide || !this.onGround()) {
			return;
		}
		Vec3 delta = this.position().subtract(this.xOld, this.yOld, this.zOld);
		double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
		if (horizontal < 0.01D) {
			return;
		}
		stepAccumulator += (float) horizontal;
		if (stepAccumulator >= 1.35F) {
			stepAccumulator = 0.0F;
			BlockPos pos = BlockPos.containing(this.getX(), this.getY() - 0.2D, this.getZ());
			this.playStepSound(pos, this.level().getBlockState(pos));
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		SoundEvent step = (alternateStep++ & 1) == 0 ? OutofboundModSounds.STEP1.get() : OutofboundModSounds.STEP2.get();
		this.playSound(step, 1.0F, 1.0F);
	}

	public static AttributeSupplier.Builder createChaserAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.95);
		builder = builder.add(Attributes.MAX_HEALTH, 40);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 500);
		return builder;
	}
}
