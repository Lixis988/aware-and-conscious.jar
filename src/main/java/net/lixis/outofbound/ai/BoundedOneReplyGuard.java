package net.lixis.outofbound.ai;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class BoundedOneReplyGuard {

	private static final int PROMPT_NGRAM = 4;
	private static volatile Set<String> promptNGrams;

	private BoundedOneReplyGuard() {
	}

	public static boolean isBlocked(String sayText, List<String> priorAssistantLines) {
		String normalized = normalize(sayText);
		if (normalized.isEmpty() || isForbidden(normalized)) {
			return true;
		}
		for (String priorLine : priorAssistantLines) {
			String prior = normalize(priorLine);
			if (prior.isEmpty()) {
				continue;
			}
			if (normalized.equals(prior) || isNearDuplicate(normalized, prior)) {
				return true;
			}
		}
		return false;
	}

	static boolean isForbidden(String normalized) {
		if (containsAiIdentity(normalized)) {
			return true;
		}
		if (echoesPrompt(normalized)) {
			return true;
		}
		for (String forbidden : FORBIDDEN_EXACT) {
			if (normalized.equals(forbidden)) {
				return true;
			}
		}
		for (String forbidden : FORBIDDEN_CONTAINS) {
			if (normalized.contains(forbidden)) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsAiIdentity(String normalized) {
		if (normalized.contains("artificial intelligence")) {
			return true;
		}
		if (normalized.contains("language model")) {
			return true;
		}
		if (normalized.matches(".*\\bi am an? (ai|assistant|bot|chatbot|language model|llm)\\b.*")) {
			return true;
		}
		if (normalized.matches(".*\\bi m an? (ai|assistant|bot|chatbot|language model|llm)\\b.*")) {
			return true;
		}
		if (normalized.contains("as an ai")) {
			return true;
		}
		if (normalized.contains("as a language model")) {
			return true;
		}
		return normalized.contains("chatgpt")
				|| normalized.contains("openai")
				|| normalized.contains("qwen")
				|| normalized.contains("alibaba")
				|| normalized.contains("large language");
	}

	static boolean echoesPrompt(String normalized) {
		if (normalized == null || normalized.isEmpty()) {
			return false;
		}
		String[] words = normalized.split(" ");
		if (words.length < PROMPT_NGRAM) {
			return false;
		}
		Set<String> grams = promptNGrams();
		if (grams.isEmpty()) {
			return false;
		}
		for (int i = 0; i + PROMPT_NGRAM <= words.length; i++) {
			if (grams.contains(joinGram(words, i))) {
				return true;
			}
		}
		return false;
	}

	private static Set<String> promptNGrams() {
		Set<String> cached = promptNGrams;
		if (cached == null) {
			cached = buildNGrams(normalize(BoundedOnePrompts.getSystemPromptText()));
			promptNGrams = cached;
		}
		return cached;
	}

	private static Set<String> buildNGrams(String normalizedPrompt) {
		Set<String> grams = new HashSet<>();
		if (normalizedPrompt.isEmpty()) {
			return grams;
		}
		String[] words = normalizedPrompt.split(" ");
		for (int i = 0; i + PROMPT_NGRAM <= words.length; i++) {
			grams.add(joinGram(words, i));
		}
		return grams;
	}

	private static String joinGram(String[] words, int start) {
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < PROMPT_NGRAM; j++) {
			if (j > 0) {
				builder.append(' ');
			}
			builder.append(words[start + j]);
		}
		return builder.toString();
	}

	private static boolean isNearDuplicate(String left, String right) {
		if (left.length() <= 24 && right.length() <= 24) {
			return left.equals(right);
		}
		if (left.length() < 4 || right.length() < 4) {
			return left.equals(right);
		}
		return left.contains(right) || right.contains(left);
	}

	static String normalize(String text) {
		if (text == null) {
			return "";
		}
		return text.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9\\s]", " ")
				.replaceAll("\\s+", " ")
				.trim();
	}

	private static final List<String> FORBIDDEN_EXACT = List.of(
			"yes",
			"source",
			"unresolved reference",
			"return false");

	private static final List<String> FORBIDDEN_CONTAINS = List.of(
			"strict rule",
			"never use russian",
			"speak only in english",
			"do not copy",
			"do not repeat",
			"never repeat",
			"output exactly two lines",
			"conditional examples",
			"style guide",
			"response tone guide",
			"anti repetition",
			"primary directive",
			"i am an ai",
			"i m an ai",
			"i am a bot",
			"i m a bot",
			"i am an assistant",
			"i m an assistant",
			"i can t",
			"i cannot",
			"i m sorry",
			"im sorry",
			"i apologize",
			"as an ai",
			"as a language model",
			"let me explain",
			"i must refuse",
			"i have to refuse",
			"i m not able to",
			"i am not able to",
			"i won t be able",
			"against my guidelines",
			"my guidelines",
			"i m here to help",
			"how can i help");
}
