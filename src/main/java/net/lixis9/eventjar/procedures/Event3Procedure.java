package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.TickThrottle;
import net.lixis9.eventjar.network.EventjarModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class Event3Procedure {

	private static final long EXPECTED_INTERVAL_TICKS = 20L * 60L * 30L;
	private static final int DISPLAY_TICKS = 20;

	private static final Map<UUID, Integer> displayCooldown = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> lastTriggerTick = new ConcurrentHashMap<>();
	private static final Set<UUID> activeThisTick = ConcurrentHashMap.newKeySet();

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!event.player.level().isClientSide()) {
			return;
		}
		UUID id = event.player.getUUID();
		activeThisTick.remove(id);
		displayCooldown.compute(id, (key, value) -> {
			if (value == null || value <= 1) {
				return null;
			}
			return value - 1;
		});
		if (!TickThrottle.due(event.player)) {
			return;
		}
		execute(event, event.player);
	}

	public static boolean isActive(Entity entity) {
		return entity != null && activeThisTick.contains(entity.getUUID());
	}

	public static boolean execute(Entity entity) {
		return execute(null, entity);
	}

	private static boolean execute(@Nullable net.minecraftforge.eventbus.api.Event event, Entity entity) {
		if (entity == null) {
			return false;
		}
		UUID id = entity.getUUID();
		if (displayCooldown.getOrDefault(id, 0) > 0) {
			return false;
		}
		if ((entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables())).patience < 1000) {
			return false;
		}

		long now = entity.tickCount;
		long last = lastTriggerTick.getOrDefault(id, Long.MIN_VALUE / 2);
		if (now - last < EXPECTED_INTERVAL_TICKS) {
			return false;
		}

		if (Math.random() < (TickThrottle.INTERVAL / (double) EXPECTED_INTERVAL_TICKS)) {
			lastTriggerTick.put(id, now);
			displayCooldown.put(id, DISPLAY_TICKS);
			activeThisTick.add(id);
			return true;
		}
		return false;
	}
}
