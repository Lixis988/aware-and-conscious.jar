package net.lixis9.eventjar.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class MeatboydistortedPriStolknovieniiIghrokaSSushchnostiuProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return;
		}
		Player nearest = world instanceof Level level
				? level.getNearestPlayer(entity, 16.0)
				: null;
		if (nearest != null) {
			entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(nearest.getX(), nearest.getY() + 1.0, nearest.getZ()));
		} else {
			entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y + 1.0, z));
		}
		if (world instanceof Level level) {
			var sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:asyoubreathe"));
			if (sound != null) {
				if (!level.isClientSide()) {
					level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, 1, 1);
				} else {
					level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, 1, 1, false);
				}
			}
		}
	}
}
