package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.TickThrottle;
import net.lixis9.eventjar.network.BloodRainPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID)
public final class BloodRainProcedure {

	private static final int ROLL_DENOM = 120_000;
	private static final int DURATION_TICKS = 20 * 90;
	private static final Map<ResourceKey<Level>, Long> END_TICK = new ConcurrentHashMap<>();

	private BloodRainProcedure() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (serverLevel.dimension() != Level.OVERWORLD) {
			return;
		}

		Player driver = serverLevel.getRandomPlayer();
		if (driver == null || driver != event.player) {
			return;
		}
		if (!TickThrottle.due(event.player)) {
			return;
		}

		long now = serverLevel.getGameTime();
		Long end = END_TICK.get(serverLevel.dimension());
		if (end != null && now < end) {

			if (!serverLevel.isRaining()) {
				int left = (int) Math.min(Integer.MAX_VALUE, end - now);
				serverLevel.setWeatherParameters(0, left, true, false);
			}
			return;
		}
		if (end != null && now >= end) {
			END_TICK.remove(serverLevel.dimension());
		}

		if (!TickThrottle.rollDenomEvent(ROLL_DENOM)) {
			return;
		}
		start(serverLevel, DURATION_TICKS);
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (!(player.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		Long end = END_TICK.get(serverLevel.dimension());
		if (end == null) {
			return;
		}
		long left = end - serverLevel.getGameTime();
		if (left > 0L) {
			BloodRainPacket.sendToPlayer(player, (int) Math.min(Integer.MAX_VALUE, left));
		}
	}

	public static boolean start(ServerLevel level, int durationTicks) {
		if (durationTicks <= 0) {
			return false;
		}
		int duration = Math.max(40, durationTicks);
		END_TICK.put(level.dimension(), level.getGameTime() + duration);
		level.setWeatherParameters(0, duration, true, false);
		BloodRainPacket.sendToDimension(level, duration);
		return true;
	}

	public static boolean isActive(ServerLevel level) {
		Long end = END_TICK.get(level.dimension());
		return end != null && level.getGameTime() < end;
	}
}
