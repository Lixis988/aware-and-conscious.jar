package net.lixis.outofbound.world;

public enum FinaleState {
	NONE("none"),
	ACTIVE("active"),
	DONE("done");

	private final String value;

	FinaleState(String value) {
		this.value = value;
	}

	public String configValue() {
		return value;
	}

	public static FinaleState fromString(String raw) {
		if (raw == null) {
			return NONE;
		}
		for (FinaleState state : values()) {
			if (state.value.equalsIgnoreCase(raw)) {
				return state;
			}
		}
		return NONE;
	}
}
