package net.lixis.outofbound;

import net.lixis.outofbound.entity.SeraphEyeEntity;
import net.lixis.outofbound.registry.OutofboundExtraEntities;
import net.lixis.outofbound.world.FinaleState;
import net.lixis.outofbound.world.PostEyeWeirdnessHandler;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class SeraphFinaleHandler {

	private static final double APPROACH_SPEED = 0.14D;
	private static final double CONTACT_DISTANCE = 2.2D;
	private static final double BREAK_RADIUS = 1.5D;
	private static final double SPAWN_HEIGHT = 22.0D;
	private static final double SPAWN_FORWARD = 14.0D;

	private static UUID eyeUuid;
	private static double anchorX;
	private static double anchorY;
	private static double anchorZ;
	private static float anchorYaw;
	private static float anchorPitch;
	private static boolean spawned;

	private SeraphFinaleHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (WorldInternalConfig.getFinaleState(server) != FinaleState.ACTIVE) {
			return;
		}

		UUID playerUuid = WorldInternalConfig.getFinalePlayerUuid(server);
		if (playerUuid == null) {
			return;
		}

		ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);
		if (player == null || !player.isAlive()) {
			return;
		}

		ServerLevel level = server.overworld();
		if (!spawned) {
			spawnEye(level, player);
			return;
		}

		SeraphEyeEntity eye = findEye(level);
		if (eye == null) {
			if (spawned) {
				spawned = false;
				eyeUuid = null;
				spawnEye(level, player);
			}
			return;
		}

		anchorPlayer(player);
		moveEyeTowardPlayer(eye, player, level);

		Vec3 eyeCenter = eye.getBoundingBox().getCenter();
		Vec3 playerEye = player.getEyePosition();
		if (eyeCenter.distanceTo(playerEye) <= CONTACT_DISTANCE) {
			finishFinale(server, player);
		}
	}

	private static void spawnEye(ServerLevel level, ServerPlayer player) {
		anchorX = player.getX();
		anchorY = player.getY();
		anchorZ = player.getZ();
		anchorYaw = player.getYRot();
		anchorPitch = player.getXRot();

		float yawRad = player.getYRot() * ((float) Math.PI / 180.0F);
		double spawnX = player.getX() - Math.sin(yawRad) * SPAWN_FORWARD;
		double spawnY = player.getY() + SPAWN_HEIGHT;
		double spawnZ = player.getZ() + Math.cos(yawRad) * SPAWN_FORWARD;

		SeraphEyeEntity eye = OutofboundExtraEntities.SERAPH_EYE.get().create(level);
		if (eye == null) {
			return;
		}

		eye.moveTo(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
		level.addFreshEntity(eye);
		eyeUuid = eye.getUUID();
		spawned = true;

		PostEyeWeirdnessHandler.onEyeEncounter(level.getServer());
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new SeraphFinaleStartPacket(eye.getId()));
	}

	private static SeraphEyeEntity findEye(ServerLevel level) {
		if (eyeUuid == null) {
			return null;
		}
		if (level.getEntity(eyeUuid) instanceof SeraphEyeEntity eye) {
			return eye;
		}
		return null;
	}

	private static void anchorPlayer(ServerPlayer player) {
		player.setDeltaMovement(Vec3.ZERO);
		player.teleportTo(player.serverLevel(), anchorX, anchorY, anchorZ, anchorYaw, anchorPitch);
		player.hurtMarked = true;
	}

	private static void moveEyeTowardPlayer(SeraphEyeEntity eye, ServerPlayer player, ServerLevel level) {
		Vec3 target = player.getEyePosition();
		Vec3 current = eye.position();
		Vec3 delta = target.subtract(current);
		double distance = delta.length();

		if (distance <= 1.0E-4D) {
			return;
		}

		Vec3 step = delta.normalize().scale(Math.min(APPROACH_SPEED, distance));
		Vec3 next = current.add(step);
		eye.setPos(next.x, next.y, next.z);
		eye.setDeltaMovement(Vec3.ZERO);
		eye.hurtMarked = true;

		breakObstacles(level, next);
	}

	private static void breakObstacles(ServerLevel level, Vec3 center) {
		int radius = (int) Math.ceil(BREAK_RADIUS);
		BlockPos centerPos = BlockPos.containing(center);
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					BlockPos pos = centerPos.offset(dx, dy, dz);
					if (centerPos.distSqr(pos) > BREAK_RADIUS * BREAK_RADIUS) {
						continue;
					}
					BlockState state = level.getBlockState(pos);
					if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) {
						continue;
					}
					level.destroyBlock(pos, false);
				}
			}
		}
	}

	private static void finishFinale(MinecraftServer server, ServerPlayer player) {

		if (eyeUuid != null) {
			ServerLevel level = server.overworld();
			if (level.getEntity(eyeUuid) instanceof SeraphEyeEntity eye) {
				eye.discard();
			}
		}
		eyeUuid = null;
		spawned = false;
		WorldInternalConfig.setFinaleState(server, FinaleState.DONE);
		WorldProgressionServerHandler.markReturnDeadline(player);
		OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new SeraphFinaleEndPacket());
	}

	public static void resetRuntimeState() {
		eyeUuid = null;
		spawned = false;
	}
}
