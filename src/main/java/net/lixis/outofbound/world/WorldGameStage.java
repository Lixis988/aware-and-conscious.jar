package net.lixis.outofbound.world;

public enum WorldGameStage {
	START(0),
	SINKING(1),
	IN_MAZE(2);

	private final byte id;

	WorldGameStage(int id) {
		this.id = (byte) id;
	}

	public byte id() {
		return id;
	}

	public static WorldGameStage fromId(byte id) {
		for (WorldGameStage stage : values()) {
			if (stage.id == id) {
				return stage;
			}
		}
		return START;
	}

	public static WorldGameStage fromString(String value) {
		if (value == null) {
			return START;
		}
		return switch (value) {
			case "sinking", "boundedcow_collision" -> SINKING;
			case "in_maze" -> IN_MAZE;
			default -> START;
		};
	}

	public String configValue() {
		return switch (this) {
			case START -> "start";
			case SINKING -> "sinking";
			case IN_MAZE -> "in_maze";
		};
	}
}
