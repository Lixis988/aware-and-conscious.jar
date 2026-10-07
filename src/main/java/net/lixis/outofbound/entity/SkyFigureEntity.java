package net.lixis.outofbound.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class SkyFigureEntity extends Entity {

	public static final String MANAGED_TAG = "outofbound_sky_figure_managed";

	private static final EntityDataAccessor<Integer> FIGURE_TYPE = SynchedEntityData.defineId(SkyFigureEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> FIGURE_SIZE = SynchedEntityData.defineId(SkyFigureEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> VARIANT_SEED = SynchedEntityData.defineId(SkyFigureEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Long> SPAWN_TICK = SynchedEntityData.defineId(SkyFigureEntity.class,
			EntityDataSerializers.LONG);

	public SkyFigureEntity(EntityType<? extends SkyFigureEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.setNoGravity(true);
		this.setInvulnerable(true);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(FIGURE_TYPE, 0);
		this.entityData.define(FIGURE_SIZE, 4);
		this.entityData.define(VARIANT_SEED, 0);
		this.entityData.define(SPAWN_TICK, 0L);
	}

	public void configure(int figureTypeOrdinal, int size, int variantSeed, long spawnTick) {
		this.entityData.set(FIGURE_TYPE, figureTypeOrdinal);
		this.entityData.set(FIGURE_SIZE, size);
		this.entityData.set(VARIANT_SEED, variantSeed);
		this.entityData.set(SPAWN_TICK, spawnTick);
	}

	public int getFigureTypeOrdinal() {
		return this.entityData.get(FIGURE_TYPE);
	}

	public int getFigureSize() {
		return this.entityData.get(FIGURE_SIZE);
	}

	public int getVariantSeed() {
		return this.entityData.get(VARIANT_SEED);
	}

	public long getSpawnTick() {
		return this.entityData.get(SPAWN_TICK);
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide) {
			return;
		}
		long lifespan = net.lixis.outofbound.dimension.MazeConfig.skyFigureLifespanTicks;
		if (level().getGameTime() - getSpawnTick() > lifespan) {
			discard();
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (tag.contains("figureType")) {
			this.entityData.set(FIGURE_TYPE, tag.getInt("figureType"));
		}
		if (tag.contains("figureSize")) {
			this.entityData.set(FIGURE_SIZE, tag.getInt("figureSize"));
		}
		if (tag.contains("variantSeed")) {
			this.entityData.set(VARIANT_SEED, tag.getInt("variantSeed"));
		}
		if (tag.contains("spawnTick")) {
			this.entityData.set(SPAWN_TICK, tag.getLong("spawnTick"));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("figureType", getFigureTypeOrdinal());
		tag.putInt("figureSize", getFigureSize());
		tag.putInt("variantSeed", getVariantSeed());
		tag.putLong("spawnTick", getSpawnTick());
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
