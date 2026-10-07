package net.lixis.outofbound.world;

import net.lixis.outofbound.BoundedOneAtmospherePacket;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.entity.DarkEyeEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class PostEyeWeirdnessHandler {

	private static final int GLITCH_INTERVAL = 3600;
	private static final double GLITCH_CHANCE = 0.25D;
	private static final int STRUCTURE_INTERVAL = 900;
	private static final int PROXIMITY_INTERVAL = 20;
	private static final double DARK_EYE_PROXIMITY = 32.0D;

	private PostEyeWeirdnessHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		MinecraftServer server = event.getServer();
		checkDarkEyeProximity(server);

		if (!PostEyeWeirdness.isActive(server)) {
			return;
		}

		int tick = server.getTickCount();
		if (tick % GLITCH_INTERVAL == 0 && ThreadLocalRandom.current().nextDouble() < GLITCH_CHANCE) {
			triggerGlitchPulse(server);
		}
		if (tick % STRUCTURE_INTERVAL == 0) {
			tryPlaceStructures(server);
		}
	}

	private static void checkDarkEyeProximity(MinecraftServer server) {
		if (server.getTickCount() % PROXIMITY_INTERVAL != 0) {
			return;
		}
		if (WorldInternalConfig.isPostEyeWeirdness(server)) {
			return;
		}

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerLevel level = player.serverLevel();
			AABB box = player.getBoundingBox().inflate(DARK_EYE_PROXIMITY);
			if (level.getEntitiesOfClass(DarkEyeEntity.class, box, entity -> !entity.isRemoved()).isEmpty()) {
				continue;
			}
			WorldInternalConfig.enablePostEyeWeirdness(server);
			return;
		}
	}

	private static void triggerGlitchPulse(MinecraftServer server) {
		BoundedOneAtmospherePacket packet = new BoundedOneAtmospherePacket(
				BoundedOneAtmospherePacket.Effect.GLITCH_PULSE, 18);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), packet);
		}
	}

	private static void tryPlaceStructures(MinecraftServer server) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.isSpectator() || player.isCreative()) {
				continue;
			}
			if (random.nextDouble() >= 0.4D) {
				continue;
			}
			StrangeStructurePlacer.tryPlaceNear(player);
		}
	}

	public static void onEyeEncounter(MinecraftServer server) {
		WorldInternalConfig.enablePostEyeWeirdness(server);
		triggerGlitchPulse(server);
	}
}
