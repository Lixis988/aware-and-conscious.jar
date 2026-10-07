package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.BitCorruptor;
import net.lixis.outofbound.feature.corruption.util.CorruptionEntityPools;
import net.lixis.outofbound.feature.corruption.util.RandomItemPool;
import net.lixis.outofbound.feature.corruption.util.CorruptionScaling;
import net.lixis.outofbound.feature.corruption.util.SafeCorruptor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class EntityCorruptor extends ServerCorruptor {

	private static final double RADIUS = 32.0D;

	@Override
	public float minLevel() {
		return 5.0F;
	}

	@Override
	public int weight() {
		return 2;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		List<LivingEntity> candidates = collectCandidates(level, player);
		if (candidates.isEmpty()) {
			return;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());
		float strength = CorruptionScaling.tierStrength(ctx.level(), minLevel());
		int count = strength >= 0.75F ? 1 + random.nextInt(2) : 1;

		for (int i = 0; i < count && !candidates.isEmpty(); i++) {
			LivingEntity target = candidates.remove(random.nextInt(candidates.size()));
			int mode = random.nextInt(3);
			if (mode == 0) {
				applyNametag(target, random, ttl);
			} else if (mode == 1) {
				applyNametag(target, random, ttl);
				jitterEntity(level, target, random, ttl);
			} else {
				jitterEntity(level, target, random, ttl);
			}
		}
	}

	private static List<LivingEntity> collectCandidates(ServerLevel level, ServerPlayer player) {
		List<LivingEntity> candidates = new ArrayList<>();
		AABB box = player.getBoundingBox().inflate(RADIUS);
		for (Entity entity : level.getEntities(player, box, e -> e instanceof LivingEntity living
				&& living.isAlive() && !living.isRemoved() && !(e instanceof ServerPlayer)
				&& !CorruptionEntityPools.isProtectedHelper(living.getType()))) {
			candidates.add((LivingEntity) entity);
		}
		return candidates;
	}

	private static void applyNametag(LivingEntity target, ThreadLocalRandom random, int ttl) {
		@Nullable Component originalName = target.getCustomName();
		boolean originalVisible = target.isCustomNameVisible();

		String base = originalName != null
				? originalName.getString()
				: target.getType().getDescription().getString();
		String corrupted = BitCorruptor.corruptText(base);
		if (random.nextBoolean()) {
			corrupted = BitCorruptor.corruptText(playerLikeName(random));
		}

		CorruptionSnapshots.scheduleEntityNameRestore(
				target.getId(),
				originalName,
				originalVisible,
				ttl);

		target.setCustomName(Component.literal(corrupted));
		target.setCustomNameVisible(true);
	}

	private static String playerLikeName(ThreadLocalRandom random) {
		String[] bases = {
				"Steve", "Alex", "Player", "Herobrine", "null", "undefined",
				"user_" + random.nextInt(9999), "guest", "???"
		};
		return BitCorruptor.corruptText(bases[random.nextInt(bases.length)]);
	}

	private static void jitterEntity(ServerLevel level, LivingEntity target, ThreadLocalRandom random, int ttl) {
		Vec3 originalPos = target.position();
		float originalYaw = target.getYRot();
		CorruptionSnapshots.schedulePosRestore(target.getId(), originalPos, originalYaw, ttl);

		double range = 2.0D + random.nextDouble() * 4.0D;
		double jitterX = SafeCorruptor.safeDouble(random.nextDouble(-range, range));
		double jitterY = SafeCorruptor.safeDouble(random.nextDouble(-range * 0.5D, range * 0.5D));
		double jitterZ = SafeCorruptor.safeDouble(random.nextDouble(-range, range));
		Vec3 next = originalPos.add(jitterX, jitterY, jitterZ);
		if (level.isLoaded(net.minecraft.core.BlockPos.containing(next))) {
			target.setPos(next.x, next.y, next.z);
		}
	}
}
