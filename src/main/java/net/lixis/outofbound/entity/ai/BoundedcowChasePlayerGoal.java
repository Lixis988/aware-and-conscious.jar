package net.lixis.outofbound.entity.ai;

import net.lixis.outofbound.BoundedcowEjectHandler;
import net.lixis.outofbound.entity.BoundedcowEntity;
import net.lixis.outofbound.entity.ChaserMovement;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BoundedcowChasePlayerGoal extends Goal {

	private final BoundedcowEntity mob;

	public BoundedcowChasePlayerGoal(BoundedcowEntity mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return findTarget() != null;
	}

	@Override
	public boolean canContinueToUse() {
		return findTarget() != null;
	}

	@Override
	public void tick() {
		Player player = findTarget();
		if (player == null) {
			return;
		}

		mob.setTarget(player);
		mob.getNavigation().stop();

		Vec3 mobPos = mob.position();
		Vec3 playerPos = player.position();
		Vec3 delta = playerPos.subtract(mobPos);
		double horizontalDistance = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

		double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
		double moveX = 0.0D;
		double moveZ = 0.0D;

		if (horizontalDistance > 0.001D) {
			moveX = delta.x / horizontalDistance * speed;
			moveZ = delta.z / horizontalDistance * speed;
			float yaw = (float) (Mth.atan2(-moveX, moveZ) * (180.0D / Math.PI));
			mob.setYRot(yaw);
			mob.yRotO = yaw;
		}

		double moveY = mob.getDeltaMovement().y;
		if (mob.horizontalCollision || (moveX * moveX + moveZ * moveZ) > 1.0E-4D) {
			ChaserMovement.breakSurroundingObstacles(mob);
		}
		if (mob.horizontalCollision) {
			moveY = 0.42D;
		} else if (mob.onGround()) {
			moveY = Math.max(moveY, 0.0D);
		}

		mob.setDeltaMovement(moveX, moveY, moveZ);
		mob.hurtMarked = true;

		double touchDistance = mob.getBbWidth() + player.getBbWidth() + 0.35D;
		if (mob.distanceTo(player) <= touchDistance) {
			BoundedcowEjectHandler.onPlayerTouch(player, mob);
		}
	}

	private Player findTarget() {
		return mob.level().getNearestPlayer(mob, mob.getAttributeValue(Attributes.FOLLOW_RANGE));
	}
}
