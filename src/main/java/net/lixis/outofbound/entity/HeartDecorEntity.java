package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis9.eventjar.init.EventjarModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class HeartDecorEntity extends Entity {

	public static final String MANAGED_TAG = "outofbound_heart_decor";

	public static final int BEAT_PERIOD_TICKS = 22;

	public HeartDecorEntity(PlayMessages.SpawnEntity packet, Level level) {
		this(OutofboundExtraEntities.HEART_DECOR.get(), level);
	}

	public HeartDecorEntity(EntityType<? extends HeartDecorEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide) {
			if (this.tickCount % BEAT_PERIOD_TICKS == 0) {
				this.level().playLocalSound(
						this.getX(), this.getY() + 0.5D, this.getZ(),
						EventjarModSounds.HEARTBEAT.get(),
						SoundSource.AMBIENT,
						0.9F,
						0.95F + this.random.nextFloat() * 0.1F,
						false);
			}
			return;
		}
		if (!MazeDimensions.isMazeDimension(this.level().dimension().location())) {
			this.discard();
			return;
		}
		this.setDeltaMovement(Vec3.ZERO);
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}
}
