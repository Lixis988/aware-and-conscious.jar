package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;
import net.lixis9.eventjar.entity.AbstractWarpedMeetboyEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class WarpedMeetboyHuntProcedure {

	private static final int SPAWN_DENOM = 120000;
	private static final double SPAWN_MIN = 14.0D;
	private static final double SPAWN_MAX = 26.0D;
	private static final int MAX_ACTIVE_NEAR = 1;
	private static final double NEAR_CHECK = 72.0D;

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
			return;
		}
		if (!TickThrottle.due(event.player)) {
			return;
		}
		Player player = event.player;
		Level level = player.level();
		if (!(level instanceof ServerLevel serverLevel) || level.dimension() != Level.OVERWORLD) {
			return;
		}
		if (!TickThrottle.rollDenomEntity(SPAWN_DENOM)) {
			return;
		}
		if (countActiveHunts(serverLevel, player) >= MAX_ACTIVE_NEAR) {
			return;
		}
		trySpawnHunt(serverLevel, player);
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntity().level().isClientSide) {
			return;
		}
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}
		if (!(player.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		discardAllHunts(serverLevel);
	}

	private static void trySpawnHunt(ServerLevel level, Player player) {
		RandomSource random = level.getRandom();
		EntityType<? extends AbstractWarpedMeetboyEntity> type = pickType(random);
		BlockPos pos = pickSpawnPos(level, player, random);
		if (pos == null) {
			return;
		}

		Entity spawned = type.spawn(level, pos, MobSpawnType.MOB_SUMMONED);
		if (spawned instanceof AbstractWarpedMeetboyEntity meetboy) {
			meetboy.markAsHunt();
			meetboy.setTarget(player);
			meetboy.setYRot(random.nextFloat() * 360.0F);
		}
	}

	private static EntityType<? extends AbstractWarpedMeetboyEntity> pickType(RandomSource random) {
		return switch (random.nextInt(3)) {
			case 0 -> EventjarModEntities.MEETBOY_GLITCH.get();
			case 1 -> EventjarModEntities.MEETBOY_ELONGATED.get();
			default -> EventjarModEntities.MEETBOY_STUBBY.get();
		};
	}

	@Nullable
	private static BlockPos pickSpawnPos(ServerLevel level, Player player, RandomSource random) {
		float yaw = player.getYRot() + 180.0F + (random.nextFloat() - 0.5F) * 100.0F;
		double dist = SPAWN_MIN + random.nextDouble() * (SPAWN_MAX - SPAWN_MIN);
		double rad = Math.toRadians(yaw);
		double x = player.getX() - Math.sin(rad) * dist;
		double z = player.getZ() + Math.cos(rad) * dist;
		int ix = Mth.floor(x);
		int iz = Mth.floor(z);
		int iy = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ix, iz);
		BlockPos pos = new BlockPos(ix, iy, iz);
		if (!level.isLoaded(pos) || !level.getWorldBorder().isWithinBounds(pos)) {
			return null;
		}
		if (Math.abs(iy - player.getY()) > 12.0D) {
			pos = BlockPos.containing(x, player.getY(), z);
		}
		return pos;
	}

	private static int countActiveHunts(ServerLevel level, Player player) {
		AABB box = player.getBoundingBox().inflate(NEAR_CHECK);
		return level.getEntitiesOfClass(AbstractWarpedMeetboyEntity.class, box, AbstractWarpedMeetboyEntity::isHunt).size();
	}

	private static void discardAllHunts(ServerLevel level) {
		AABB box = null;
		for (Player p : level.players()) {
			AABB next = p.getBoundingBox().inflate(256.0D);
			box = box == null ? next : box.minmax(next);
		}
		if (box == null) {
			return;
		}
		for (AbstractWarpedMeetboyEntity entity : level.getEntitiesOfClass(
				AbstractWarpedMeetboyEntity.class, box, AbstractWarpedMeetboyEntity::isHunt)) {
			entity.discard();
		}
	}
}
