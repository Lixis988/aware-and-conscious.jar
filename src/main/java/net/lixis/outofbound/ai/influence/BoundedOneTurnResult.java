package net.lixis.outofbound.ai.influence;

public final class BoundedOneTurnResult {

	private final BoundedOneAction action;
	private final String sayText;
	private final boolean silent;
	private final String displayText;

	public BoundedOneTurnResult(BoundedOneAction action, String sayText, boolean silent, String displayText) {
		this.action = action;
		this.sayText = sayText == null ? "" : sayText;
		this.silent = silent;
		this.displayText = displayText == null ? "" : displayText;
	}

	public BoundedOneAction action() {
		return action;
	}

	public String sayText() {
		return sayText;
	}

	public boolean silent() {
		return silent;
	}

	public String displayText() {
		return displayText;
	}
}
