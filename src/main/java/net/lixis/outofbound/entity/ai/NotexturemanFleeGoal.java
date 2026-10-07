package net.lixis.outofbound.entity.ai;

import net.lixis.outofbound.entity.NotexturemanEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class NotexturemanFleeGoal extends Goal {

	private static final double FLEE_SPEED = 2.0D;

	private final NotexturemanEntity mob;

	public NotexturemanFleeGoal(NotexturemanEntity mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return mob.getBehaviorState() == NotexturemanEntity.STATE_FLEEING;
	}

	@Override
	public boolean canContinueToUse() {
		return canUse();
	}

	@Override
	public void tick() {
		ServerPlayer target = mob.resolveTargetPlayer();
		if (target == null) {
			return;
		}
		Vec3 fleePos = DefaultRandomPos.getPosAway(mob, 16, 7, target.position());
		if (fleePos != null) {
			mob.getNavigation().moveTo(fleePos.x, fleePos.y, fleePos.z, FLEE_SPEED);
		}
	}
}
