package net.lixis9.eventjar.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.EventjarMod;

public class ErrundefinePriStolknovieniiIghrokaSSushchnostiuProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:glitchjumpscare")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:glitchjumpscare")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}
		EventjarMod.queueServerWork(20, () -> {
			if (!world.isClientSide() && world.getServer() != null)
				ServerLifecycleHooks.getCurrentServer().stopServer();
			EventjarMod.queueServerWork(20, () -> {
				if (!entity.level().isClientSide())
					entity.discard();
			});
		});
	}
}
