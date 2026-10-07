package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.AacConfig;
import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.entity.FaultEntity;
import net.lixis9.eventjar.entity.ScavengerEntity;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID)
public final class StalkerNearPlayerSpawnProcedure {

	private static final int CHECK_INTERVAL = 160;
	private static final float SPAWN_CHANCE = 0.12F;
	private static final double MIN_DIST = 10.0D;
	private static final double MAX_DIST = 22.0D;
	private static final double NEARBY_CAP_RANGE = 56.0D;

	private StalkerNearPlayerSpawnProcedure() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player instanceof ServerPlayer player)) {
			return;
		}
		if (player.tickCount % CHECK_INTERVAL != 0) {
			return;
		}
		if (player.level().dimension() != Level.OVERWORLD) {
			return;
		}
		if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
			return;
		}
		if (AacConfig.ENTITY_SPAWN_MULTIPLIER <= 0.0D) {
			return;
		}
		float chance = AacConfig.scaleEntityChance(SPAWN_CHANCE);
		if (player.getRandom().nextFloat() > chance) {
			return;
		}

		ServerLevel level = player.serverLevel();
		boolean spawnFault = player.getRandom().nextBoolean();
		if (spawnFault) {
			if (countNearby(level, player, FaultEntity.class) > 0) {
				spawnFault = false;
			}
		}
		if (!spawnFault && countNearby(level, player, ScavengerEntity.class) > 0) {
			return;
		}
		if (spawnFault && countNearby(level, player, FaultEntity.class) > 0) {
			return;
		}

		BlockPos spawnPos = findSpawnOutsideView(level, player);
		if (spawnPos == null) {
			return;
		}

		EntityType<? extends Mob> type = spawnFault
				? EventjarModEntities.FAULT.get()
				: EventjarModEntities.SCAVENGER.get();
		Entity spawned = type.spawn(level, spawnPos, MobSpawnType.MOB_SUMMONED);
		if (spawned != null) {
			spawned.setYRot(player.getYRot() + 180.0F);
			spawned.setYHeadRot(spawned.getYRot());
		}
	}

	private static <T extends Entity> int countNearby(ServerLevel level, ServerPlayer player, Class<T> type) {
		AABB box = player.getBoundingBox().inflate(NEARBY_CAP_RANGE);
		return level.getEntitiesOfClass(type, box).size();
	}

	private static BlockPos findSpawnOutsideView(ServerLevel level, ServerPlayer player) {
		Vec3 look = player.getLookAngle();
		Vec3 flatLook = new Vec3(look.x, 0.0D, look.z);
		if (flatLook.lengthSqr() < 1.0E-4) {
			flatLook = new Vec3(0.0D, 0.0D, 1.0D);
		} else {
			flatLook = flatLook.normalize();
		}
		Vec3 side = new Vec3(-flatLook.z, 0.0D, flatLook.x);

		for (int attempt = 0; attempt < 12; attempt++) {
			double dist = MIN_DIST + player.getRandom().nextDouble() * (MAX_DIST - MIN_DIST);
			double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;

			Vec3 dir = new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));
			if (dir.dot(flatLook) > 0.35D) {
				dir = dir.add(flatLook.scale(-1.2D)).normalize();
			}

			dir = dir.add(side.scale((player.getRandom().nextDouble() - 0.5D) * 0.4D));
			if (dir.lengthSqr() < 1.0E-4) {
				continue;
			}
			dir = dir.normalize();

			double x = player.getX() + dir.x * dist;
			double z = player.getZ() + dir.z * dist;
			int ix = Mth.floor(x);
			int iz = Mth.floor(z);
			int iy = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ix, iz);
			BlockPos feet = new BlockPos(ix, iy, iz);
			if (!level.getWorldBorder().isWithinBounds(feet)) {
				continue;
			}
			if (!level.getBlockState(feet).isAir() || !level.getBlockState(feet.above()).isAir()) {
				continue;
			}
			if (!level.getBlockState(feet.below()).isSolidRender(level, feet.below())) {
				continue;
			}
			return feet;
		}
		return null;
	}
}
