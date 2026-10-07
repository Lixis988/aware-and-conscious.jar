package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class GlitchGrassHandler {

	public static final int TICK_INTERVAL = 10_000;
	public static final float TRIGGER_CHANCE = 0.25F;

	private GlitchGrassHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (event.getServer().getTickCount() % PostEyeWeirdness.scaledInterval(event.getServer(), GlitchGrassHandler.TICK_INTERVAL) != 0) {
			return;
		}

		RandomSource random = RandomSource.create(event.getServer().getTickCount() ^ 0x47C14C48495347L);
		float chance = (float) PostEyeWeirdness.scaledChance(event.getServer(), GlitchGrassHandler.TRIGGER_CHANCE);
		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			if (player.serverLevel().dimension() != Level.OVERWORLD) {
				continue;
			}
			if (random.nextFloat() > chance) {
				continue;
			}
			GlitchGrassPlacer.tryReplaceRandomGrass(player.serverLevel(), player.chunkPosition(), random);
		}
	}
}
