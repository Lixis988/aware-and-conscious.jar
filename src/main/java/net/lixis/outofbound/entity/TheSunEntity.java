package net.lixis.outofbound.entity;

import net.lixis.outofbound.dimension.MazeTeleportUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class TheSunEntity extends Entity {

	private static final String TARGET_TAG = "outofbound_the_sun_target";
	private static final double OFFSET_ABOVE_HEAD = 4.5D;
	private static final double LOOK_DOT_THRESHOLD = 0.94D;
	private static final int GRACE_TICKS = 30;

	private UUID targetUuid;

	public TheSunEntity(EntityType<? extends TheSunEntity> type, Level level) {
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

		if (!(level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
			return;
		}

		ServerPlayer player = resolveTarget(serverLevel);
		if (player == null || !player.isAlive() || player.isSpectator() || player.level() != serverLevel) {
			discard();
			return;
		}
		this.targetUuid = player.getUUID();

		double headY = player.getEyePosition().y + OFFSET_ABOVE_HEAD;
		setPos(player.getX(), headY, player.getZ());
		setDeltaMovement(Vec3.ZERO);

		if (tickCount < GRACE_TICKS) {
			return;
		}

		if (isPlayerLookingAtSun(player)) {
			net.lixis.outofbound.util.ScareSoundUtil.playRandomNoise(player);
			teleportPlayer(player);
			discard();
		}
	}

	private ServerPlayer resolveTarget(net.minecraft.server.level.ServerLevel serverLevel) {
		if (targetUuid != null) {
			Entity existing = serverLevel.getEntity(targetUuid);
			if (existing instanceof ServerPlayer player) {
				return player;
			}
		}
		return (ServerPlayer) serverLevel.getNearestPlayer(this, 64.0D);
	}

	private boolean isPlayerLookingAtSun(ServerPlayer player) {
		Vec3 eye = player.getEyePosition();
		Vec3 sunCenter = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
		Vec3 toSun = sunCenter.subtract(eye);
		if (toSun.lengthSqr() < 1.0E-4D) {
			return true;
		}
		Vec3 look = player.getLookAngle().normalize();
		double dot = look.dot(toSun.normalize());
		return dot >= LOOK_DOT_THRESHOLD;
	}

	private void teleportPlayer(ServerPlayer player) {
		long currentIndex = player.getPersistentData().getLong("outofbound_maze_index");
		MazeTeleportUtil.teleportToRandomMaze(player, currentIndex);
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
