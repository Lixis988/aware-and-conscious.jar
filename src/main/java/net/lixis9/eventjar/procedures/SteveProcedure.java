package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.init.EventjarModEntities;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SteveProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player.getX(), event.player.getZ());
		}
	}

	public static void execute(LevelAccessor world, double x, double z) {
		execute(null, world, x, z);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double z) {
		if (Math.random() < (1) / ((float) 100000)) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = EventjarModEntities.WHOAMI.get().spawn(_level, BlockPos.containing(x + 70, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 70), (int) z), z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
				}
			}
		}
	}
}
