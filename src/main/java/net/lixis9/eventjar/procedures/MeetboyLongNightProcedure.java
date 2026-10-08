package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.TickThrottle;
import net.lixis9.eventjar.entity.MeetboyLongEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.init.EventjarModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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

@Mod.EventBusSubscriber(modid = EventjarMod.MODID)
public final class MeetboyLongNightProcedure {

	private static final int ROLL_DENOM = 90_000;
	private static final double SPAWN_MIN = 18.0D;
	private static final double SPAWN_MAX = 38.0D;
	private static final int MIN_ELEVATION = 6;
	private static final int MAX_ACTIVE_NEAR = 1;
	private static final double NEAR_CHECK = 80.0D;

	private MeetboyLongNightProcedure() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
			return;
		}
		if (!TickThrottle.due(event.player)) {
			return;
		}
		Player player = event.player;
		if (!(player.level() instanceof ServerLevel serverLevel) || serverLevel.dimension() != Level.OVERWORLD) {
			return;
		}
		if (serverLevel.isDay()) {
			return;
		}
		if (countNearby(serverLevel, player) >= MAX_ACTIVE_NEAR) {
			return;
		}
		if (!TickThrottle.rollDenomEvent(ROLL_DENOM)) {
			return;
		}
		trySpawn(serverLevel, player);
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntity().level().isClientSide || !(event.getEntity() instanceof Player player)) {
			return;
		}
		if (!(player.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		AABB box = player.getBoundingBox().inflate(256.0D);
		for (MeetboyLongEntity entity : serverLevel.getEntitiesOfClass(MeetboyLongEntity.class, box, ignored -> true)) {
			entity.discard();
		}
	}

	private static void trySpawn(ServerLevel level, Player player) {
		BlockPos pos = pickHighGround(level, player);
		if (pos == null) {
			return;
		}
		Entity spawned = EventjarModEntities.MEETBOY_LONG.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
		if (spawned == null) {
			return;
		}
		spawned.setYRot(player.getYRot() + 180.0F);
		spawned.setYHeadRot(spawned.getYRot());
		level.playSound(null, pos, EventjarModSounds.UNDEFINE.get(), SoundSource.PLAYERS, 2.4F, 0.7F);
	}

	@Nullable
	private static BlockPos pickHighGround(ServerLevel level, Player player) {
		RandomSource random = level.getRandom();
		BlockPos best = null;
		int bestY = Mth.floor(player.getY()) + MIN_ELEVATION - 1;
		for (int attempt = 0; attempt < 16; attempt++) {
			double dist = SPAWN_MIN + random.nextDouble() * (SPAWN_MAX - SPAWN_MIN);
			double yaw = Math.toRadians(random.nextFloat() * 360.0F);
			double x = player.getX() - Math.sin(yaw) * dist;
			double z = player.getZ() + Math.cos(yaw) * dist;
			int ix = Mth.floor(x);
			int iz = Mth.floor(z);
			int iy = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ix, iz);
			BlockPos pos = new BlockPos(ix, iy, iz);
			if (!level.isLoaded(pos) || !level.getWorldBorder().isWithinBounds(pos)) {
				continue;
			}
			if (iy < Mth.floor(player.getY()) + MIN_ELEVATION) {
				continue;
			}
			if (iy > bestY) {
				bestY = iy;
				best = pos;
			}
		}
		return best;
	}

	private static int countNearby(ServerLevel level, Player player) {
		AABB box = player.getBoundingBox().inflate(NEAR_CHECK);
		return level.getEntitiesOfClass(MeetboyLongEntity.class, box, ignored -> true).size();
	}
}
