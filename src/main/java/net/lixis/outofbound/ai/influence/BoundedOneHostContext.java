package net.lixis.outofbound.ai.influence;

import java.util.List;

public final class BoundedOneHostContext {

	public record DesktopEntry(String name, String type) {
	}

	private final int blockX;
	private final int blockY;
	private final int blockZ;
	private final String dimensionId;
	private final String gameStage;
	private final long mazeIndex;
	private final long worldTime;
	private final List<String> logLines;
	private final List<DesktopEntry> desktopEntries;
	private final BoundedOneAction selectedAction;

	public BoundedOneHostContext(int blockX, int blockY, int blockZ, String dimensionId, String gameStage,
			long mazeIndex, long worldTime, List<String> logLines, List<DesktopEntry> desktopEntries,
			BoundedOneAction selectedAction) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.dimensionId = dimensionId;
		this.gameStage = gameStage;
		this.mazeIndex = mazeIndex;
		this.worldTime = worldTime;
		this.logLines = List.copyOf(logLines);
		this.desktopEntries = List.copyOf(desktopEntries);
		this.selectedAction = selectedAction;
	}

	public int blockX() {
		return blockX;
	}

	public int blockY() {
		return blockY;
	}

	public int blockZ() {
		return blockZ;
	}

	public String dimensionId() {
		return dimensionId;
	}

	public String gameStage() {
		return gameStage;
	}

	public long mazeIndex() {
		return mazeIndex;
	}

	public long worldTime() {
		return worldTime;
	}

	public List<String> logLines() {
		return logLines;
	}

	public List<DesktopEntry> desktopEntries() {
		return desktopEntries;
	}

	public BoundedOneAction selectedAction() {
		return selectedAction;
	}

	public BoundedOneHostContext withAction(BoundedOneAction action) {
		return new BoundedOneHostContext(blockX, blockY, blockZ, dimensionId, gameStage, mazeIndex, worldTime,
				logLines, desktopEntries, action);
	}

	public String toPromptBlock(String availableActions) {
		StringBuilder builder = new StringBuilder();
		builder.append("[OBSERVED]\n");
		builder.append("pos: ").append(blockX).append(' ').append(blockY).append(' ').append(blockZ);
		builder.append(" dim: ").append(dimensionId);
		builder.append(" stage: ").append(gameStage);
		if (mazeIndex > 0L) {
			builder.append(" maze: ").append(mazeIndex);
		}
		builder.append(" time: ").append(worldTime);
		builder.append('\n');
		if (!desktopEntries.isEmpty()) {
			builder.append("desktop: ");
			int shown = 0;
			for (DesktopEntry entry : desktopEntries) {
				if (shown > 0) {
					builder.append(", ");
				}
				builder.append(entry.name()).append(" (").append(entry.type()).append(')');
				shown++;
				if (shown >= 6) {
					break;
				}
			}
			builder.append('\n');
		}
		if (!logLines.isEmpty()) {
			builder.append("log: ");
			builder.append(logLines.get(logLines.size() - 1));
			builder.append('\n');
		}
		builder.append("available_actions: ").append(availableActions);
		builder.append("\nYou may quietly hint at one thing listed above, but keep speaking in plain lonely words.");
		String block = builder.toString();
		if (block.length() > 500) {
			return block.substring(0, 500);
		}
		return block;
	}

	public String pickObservationSnippet() {
		if (!desktopEntries.isEmpty() && (blockX + blockY + blockZ + worldTime) % 2L == 0L) {
			return desktopEntries.get((int) (worldTime % desktopEntries.size())).name();
		}
		return blockX + " " + blockY + " " + blockZ;
	}

	public String pickPureHostDatum(java.util.concurrent.ThreadLocalRandom random) {
		boolean hasDesktop = !desktopEntries.isEmpty();
		boolean hasLog = !logLines.isEmpty();
		if (!hasDesktop && !hasLog) {
			return null;
		}
		if (hasDesktop && hasLog) {
			if (random.nextBoolean()) {
				return desktopEntries.get(random.nextInt(desktopEntries.size())).name();
			}
			return trimLogFragment(logLines.get(random.nextInt(logLines.size())));
		}
		if (hasDesktop) {
			return desktopEntries.get(random.nextInt(desktopEntries.size())).name();
		}
		return trimLogFragment(logLines.get(random.nextInt(logLines.size())));
	}

	public boolean hasHostFileOrLogData() {
		return !desktopEntries.isEmpty() || !logLines.isEmpty();
	}

	private static String trimLogFragment(String line) {
		if (line == null || line.isBlank()) {
			return "NULL";
		}
		String trimmed = line.trim();
		if (trimmed.length() > 72) {
			trimmed = trimmed.substring(trimmed.length() - 72).trim();
		}
		return trimmed;
	}

	public String pickLogSnippet() {
		if (logLines.isEmpty()) {
			return "NULL";
		}
		return logLines.get((int) (worldTime % logLines.size()));
	}
}
