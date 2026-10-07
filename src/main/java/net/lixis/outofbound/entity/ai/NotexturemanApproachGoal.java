package net.lixis.outofbound.entity.ai;

import net.lixis.outofbound.entity.NotexturemanEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class NotexturemanApproachGoal extends Goal {

	private final NotexturemanEntity mob;

	public NotexturemanApproachGoal(NotexturemanEntity mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return mob.getBehaviorState() == NotexturemanEntity.STATE_APPROACH && mob.resolveTargetPlayer() != null;
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
		mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
		mob.getNavigation().moveTo(target, 1.0D);
	}
}
