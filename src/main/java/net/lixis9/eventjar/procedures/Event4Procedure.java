package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.lixis9.eventjar.network.EventjarModVariables;
import net.lixis9.eventjar.init.EventjarModEntities;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Event4Procedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables())).patience == 10000) {
			if (Math.random() < (1) / ((float) 50)) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = EventjarModEntities.ENTITYWHOWATCHYOU.get().spawn(_level, BlockPos.containing(4 + x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
		}
	}
}
