package net.lixis9.eventjar.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.arguments.EntityAnchorArgument;

import net.lixis9.eventjar.EventjarMod;

public class CavemeetboyPriStolknovieniiIghrokaSSushchnostiuProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		EventjarMod.queueServerWork(100, () -> {
			if (!entity.level().isClientSide())
				entity.discard();
			entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, (1 + y), (2 + z)));
		});
	}
}
