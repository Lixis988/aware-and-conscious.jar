package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.BitCorruptor;
import net.lixis.outofbound.feature.corruption.util.CorruptionEntityPools;
import net.lixis.outofbound.feature.corruption.util.SafeCorruptor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PhysicsCorruptor extends ServerCorruptor {

	private static final double RADIUS = 24.0D;

	@Override
	public float minLevel() {
		return 10.0F;
	}

	@Override
	public int weight() {
		return 1;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		List<LivingEntity> candidates = new ArrayList<>();
		AABB box = player.getBoundingBox().inflate(RADIUS);
		for (Entity entity : level.getEntities(player, box, e -> e instanceof LivingEntity living
				&& living.isAlive() && !living.isRemoved() && !(e instanceof ServerPlayer)
				&& !CorruptionEntityPools.isProtectedHelper(living.getType()))) {
			candidates.add((LivingEntity) entity);
		}
		if (candidates.isEmpty()) {
			return;
		}

		LivingEntity target = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
		Vec3 original = target.getDeltaMovement();
		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());
		CorruptionSnapshots.scheduleVelocityRestore(target.getId(), original, ttl);

		ThreadLocalRandom random = ThreadLocalRandom.current();
		double strength = 0.5D + random.nextDouble() * 1.5D;
		Vec3 impulse = new Vec3(
				(random.nextDouble() - 0.5D) * strength,
				0.5D + random.nextDouble() * strength,
				(random.nextDouble() - 0.5D) * strength);

		Vec3 corrupted = new Vec3(
				SafeCorruptor.safeFloat((float) (original.x + impulse.x + BitCorruptor.corruptFloat(0.0F))),
				SafeCorruptor.safeFloat((float) (original.y + impulse.y + BitCorruptor.corruptFloat(0.0F))),
				SafeCorruptor.safeFloat((float) (original.z + impulse.z + BitCorruptor.corruptFloat(0.0F))));
		target.setDeltaMovement(corrupted);
	}
}
