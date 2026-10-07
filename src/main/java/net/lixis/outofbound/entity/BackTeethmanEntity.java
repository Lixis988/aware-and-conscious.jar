package net.lixis.outofbound.entity;

import net.lixis.outofbound.util.PlayerLookUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class BackTeethmanEntity extends Entity {

	private static final String TARGET_TAG = "outofbound_back_teethman_target";
	private static final double BEHIND_DISTANCE = 4.5D;
	private static final double LOOK_RANGE = 64.0D;
	private static final double LOOK_DOT_THRESHOLD = 0.85D;
	private static final int GRACE_TICKS = 20;
	private static final double REPOSITION_DISTANCE = 8.0D;

	private UUID targetUuid;

	public BackTeethmanEntity(EntityType<? extends BackTeethmanEntity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.setNoGravity(true);
		this.setInvulnerable(true);
	}

	public void setTarget(UUID uuid) {
		this.targetUuid = uuid;
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide) {
			return;
		}

		if (!(level() instanceof ServerLevel serverLevel)) {
			return;
		}

		ServerPlayer player = resolveTarget(serverLevel);
		if (player == null || !player.isAlive() || player.isSpectator() || player.level() != serverLevel) {
			discard();
			return;
		}
		this.targetUuid = player.getUUID();

		if (tickCount >= GRACE_TICKS && isPlayerLookingBack(player)) {
			net.lixis.outofbound.util.ScareSoundUtil.playRandomNoise(player);
			vanishWithSmoke(serverLevel);
			return;
		}

		double distSq = this.distanceToSqr(player);
		boolean needsReposition = tickCount == 0
				|| distSq > REPOSITION_DISTANCE * REPOSITION_DISTANCE;
		if (needsReposition) {
			updateBehindPosition(serverLevel, player);
		} else {
			faceToward(player);
		}
		setDeltaMovement(Vec3.ZERO);
	}

	private void faceToward(ServerPlayer player) {
		float yaw = yawToward(player, getX(), getY(), getZ());
		setYRot(yaw);
		setYHeadRot(yaw);
	}

	private ServerPlayer resolveTarget(ServerLevel serverLevel) {
		if (targetUuid != null) {
			Entity existing = serverLevel.getEntity(targetUuid);
			if (existing instanceof ServerPlayer player) {
				return player;
			}
		}
		return (ServerPlayer) serverLevel.getNearestPlayer(this, 64.0D);
	}

	private void updateBehindPosition(ServerLevel level, ServerPlayer player) {
		float yawRad = player.getYRot() * ((float) Math.PI / 180.0F);
		double behindX = player.getX() + Math.sin(yawRad) * BEHIND_DISTANCE;
		double behindZ = player.getZ() - Math.cos(yawRad) * BEHIND_DISTANCE;
		int blockX = Mth.floor(behindX);
		int blockZ = Mth.floor(behindZ);

		double spawnY = player.getY();
		behindX = blockX + 0.5D;
		behindZ = blockZ + 0.5D;

		if (level.hasChunk(blockX >> 4, blockZ >> 4)) {
			int playerY = player.getBlockY();
			boolean found = false;
			for (int dy = 0; dy <= 4 && !found; dy++) {
				for (int sign = 0; sign < 2; sign++) {
					int y = playerY + (sign == 0 ? -dy : dy);
					if (dy == 0 && sign == 1) {
						continue;
					}
					BlockPos feet = new BlockPos(blockX, y, blockZ);
					BlockPos ground = feet.below();
					BlockPos head = feet.above();
					if (OverworldStalkerSpawnUtil.isValidSpawnSpace(level, ground, feet, head)) {
						spawnY = feet.getY();
						found = true;
						break;
					}
				}
			}
			if (!found) {

				return;
			}
		} else {
			return;
		}

		float yaw = yawToward(player, behindX, spawnY, behindZ);
		setPos(behindX, spawnY, behindZ);
		setYRot(yaw);
		setYHeadRot(yaw);
		setDeltaMovement(Vec3.ZERO);
	}

	private static float yawToward(ServerPlayer player, double x, double y, double z) {
		Vec3 delta = player.position().subtract(x, y, z);
		return (float) (Mth.atan2(-delta.x, delta.z) * (180.0D / Math.PI));
	}

	private boolean isPlayerLookingBack(ServerPlayer player) {
		if (!player.hasLineOfSight(this)) {
			return false;
		}
		if (!PlayerLookUtil.isLookingAt(player, this, LOOK_RANGE, LOOK_DOT_THRESHOLD)) {
			return false;
		}
		Vec3 look = player.getViewVector(1.0F);
		Vec3 toEntity = position().subtract(player.getEyePosition()).normalize();
		return look.dot(toEntity) > 0.0D;
	}

	private void vanishWithSmoke(ServerLevel level) {
		double centerX = getX();
		double centerY = getY() + getBbHeight() * 0.5D;
		double centerZ = getZ();
		level.sendParticles(ParticleTypes.LARGE_SMOKE, centerX, centerY, centerZ, 18, 0.35D, 0.5D, 0.35D, 0.02D);
		level.sendParticles(ParticleTypes.SMOKE, centerX, centerY, centerZ, 12, 0.25D, 0.35D, 0.25D, 0.01D);
		discard();
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (tag.hasUUID(TARGET_TAG)) {
			this.targetUuid = tag.getUUID(TARGET_TAG);
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		if (targetUuid != null) {
			tag.putUUID(TARGET_TAG, targetUuid);
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
