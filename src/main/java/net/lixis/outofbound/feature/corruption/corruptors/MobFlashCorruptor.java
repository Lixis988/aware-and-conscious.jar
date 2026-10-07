package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.CorruptionEntityPools;
import net.lixis.outofbound.feature.corruption.util.CorruptionScaling;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public final class MobFlashCorruptor extends ServerCorruptor {

	public static final String FLASH_TAG = "outofbound_corruption_flash";
	private static final int FLASH_TICKS = 20;
	private static final double MIN_DISTANCE = 4.0D;
	private static final double MAX_DISTANCE = 14.0D;

	@Override
	public float minLevel() {
		return 12.0F;
	}

	@Override
	public int weight() {
		return 4;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		float strength = CorruptionScaling.tierStrength(ctx.level(), minLevel());
		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextFloat() >= 0.25F + strength * 0.45F) {
			return;
		}
		flashOneMob(level, player, random);
	}

	private static void flashOneMob(ServerLevel level, ServerPlayer player, ThreadLocalRandom random) {
		EntityType<?> type = CorruptionEntityPools.pickFlashType(random);
		if (type == null) {
			return;
		}

		Entity entity = type.create(level);
		if (entity == null) {
			return;
		}

		double angle = random.nextDouble() * Math.PI * 2.0D;
		double distance = MIN_DISTANCE + random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
		double x = player.getX() + Math.cos(angle) * distance;
		double z = player.getZ() + Math.sin(angle) * distance;
		double y = player.getY() + random.nextDouble(-2.0D, 3.0D);
		Vec3 pos = new Vec3(x, y, z);

		entity.moveTo(pos.x, pos.y, pos.z, random.nextFloat() * 360.0F, 0.0F);
		entity.getPersistentData().putBoolean(FLASH_TAG, true);
		level.addFreshEntity(entity);
		CorruptionSnapshots.scheduleEntityDiscard(entity.getId(), FLASH_TICKS);
	}
}
