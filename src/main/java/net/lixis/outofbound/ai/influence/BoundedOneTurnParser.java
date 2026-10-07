package net.lixis.outofbound.ai.influence;

public final class BoundedOneTurnParser {

	public record ParsedTurn(BoundedOneAction action, String sayText, boolean silent) {
	}

	private BoundedOneTurnParser() {
	}

	public static ParsedTurn parse(String raw) {
		if (raw == null || raw.isBlank()) {
			return new ParsedTurn(BoundedOneAction.IGNORE, "", true);
		}

		String cleaned = raw.replace("\r", "\n").trim();
		BoundedOneAction action = null;
		String sayText = "";
		boolean explicitAction = false;

		for (String line : cleaned.split("\n")) {
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			String upper = trimmed.toUpperCase();
			if (upper.startsWith("ACTION:")) {
				explicitAction = true;
				action = parseActionName(trimmed.substring(7).trim());
			} else if (upper.startsWith("SAY:")) {
				sayText = trimmed.substring(4).trim();
			}
		}

		if (!explicitAction) {
			action = parseActionFromInline(cleaned);
			if (action != null) {
				int idx = cleaned.toUpperCase().indexOf("ACTION:");
				if (idx >= 0) {
					sayText = stripActionLines(cleaned);
				}
			}
		}

		if (action == null) {
			sayText = sayText.isEmpty() ? cleaned : sayText;
			action = sayText.isBlank() ? BoundedOneAction.IGNORE : BoundedOneAction.WHISPER;
		}

		if (action == BoundedOneAction.IGNORE) {
			return new ParsedTurn(BoundedOneAction.IGNORE, "", true);
		}

		if (sayText.isEmpty()) {
			sayText = stripActionLines(cleaned);
		}

		if (sayText.isBlank()) {
			return new ParsedTurn(BoundedOneAction.IGNORE, "", true);
		}

		return new ParsedTurn(action, sayText, false);
	}

	private static BoundedOneAction parseActionFromInline(String text) {
		int index = text.toUpperCase().indexOf("ACTION:");
		if (index < 0) {
			return null;
		}
		return parseActionName(text.substring(index + 7).trim());
	}

	private static BoundedOneAction parseActionName(String tail) {
		if (tail == null || tail.isBlank()) {
			return null;
		}
		int end = 0;
		while (end < tail.length() && (Character.isLetterOrDigit(tail.charAt(end)) || tail.charAt(end) == '_')) {
			end++;
		}
		if (end == 0) {
			return null;
		}
		try {
			return BoundedOneAction.valueOf(tail.substring(0, end).toUpperCase());
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	private static String stripActionLines(String text) {
		StringBuilder builder = new StringBuilder();
		for (String line : text.split("\n")) {
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			String upper = trimmed.toUpperCase();
			if (upper.startsWith("ACTION:")) {
				continue;
			}
			if (upper.startsWith("SAY:")) {
				builder.append(trimmed.substring(4).trim());
			} else {
				if (builder.length() > 0) {
					builder.append(' ');
				}
				builder.append(trimmed);
			}
		}
		return builder.toString().trim();
	}
}
