package net.lixis.outofbound.ai.influence;

public enum BoundedOneAction {
	IGNORE(0, false),
	WHISPER(40, false),
	WHISPER_OBSERVATION(18, false),
	AMBIENT_SOUND(14, false),
	FAKE_LOG_CHAT(10, false),
	CLIENT_GLITCH(8, false),
	SPAWN_PRESENCE(6, false),
	SCARE_DIALOG(2, true),
	DISCONNECT_MESSAGE(1, true);

	private final int weight;
	private final boolean heavy;

	BoundedOneAction(int weight, boolean heavy) {
		this.weight = weight;
		this.heavy = heavy;
	}

	public int weight() {
		return weight;
	}

	public boolean heavy() {
		return heavy;
	}
}
