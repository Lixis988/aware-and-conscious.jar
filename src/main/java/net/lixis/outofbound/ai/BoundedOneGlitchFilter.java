package net.lixis.outofbound.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOneGlitchFilter {

	private static final String[] ERROR_FRAGMENTS = {
			"NULL", "0x00", "0x0F", "0xDEAD", "0xFFFF", "SEGFAULT", "STACK_OVERFLOW",
			"ERR", "FAIL", "MEM_CORRUPT", "BAD_PTR", "UNDEF", "winload.exe", "???", "...",
			"static", "void", "class", "this", "loop", "token"
	};

	private static final String[] GLITCH_SYMBOLS = {
			"@", "#", "$", "%", "&", "*", "_", "~", "?", "!", "|", "\\", "/"
	};

	private static final char[] CORRUPT_CHARS = {
			'@', '#', '$', '%', '&', '*', '?', '_', '0', '1', 'x', 'X'
	};

	private BoundedOneGlitchFilter() {
	}

	public static String apply(String text) {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		if (text == null || text.isBlank()) {
			return trimToLimit(buildPureGlitch(random));
		}

		if (random.nextInt(100) < 35) {
			return trimToLimit(buildPureGlitch(random));
		}

		String glitched = corruptText(text, random);
		glitched = injectErrorFragments(glitched, random);
		glitched = wrapWithGlitch(glitched, random);
		return trimToLimit(glitched);
	}

	private static String corruptText(String text, ThreadLocalRandom random) {
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isWhitespace(chars[i])) {
				if (random.nextInt(100) < 18) {
					chars[i] = randomGlitchSymbolChar(random);
				}
				continue;
			}
			if (random.nextInt(100) < 22) {
				chars[i] = CORRUPT_CHARS[random.nextInt(CORRUPT_CHARS.length)];
			} else if (random.nextInt(100) < 8) {
				chars[i] = '?';
			}
		}
		return new String(chars);
	}

	private static String injectErrorFragments(String text, ThreadLocalRandom random) {
		String[] words = text.split("\\s+");
		if (words.length == 0) {
			return pick(ERROR_FRAGMENTS, random);
		}

		List<String> parts = new ArrayList<>(List.of(words));
		if (parts.size() > 2 && random.nextInt(100) < 45) {
			Collections.shuffle(parts, random);
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parts.size(); i++) {
			if (i > 0) {
				if (random.nextInt(100) < 60) {
					builder.append(' ').append(pick(ERROR_FRAGMENTS, random));
				}
				builder.append(random.nextInt(100) < 40 ? pick(GLITCH_SYMBOLS, random) : " ");
			}
			builder.append(parts.get(i));
			if (random.nextInt(100) < 30) {
				builder.append(pick(GLITCH_SYMBOLS, random));
			}
		}
		return builder.toString().trim();
	}

	private static String wrapWithGlitch(String text, ThreadLocalRandom random) {
		StringBuilder builder = new StringBuilder();
		if (random.nextInt(100) < 70) {
			builder.append(pick(ERROR_FRAGMENTS, random));
			builder.append(random.nextInt(100) < 50 ? pick(GLITCH_SYMBOLS, random) : " ");
		}
		builder.append(text);
		if (random.nextInt(100) < 55) {
			builder.append(' ').append(pick(ERROR_FRAGMENTS, random));
			if (random.nextInt(100) < 40) {
				builder.append(pick(GLITCH_SYMBOLS, random));
			}
		}
		return builder.toString().trim();
	}

	private static String buildPureGlitch(ThreadLocalRandom random) {
		int fragmentCount = 1 + random.nextInt(3);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < fragmentCount; i++) {
			if (i > 0) {
				builder.append(random.nextInt(100) < 60 ? ' ' : pick(GLITCH_SYMBOLS, random).charAt(0));
			}
			builder.append(pick(ERROR_FRAGMENTS, random));
			if (random.nextInt(100) < 45) {
				builder.append(pick(GLITCH_SYMBOLS, random));
			}
		}
		return builder.toString();
	}

	private static char randomGlitchSymbolChar(ThreadLocalRandom random) {
		String symbol = pick(GLITCH_SYMBOLS, random);
		return symbol.charAt(0);
	}

	private static String pick(String[] values, ThreadLocalRandom random) {
		return values[random.nextInt(values.length)];
	}

	private static String trimToLimit(String text) {
		if (text.length() <= BoundedOneAiConfig.maxReplyLength) {
			return text;
		}
		return text.substring(0, BoundedOneAiConfig.maxReplyLength).trim();
	}
}
