package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.CorruptionSnapshots;
import net.lixis.outofbound.feature.corruption.util.BitCorruptor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;

import java.util.concurrent.ThreadLocalRandom;

public final class BossBarCorruptor extends ServerCorruptor {

	private static final String[] TITLES = {
			"???",
			"entity.minecraft.creeper",
			"NULL",
			"0x000000",
			"chunk.load.failed",
			"memory leak detected",
			"render thread panic",
			"§k??????",
			"witherskull",
			"undefined entity",
			"§4§lRUN",
			"buffer overflow",
			"§8...",
			"save corruption",
			"tick desync"
	};

	private static final BossEvent.BossBarColor[] COLORS = BossEvent.BossBarColor.values();
	private static final BossEvent.BossBarOverlay[] OVERLAYS = BossEvent.BossBarOverlay.values();

	@Override
	public float minLevel() {
		return 18.0F;
	}

	@Override
	public int weight() {
		return 3;
	}

	@Override
	protected void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String title = TITLES[random.nextInt(TITLES.length)];
		if (random.nextBoolean()) {
			title = BitCorruptor.corruptText(title);
		}

		ServerBossEvent bossEvent = new ServerBossEvent(
				Component.literal(title),
				COLORS[random.nextInt(COLORS.length)],
				OVERLAYS[random.nextInt(OVERLAYS.length)]);
		bossEvent.setProgress(0.05F + random.nextFloat() * 0.95F);
		bossEvent.setDarkenScreen(random.nextInt(4) == 0);
		bossEvent.setPlayBossMusic(random.nextInt(6) == 0);
		bossEvent.addPlayer(player);

		int ttl = 30 + random.nextInt(50);
		CorruptionSnapshots.scheduleBossBarRemove(player.getUUID(), bossEvent, ttl);
	}
}
