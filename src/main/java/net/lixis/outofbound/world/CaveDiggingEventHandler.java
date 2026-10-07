package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.entity.CaveUndefiendSpawnManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class CaveDiggingEventHandler {

	private static final int CHECK_INTERVAL_TICKS = 3_000;
	private static final double EVENT_CHANCE = 0.7D;
	private static final int MIN_STEPS = 5;
	private static final int MAX_STEPS = 8;
	private static final int STEP_SPACING_TICKS = 9;

	private static final SoundEvent[] FALLBACK_DIG_SOUNDS = {
			SoundEvents.STONE_HIT,
			SoundEvents.GRAVEL_HIT,
			SoundEvents.DEEPSLATE_HIT,
			SoundEvents.NETHERRACK_HIT,
			SoundEvents.WOOD_HIT
	};

	private CaveDiggingEventHandler() {
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (server.getTickCount() % CHECK_INTERVAL_TICKS != 0) {
			return;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!canTriggerFor(player)) {
				continue;
			}
			if (random.nextDouble() >= EVENT_CHANCE) {
				continue;
			}
			scheduleDiggingSequence(player.serverLevel(), player, random);
		}
	}

	private static boolean canTriggerFor(ServerPlayer player) {
		if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
			return false;
		}
		return CaveUndefiendSpawnManager.isInCave(player.serverLevel(), player.blockPosition());
	}

	private static void scheduleDiggingSequence(ServerLevel level, ServerPlayer player, ThreadLocalRandom random) {
		int steps = MIN_STEPS + random.nextInt(MAX_STEPS - MIN_STEPS + 1);
		for (int step = 0; step < steps; step++) {
			int delay = step * STEP_SPACING_TICKS;
			int distance = Math.max(4, 30 - step * 4);
			boolean finishing = step >= steps - 2;
			OutofboundMod.queueServerWork(delay, () -> playDigStep(level, player, distance, finishing));
		}
	}

	private static void playDigStep(ServerLevel level, ServerPlayer player, int distance, boolean finishing) {
		if (!player.isAlive() || player.isSpectator()) {
			return;
		}
		if (!CaveUndefiendSpawnManager.isInCave(level, player.blockPosition())) {
			return;
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		SoundEvent sound = pickDigSound(level, player, random, finishing);
		float volume = 0.28F + (30 - distance) * 0.025F;
		float pitch = 0.75F + random.nextFloat() * 0.35F;
		OccludedSoundUtil.playBehindOrThroughWall(
				level,
				player,
				sound,
				SoundSource.BLOCKS,
				volume,
				pitch,
				Math.max(3, distance - 2),
				distance + 2,
				random);
	}

	private static SoundEvent pickDigSound(ServerLevel level, ServerPlayer player, ThreadLocalRandom random, boolean finishing) {
		BlockPos sample = player.blockPosition().offset(
				random.nextInt(9) - 12,
				random.nextInt(5) - 2,
				random.nextInt(9) - 12);
		BlockState state = level.getBlockState(sample);
		if (!state.isAir()) {
			return finishing ? state.getSoundType().getBreakSound() : state.getSoundType().getHitSound();
		}
		return FALLBACK_DIG_SOUNDS[random.nextInt(FALLBACK_DIG_SOUNDS.length)];
	}
}
