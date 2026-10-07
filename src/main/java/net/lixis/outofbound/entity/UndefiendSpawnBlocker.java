package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class UndefiendSpawnBlocker {

	private UndefiendSpawnBlocker() {
	}

	@SubscribeEvent
	public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
		if (event.getEntity() instanceof UndefiendEntity) {
			if (isOverworld(event.getEntity().level())) {
				event.setSpawnCancelled(true);
			}
			return;
		}

		if (event.getEntity() instanceof TeethmanEntity) {
			if (isOverworld(event.getEntity().level())) {
				event.setSpawnCancelled(true);
			}
			return;
		}

		if (!(event.getEntity() instanceof ChaserEntity)) {
			return;
		}
		if (event.getSpawnType() == MobSpawnType.SPAWN_EGG || event.getSpawnType() == MobSpawnType.COMMAND) {
			return;
		}
		event.setSpawnCancelled(true);
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) {
			return;
		}
		if (event.getEntity() instanceof UndefiendEntity entity) {
			if (!isOverworld(event.getLevel())) {
				return;
			}
			entity.discard();
			event.setCanceled(true);
			return;
		}
		if (event.getEntity() instanceof TeethmanEntity entity) {
			if (!isOverworld(event.getLevel())) {
				return;
			}
			entity.discard();
			event.setCanceled(true);
		}
	}

	static boolean isAllowedDimension(Level level) {
		if (level.dimension() == Level.OVERWORLD) {
			return false;
		}
		return level instanceof ServerLevel serverLevel
				&& MazeDimensions.isMazeDimension(serverLevel.dimension().location());
	}

	private static boolean isOverworld(Level level) {
		return level.dimension() == Level.OVERWORLD;
	}
}
