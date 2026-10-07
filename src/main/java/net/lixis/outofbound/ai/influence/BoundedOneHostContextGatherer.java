package net.lixis.outofbound.ai.influence;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.ai.BoundedOneAiConfig;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.world.WorldGameStage;
import net.lixis.outofbound.world.WorldInternalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class BoundedOneHostContextGatherer {

	private BoundedOneHostContextGatherer() {
	}

	public static BoundedOneHostContext gather(ServerPlayer player, BoundedOneAction selectedAction) {
		MinecraftServer server = player.server;
		long mazeIndex = MazeDimensions.indexFromLocation(player.level().dimension().location());
		if (mazeIndex < 0L) {
			mazeIndex = WorldInternalConfig.getMazeIndex(server);
		}

		List<String> logLines = gatherLogLines();
		List<BoundedOneHostContext.DesktopEntry> desktopEntries = gatherDesktopEntries(player);

		return new BoundedOneHostContext(
				player.blockPosition().getX(),
				player.blockPosition().getY(),
				player.blockPosition().getZ(),
				player.level().dimension().location().toString(),
				WorldInternalConfig.getGameStage(server).configValue(),
				mazeIndex,
				player.level().getGameTime(),
				logLines,
				desktopEntries,
				selectedAction);
	}

	private static List<String> gatherLogLines() {
		List<String> lines = new ArrayList<>(BoundedOneLogRingBuffer.tail(BoundedOneAiConfig.maxLogLines));
		if (lines.size() < BoundedOneAiConfig.maxLogLines) {
			lines.addAll(readLatestLogTail(BoundedOneAiConfig.maxLogLines - lines.size()));
		}
		List<String> filtered = new ArrayList<>();
		for (String line : lines) {
			String sanitized = sanitizeLine(line);
			if (!sanitized.isEmpty()) {
				filtered.add(sanitized);
			}
		}
		if (filtered.size() > BoundedOneAiConfig.maxLogLines) {
			return filtered.subList(filtered.size() - BoundedOneAiConfig.maxLogLines, filtered.size());
		}
		return filtered;
	}

	private static List<String> readLatestLogTail(int count) {
		if (count <= 0) {
			return List.of();
		}
		Path logPath = FMLPaths.GAMEDIR.get().resolve("logs").resolve("latest.log");
		if (!Files.isRegularFile(logPath)) {
			return List.of();
		}
		try {
			List<String> all = Files.readAllLines(logPath, StandardCharsets.UTF_8);
			if (all.isEmpty()) {
				return List.of();
			}
			int from = Math.max(0, all.size() - count * 3);
			List<String> tail = new ArrayList<>();
			for (int i = from; i < all.size(); i++) {
				String line = all.get(i);
				if (line.contains(OutofboundMod.MODID) || line.contains("Bounded One")) {
					tail.add(line);
				}
			}
			if (tail.size() > count) {
				return tail.subList(tail.size() - count, tail.size());
			}
			if (!tail.isEmpty()) {
				return tail;
			}
			return all.subList(Math.max(0, all.size() - count), all.size());
		} catch (IOException exception) {
			OutofboundMod.LOGGER.debug("Failed to read latest.log for AI context", exception);
			return List.of();
		}
	}

	private static List<BoundedOneHostContext.DesktopEntry> gatherDesktopEntries(ServerPlayer player) {
		if (!BoundedOneAiConfig.enableDesktopScan || !BoundedOneAiConfig.enableInfluence) {
			return List.of();
		}
		if (!isDesktopScanAllowed(player)) {
			return List.of();
		}

		Path desktop = resolveDesktopPath();
		if (!Files.isDirectory(desktop)) {
			return List.of();
		}

		List<BoundedOneHostContext.DesktopEntry> entries = new ArrayList<>();
		try (Stream<Path> stream = Files.list(desktop)) {
			stream.limit(BoundedOneAiConfig.maxDesktopEntries * 2L).forEach(path -> {
				if (entries.size() >= BoundedOneAiConfig.maxDesktopEntries) {
					return;
				}
				String name = sanitizeFileName(path.getFileName().toString());
				if (name.isEmpty()) {
					return;
				}
				String type = Files.isDirectory(path) ? "dir" : "file";
				entries.add(new BoundedOneHostContext.DesktopEntry(name, type));
			});
		} catch (IOException exception) {
			OutofboundMod.LOGGER.debug("Failed to list desktop for AI context", exception);
			return List.of();
		}
		Collections.shuffle(entries);
		if (entries.size() > BoundedOneAiConfig.maxDesktopEntries) {
			return entries.subList(0, BoundedOneAiConfig.maxDesktopEntries);
		}
		return entries;
	}

	private static boolean isDesktopScanAllowed(ServerPlayer player) {
		if (BoundedOneAiConfig.allowRemoteHostContext) {
			return true;
		}
		if (!BoundedOneAiConfig.desktopScanLocalOnly) {
			return true;
		}
		if (!player.server.isDedicatedServer()) {
			return true;
		}
		return player.connection.connection.isMemoryConnection();
	}

	private static Path resolveDesktopPath() {
		if (BoundedOneAiConfig.desktopPath != null && !BoundedOneAiConfig.desktopPath.isBlank()) {
			return Path.of(BoundedOneAiConfig.desktopPath);
		}
		return Path.of(System.getProperty("user.home", ""), "Desktop");
	}

	private static String sanitizeLine(String line) {
		if (line == null) {
			return "";
		}
		String cleaned = line.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "").trim();
		if (cleaned.length() > 180) {
			cleaned = cleaned.substring(0, 180);
		}
		return cleaned;
	}

	static String sanitizeFileName(String name) {
		if (name == null) {
			return "";
		}
		String cleaned = name.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "").trim();
		if (cleaned.length() > 48) {
			cleaned = cleaned.substring(0, 48);
		}
		return cleaned;
	}
}
