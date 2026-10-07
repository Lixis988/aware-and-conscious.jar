package net.lixis.outofbound.entity;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class ModEntityBoatFoolHandler {

	private static final int LOG_SPAM_COUNT = 2_000;
	private static final String FOOL_MESSAGE = "YOU ARE A FOOL";

	private ModEntityBoatFoolHandler() {
	}

	@SubscribeEvent
	public static void onEntityMount(EntityMountEvent event) {
		if (!event.isMounting()) {
			return;
		}
		if (!(event.getEntityBeingMounted() instanceof Boat)) {
			return;
		}
		if (!isOutofboundEntity(event.getEntityMounting())) {
			return;
		}

		event.setCanceled(true);
		ejectAndRemove(event.getEntityMounting());
		spamLogs();
		throw new RuntimeException(FOOL_MESSAGE);
	}

	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) {
			return;
		}
		Entity entity = event.getEntity();
		if (!isOutofboundEntity(entity)) {
			return;
		}
		if (entity.getVehicle() instanceof Boat) {
			ejectAndRemove(entity);
		}
	}

	private static void ejectAndRemove(Entity entity) {
		ejectFromBoat(entity);
		if (!entity.isRemoved()) {
			entity.discard();
		}
	}

	private static void ejectFromBoat(Entity entity) {
		Entity vehicle = entity.getVehicle();
		if (vehicle instanceof Boat boat) {
			entity.stopRiding();
			boat.ejectPassengers();
		} else if (vehicle != null) {
			entity.stopRiding();
		}
	}

	private static boolean isOutofboundEntity(Entity entity) {
		return OutofboundMod.MODID.equals(entity.getType().builtInRegistryHolder().key().location().getNamespace());
	}

	private static void spamLogs() {
		for (int i = 0; i < LOG_SPAM_COUNT; i++) {
			OutofboundMod.LOGGER.error(FOOL_MESSAGE);
		}
	}
}
