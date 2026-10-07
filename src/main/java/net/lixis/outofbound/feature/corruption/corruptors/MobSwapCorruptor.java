package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.CorruptionEntityPools;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MobSwapCorruptor extends ServerCorruptor {

	public static final String SWAP_TAG = "outofbound_corruption_swap";

	private static final double RADIUS = 20.0D;

	@Override
	public float minLevel() {
		return 3.0F;
	}

	@Override
	public int weight() {
		return 6;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());
		long daysSince = daysSince(player);
		swapNearPlayer(level, player, ttl, false, daysSince);
	}

	public static boolean swapNearPlayer(ServerLevel level, ServerPlayer player, int ttl, boolean preferClosest) {
		return swapNearPlayer(level, player, ttl, preferClosest, daysSince(player));
	}

	public static boolean swapNearPlayer(ServerLevel level, ServerPlayer player, int ttl, boolean preferClosest,
			long daysSince) {
		List<LivingEntity> candidates = collectSwapCandidates(level, player);
		if (candidates.isEmpty()) {
			return false;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		LivingEntity target;
		if (preferClosest) {
			candidates.sort(Comparator.comparingDouble(e -> e.distanceToSqr(player)));
			int pick = Math.min(random.nextInt(3), candidates.size() - 1);
			target = candidates.get(pick);
		} else {
			target = candidates.get(random.nextInt(candidates.size()));
		}

		return swapEntity(level, target, ttl, daysSince);
	}

	public static boolean swapEntity(ServerLevel level, LivingEntity target, int ttl) {
		return swapEntity(level, target, ttl, daysSince(level.getServer()));
	}

	public static boolean swapEntity(ServerLevel level, LivingEntity target, int ttl, long daysSince) {
		EntityType<?> originalType = target.getType();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		EntityType<?> replacementType = CorruptionEntityPools.pickSwapType(originalType, daysSince, random);
		if (replacementType == null || replacementType == originalType) {
			return false;
		}

		CompoundTag originalNbt = new CompoundTag();
		target.saveWithoutId(originalNbt);

		Entity replacement = replacementType.create(level);
		if (!(replacement instanceof LivingEntity livingReplacement)) {
			if (replacement != null) {
				replacement.discard();
			}
			return false;
		}

		livingReplacement.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
		livingReplacement.getPersistentData().putBoolean(SWAP_TAG, true);
		level.addFreshEntity(livingReplacement);
		int replacementId = livingReplacement.getId();
		target.discard();

		CorruptionSnapshots.scheduleMobSwapRestore(
				level.dimension(),
				originalType,
				originalNbt,
				replacementId,
				ttl);
		return true;
	}

	private static List<LivingEntity> collectSwapCandidates(ServerLevel level, ServerPlayer player) {
		List<LivingEntity> candidates = new ArrayList<>();
		AABB box = player.getBoundingBox().inflate(RADIUS);
		for (Entity entity : level.getEntities(player, box, MobSwapCorruptor::isEligibleTarget)) {
			candidates.add((LivingEntity) entity);
		}
		return candidates;
	}

	private static boolean isEligibleTarget(Entity entity) {
		return entity instanceof LivingEntity living && CorruptionEntityPools.isSwapTarget(living);
	}

	@Nullable
	public static LivingEntity findClosestSwapCandidate(ServerLevel level, ServerPlayer player, double radius) {
		LivingEntity closest = null;
		double bestDist = radius * radius;
		AABB box = player.getBoundingBox().inflate(radius);
		for (Entity entity : level.getEntities(player, box, MobSwapCorruptor::isEligibleTarget)) {
			double dist = entity.distanceToSqr(player);
			if (dist < bestDist) {
				bestDist = dist;
				closest = (LivingEntity) entity;
			}
		}
		return closest;
	}

	private static long daysSince(ServerPlayer player) {
		MinecraftServer server = player.getServer();
		return server != null ? WorldInternalConfig.getDaysSinceBoundedcowCollision(server) : 0L;
	}

	private static long daysSince(@Nullable MinecraftServer server) {
		return server != null ? WorldInternalConfig.getDaysSinceBoundedcowCollision(server) : 0L;
	}
}
