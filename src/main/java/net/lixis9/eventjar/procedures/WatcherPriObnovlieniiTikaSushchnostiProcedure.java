package net.lixis9.eventjar.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WatcherPriObnovlieniiTikaSushchnostiProcedure {

	private static final String STUCK_TICKS = "eventjar_watcher_stuck";

	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null || !(entity instanceof net.minecraft.world.entity.Mob)) {
			return;
		}
		Player player = world.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 64, false);
		if (player == null) {
			return;
		}

		Vec3 look = player.getLookAngle();
		Vec3 delta = entity.position().subtract(player.position());
		double lengthSqr = delta.lengthSqr();
		if (lengthSqr < 1.0E-8) {
			return;
		}
		Vec3 toWatcher = delta.scale(1.0 / Math.sqrt(lengthSqr));
		double dot = look.dot(toWatcher);
		if (dot <= 0.995) {
			entity.getPersistentData().putInt(STUCK_TICKS, 0);
			return;
		}

		boolean los = player.hasLineOfSight(entity);
		boolean stuck = isEmbedded(entity);
		if (stuck) {
			int stuckTicks = entity.getPersistentData().getInt(STUCK_TICKS) + 1;
			entity.getPersistentData().putInt(STUCK_TICKS, stuckTicks);

			if (stuckTicks >= 40 || los) {
				if (!entity.level().isClientSide()) {
					entity.discard();
				}
			}
			return;
		}
		entity.getPersistentData().putInt(STUCK_TICKS, 0);

		if (los && !entity.level().isClientSide()) {
			entity.discard();
		}
	}

	private static boolean isEmbedded(Entity entity) {
		Level level = entity.level();
		BlockPos feet = entity.blockPosition();
		BlockPos head = feet.above();
		return hasHardCollision(level, feet) || hasHardCollision(level, head);
	}

	private static boolean hasHardCollision(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.isAir() || state.canBeReplaced()) {
			return false;
		}
		return !state.getCollisionShape(level, pos).isEmpty();
	}
}
