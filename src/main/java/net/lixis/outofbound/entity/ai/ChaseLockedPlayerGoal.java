package net.lixis.outofbound.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.Supplier;

public class ChaseLockedPlayerGoal extends Goal {

	private final Mob mob;
	private final Supplier<LivingEntity> targetSupplier;

	public ChaseLockedPlayerGoal(Mob mob, Supplier<LivingEntity> targetSupplier) {
		this.mob = mob;
		this.targetSupplier = targetSupplier;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return targetSupplier.get() != null;
	}

	@Override
	public boolean canContinueToUse() {
		return targetSupplier.get() != null;
	}

	@Override
	public void tick() {
		LivingEntity target = targetSupplier.get();
		if (target != null) {
			mob.setTarget(target);
		}
	}
}
