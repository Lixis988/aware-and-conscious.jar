package net.lixis9.eventjar.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class SeekeractPriObnovlieniiTikaSushchnostiProcedure {

	private static final double BLINK_DISTANCE = 22.0D;
	private static final double BLINK_LANDING = 8.0D;
	private static final int BLOCK_BREAK_RANGE = 3;

	public static void execute(Entity entity) {
		if (entity == null || entity.level().isClientSide()) {
			return;
		}
		if (!(entity.level() instanceof ServerLevel level)) {
			return;
		}

		if (entity.getVehicle() instanceof Boat boat) {
			entity.stopRiding();
			boat.hurt(level.damageSources().generic(), 100.0F);
			boat.discard();
		}

		Player target = level.getNearestPlayer(entity, 128.0D);
		if (target == null || !target.isAlive() || target.isSpectator()) {
			return;
		}

		if (entity instanceof Mob mob) {
			mob.setTarget(target);
		}

		double dist = entity.distanceTo(target);

		if (target.isFallFlying() || dist > BLINK_DISTANCE) {
			blinkNearPlayer(level, entity, target);
			return;
		}

		if (entity.tickCount % 4 == 0 && dist < 12.0D) {
			breakSoftBlocksToward(level, entity, target);
		}
	}

	private static void blinkNearPlayer(ServerLevel level, Entity seeker, Player player) {
		Vec3 look = player.getLookAngle();
		Vec3 flat = new Vec3(look.x, 0.0D, look.z);
		if (flat.lengthSqr() < 1.0E-4) {
			flat = new Vec3(0.0D, 0.0D, 1.0D);
		} else {
			flat = flat.normalize();
		}

		double tx = player.getX() - flat.x * BLINK_LANDING;
		double tz = player.getZ() - flat.z * BLINK_LANDING;
		int ix = Mth.floor(tx);
		int iz = Mth.floor(tz);
		int iy = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ix, iz);
		BlockPos feet = new BlockPos(ix, iy, iz);
		if (!level.getWorldBorder().isWithinBounds(feet)) {
			return;
		}
		seeker.teleportTo(ix + 0.5D, iy, iz + 0.5D);
		if (seeker instanceof Mob mob) {
			mob.getNavigation().moveTo(player, 2.2D);
		}
	}

	private static void breakSoftBlocksToward(ServerLevel level, Entity seeker, Player target) {
		double dx = target.getX() - seeker.getX();
		double dz = target.getZ() - seeker.getZ();
		double distance = Math.sqrt(dx * dx + dz * dz);
		if (distance < 1.0D || distance > BLOCK_BREAK_RANGE + 2) {
			return;
		}
		dx /= distance;
		dz /= distance;
		for (int i = 1; i <= BLOCK_BREAK_RANGE; i++) {
			BlockPos base = BlockPos.containing(
					seeker.getX() + dx * i,
					seeker.getY(),
					seeker.getZ() + dz * i);
			for (int y = 0; y <= 2; y++) {
				BlockPos pos = base.above(y);
				BlockState state = level.getBlockState(pos);
				if (state.isAir()) {
					continue;
				}
				float hardness = state.getDestroySpeed(level, pos);
				if (hardness < 0.0F || hardness >= 20.0F) {
					continue;
				}
				if (state.is(Blocks.BEDROCK) || state.is(Blocks.BARRIER) || state.is(Blocks.OBSIDIAN)) {
					continue;
				}
				level.destroyBlock(pos, false);
			}
		}
	}
}
