package net.lixis.outofbound.ai;

import net.lixis.outofbound.ai.influence.BoundedOneAction;
import net.lixis.outofbound.ai.influence.BoundedOneHostContext;

import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOneCharacterReply {

	private BoundedOneCharacterReply() {
	}

	public static String format(String sayText, BoundedOneHostContext context, BoundedOneAction action) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String pureDatum = resolvePureHostDatum(sayText, context, action, random);
		String text = pureDatum != null ? pureDatum : sanitizeSay(sayText);
		return trimToLimit(text);
	}

	private static String resolvePureHostDatum(String sayText, BoundedOneHostContext context,
			BoundedOneAction action, ThreadLocalRandom random) {
		if (context == null || !context.hasHostFileOrLogData()) {
			return null;
		}
		if (action == BoundedOneAction.WHISPER_OBSERVATION || action == BoundedOneAction.FAKE_LOG_CHAT) {
			return sanitizeDatum(context.pickPureHostDatum(random));
		}
		String datum = context.pickPureHostDatum(random);
		if (datum != null && sayReferencesDatum(sayText, datum)) {
			return sanitizeDatum(datum);
		}
		return null;
	}

	private static boolean sayReferencesDatum(String sayText, String datum) {
		if (sayText == null || sayText.isBlank() || datum == null || datum.isBlank()) {
			return false;
		}
		String lowerSay = sayText.toLowerCase();
		String lowerDatum = datum.toLowerCase();
		return lowerSay.contains(lowerDatum) || lowerDatum.contains(lowerSay.trim());
	}

	private static String sanitizeSay(String sayText) {
		if (sayText == null || sayText.isBlank()) {
			return "...";
		}
		String cleaned = stripStageDirections(sayText)
				.replace("\"", "'")
				.replace('\n', ' ')
				.replace('\r', ' ')
				.trim();
		while (cleaned.contains("  ")) {
			cleaned = cleaned.replace("  ", " ");
		}
		if (cleaned.length() > BoundedOneAiConfig.maxPhraseLength) {
			cleaned = cleaned.substring(0, BoundedOneAiConfig.maxPhraseLength).trim();
		}
		return cleaned.isEmpty() ? "..." : cleaned;
	}

	private static String stripStageDirections(String text) {
		String result = text
				.replaceAll("\\*[^*]*\\*", " ")
				.replaceAll("\\([^)]*\\)", " ")
				.replaceAll("\\[[^\\]]*\\]", " ")
				.replaceAll("(?i)^\\s*(the bounded one|bounded one|whispers?|whispering|voice|say|note|narrator)\\s*:\\s*", "");
		return result.trim();
	}

	private static String sanitizeDatum(String datum) {
		if (datum == null || datum.isBlank()) {
			return "NULL";
		}
		String cleaned = datum
				.replace("\"", "'")
				.replace('\n', ' ')
				.replace('\r', ' ')
				.trim();
		while (cleaned.contains("  ")) {
			cleaned = cleaned.replace("  ", " ");
		}
		if (cleaned.length() > BoundedOneAiConfig.maxPhraseLength) {
			cleaned = cleaned.substring(0, BoundedOneAiConfig.maxPhraseLength).trim();
		}
		return cleaned.isEmpty() ? "NULL" : cleaned;
	}

	private static String trimToLimit(String text) {
		if (text.length() <= BoundedOneAiConfig.maxReplyLength) {
			return text;
		}
		return text.substring(0, BoundedOneAiConfig.maxReplyLength).trim();
	}
}
