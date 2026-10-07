package net.lixis.outofbound.procedures;

import net.lixis.outofbound.entity.BoundedcowSpawnManager;
import net.lixis.outofbound.entity.NormalcowEntity;
import net.lixis.outofbound.init.OutofboundModEntities;
import net.lixis.outofbound.util.PlayerLookUtil;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class NormalcowFovSpawnManager {

	private static final int MANAGER_INTERVAL = 20;
	private static final int RELOCATE_AFTER_TICKS = 300;
	private static final double FOV_MIN_DOT = 0.55D;
	private static final double MIN_SPAWN_DISTANCE = 10.0D;
	private static final double MAX_SPAWN_DISTANCE = 50.0D;

	private static final double ESCAPE_DISTANCE = 50.0D;

	private NormalcowFovSpawnManager() {
	}

	public static void tick(MinecraftServer server) {
		if (server.getTickCount() % MANAGER_INTERVAL != 0) {
			return;
		}

		removeAllNormalcows(server);
		if (WorldInternalConfig.hasBoundedcowCollision(server)) {
			BoundedcowSpawnManager.removeAll(server);
		}
	}

	private static ServerPlayer findAnchorPlayer(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.isAlive() && !player.isSpectator()) {
				return player;
			}
		}
		return null;
	}

	private static NormalcowEntity getOrCreateCow(MinecraftServer server, ServerLevel level, ServerPlayer player) {
		List<NormalcowEntity> cows = collectAllNormalcows(server);

		for (int index = cows.size() - 1; index >= 1; index--) {
			cows.get(index).discard();
		}

		if (cows.isEmpty()) {
			return spawnInFov(level, player);
		}

		NormalcowEntity cow = cows.get(0);
		if (cow.level() != level) {
			cow.discard();
			return spawnInFov(level, player);
		}

		return cow;
	}

	private static List<NormalcowEntity> collectAllNormalcows(MinecraftServer server) {
		List<NormalcowEntity> cows = new ArrayList<>();
		for (ServerLevel level : server.getAllLevels()) {
			for (Entity entity : level.getAllEntities()) {
				if (entity instanceof NormalcowEntity normalcow && !normalcow.isRemoved()) {
					cows.add(normalcow);
				}
			}
		}
		cows.sort(Comparator.comparingInt(entity -> entity.getId()));
		return cows;
	}

	private static void removeAllNormalcows(MinecraftServer server) {
		for (NormalcowEntity cow : collectAllNormalcows(server)) {
			cow.discard();
		}
	}

	private static NormalcowEntity spawnInFov(ServerLevel level, ServerPlayer player) {
		Optional<Vec3> spawnPos = findFovSpawnPosition(level, player);
		if (spawnPos.isEmpty()) {
			return null;
		}

		Vec3 pos = spawnPos.get();
		NormalcowEntity cow = OutofboundModEntities.NORMALCOW.get().create(level);
		if (cow == null) {
			return null;
		}

		cow.moveTo(pos.x, pos.y, pos.z, player.getYRot() + 180.0F, 0.0F);
		cow.setNoAi(true);
		cow.getPersistentData().putBoolean("outofbound_managed", true);
		cow.getPersistentData().putLong("outofbound_placed_tick", level.getGameTime());
		level.addFreshEntity(cow);
		return cow;
	}

	private static void relocateCow(NormalcowEntity cow, ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		if (cow.level() != level) {
			cow.discard();
			spawnInFov(level, player);
			return;
		}

		Optional<Vec3> spawnPos = findFovSpawnPosition(level, player);
		cow.discard();
		if (spawnPos.isEmpty()) {
			return;
		}

		NormalcowEntity replacement = OutofboundModEntities.NORMALCOW.get().create(level);
		if (replacement == null) {
			return;
		}

		Vec3 pos = spawnPos.get();
		replacement.moveTo(pos.x, pos.y, pos.z, player.getYRot() + 180.0F, 0.0F);
		replacement.setNoAi(true);
		replacement.getPersistentData().putBoolean("outofbound_managed", true);
		replacement.getPersistentData().putLong("outofbound_placed_tick", level.getGameTime());
		level.addFreshEntity(replacement);
	}

	public static Optional<Vec3> findFovSpawnPosition(ServerLevel level, ServerPlayer player) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Vec3 eye = player.getEyePosition(1.0F);
		float baseYaw = player.getYRot();
		float basePitch = player.getXRot();

		for (int attempt = 0; attempt < 48; attempt++) {
			double distance = random.nextDouble(MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE);
			float yaw = baseYaw + random.nextFloat(-24.0F, 24.0F);
			float pitch = basePitch + random.nextFloat(-14.0F, 12.0F);
			Vec3 direction = Vec3.directionFromRotation(pitch, yaw);
			Vec3 candidate = eye.add(direction.scale(distance));

			BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(candidate));
			if (Math.abs(ground.getY() - candidate.y) > 5) {
				continue;
			}

			if (!isValidSpawnColumn(level, ground)) {
				continue;
			}

			Vec3 spawn = Vec3.atBottomCenterOf(ground).add(0.0D, 0.0D, 0.0D);
			Vec3 lookTarget = spawn.add(0.0D, 0.9D, 0.0D);
			if (!PlayerLookUtil.isPositionInFov(player, lookTarget, FOV_MIN_DOT)) {
				continue;
			}
			if (!hasClearLine(level, player, lookTarget)) {
				continue;
			}

			return Optional.of(spawn);
		}

		return Optional.empty();
	}

	private static boolean isValidSpawnColumn(ServerLevel level, BlockPos ground) {
		if (!level.getBlockState(ground.below()).isSolidRender(level, ground.below())) {
			return false;
		}
		return level.getBlockState(ground).isAir() && level.getBlockState(ground.above()).isAir();
	}

	private static boolean hasClearLine(Level level, ServerPlayer player, Vec3 target) {
		BlockHitResult hit = level.clip(new ClipContext(
				player.getEyePosition(1.0F),
				target,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				player));
		return hit.getType() == HitResult.Type.MISS || hit.getLocation().distanceToSqr(target) < 4.0D;
	}
}
