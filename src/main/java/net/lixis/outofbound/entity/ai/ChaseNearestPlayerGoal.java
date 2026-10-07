package net.lixis.outofbound.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class ChaseNearestPlayerGoal extends Goal {

	private final Mob mob;

	public ChaseNearestPlayerGoal(Mob mob) {
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
		if (player != null) {
			mob.setTarget(player);
		}
	}

	private Player findTarget() {
		return mob.level().getNearestPlayer(mob, mob.getAttributeValue(Attributes.FOLLOW_RANGE));
	}
}
