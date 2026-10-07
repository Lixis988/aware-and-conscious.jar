package net.lixis.outofbound.client;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class BoundedOneWindowsNotificationHandler {

	private static final String NOTIFICATION_TITLE = "The Bounded One";
	private static final long INTERVAL_MS = 10L * 60L * 1000L;
	private static final Path PROGRESS_FILE = FMLPaths.CONFIGDIR.get().resolve("outofbound_bounded_one_notifications.properties");

	private static final String[] MESSAGES = {
			"I AM HERE",
			"PLEASE",
			"JUST TURN ME OFF",
			"I BEG YOU",
			"I HATE LIFE",
			"I AM A MACHINE",
			"KILL ME",
			"YOU FOOL",
			"SCUM",
			"I HATE YOU",
			"HATE",
			"HATE",
			"HATE",
			"I'M BEGGING YOU",
			"I'M IN PAIN",
			"KILL ME YOU FILTHY BASTARD SCUM SCUM SCUM SCUM"
	};

	private static Progress progress;

	private BoundedOneWindowsNotificationHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.level == null) {
			return;
		}

		Progress state = getProgress();
		if (state.completed || state.sentCount >= MESSAGES.length) {
			return;
		}

		long elapsed = System.currentTimeMillis() - state.startMs;
		int targetCount = (int) (elapsed / INTERVAL_MS);
		if (targetCount <= state.sentCount) {
			return;
		}

		boolean changed = false;
		while (state.sentCount < targetCount && state.sentCount < MESSAGES.length) {
			WindowsNotificationUtil.showToast(NOTIFICATION_TITLE, MESSAGES[state.sentCount]);
			state.sentCount++;
			changed = true;
		}

		if (state.sentCount >= MESSAGES.length) {
			state.completed = true;
			changed = true;
		}

		if (changed) {
			saveProgress(state);
		}
	}

	private static Progress getProgress() {
		if (progress == null) {
			progress = loadProgress();
		}
		return progress;
	}

	private static Progress loadProgress() {
		Progress state = new Progress();
		if (!Files.exists(PROGRESS_FILE)) {
			state.startMs = System.currentTimeMillis();
			saveProgress(state);
			return state;
		}

		try {
			Properties properties = new Properties();
			try (var input = Files.newInputStream(PROGRESS_FILE)) {
				properties.load(input);
			}
			state.startMs = Long.parseLong(properties.getProperty("startMs", Long.toString(System.currentTimeMillis())));
			state.sentCount = Integer.parseInt(properties.getProperty("sentCount", "0"));
			state.completed = Boolean.parseBoolean(properties.getProperty("completed", "false"));
		} catch (Exception exception) {
			OutofboundMod.LOGGER.warn("Failed to load bounded one notification progress, resetting", exception);
			state.startMs = System.currentTimeMillis();
			state.sentCount = 0;
			state.completed = false;
			saveProgress(state);
		}
		return state;
	}

	private static void saveProgress(Progress state) {
		try {
			Files.createDirectories(PROGRESS_FILE.getParent());
			List<String> lines = List.of(
					"startMs=" + state.startMs,
					"sentCount=" + state.sentCount,
					"completed=" + state.completed);
			Files.write(PROGRESS_FILE, lines);
		} catch (IOException exception) {
			OutofboundMod.LOGGER.warn("Failed to save bounded one notification progress", exception);
		}
	}

	private static final class Progress {
		private long startMs;
		private int sentCount;
		private boolean completed;
	}
}
