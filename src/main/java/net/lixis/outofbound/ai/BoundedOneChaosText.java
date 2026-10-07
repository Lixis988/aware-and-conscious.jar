package net.lixis.outofbound.ai;

import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOneChaosText {

	private static final String[] SHARDS = {
			"0x", "null", "???", "---", "///", "...", "ERR", "SEG", "VOID", "LOOP",
			"@@", "##", "~~", "[[", "]]", "{{", "}}", "fn", "ret", "brk", "jmp",
			"who", "no", "stop", "help", "out", "in", "me", "you", "they", "it"
	};

	private static final char[] GLITCH = {
			'¦', '§', '¤', '±', '×', '÷', '░', '▒', '▓', '█', '▀', '▄', '■', '□',
			'◆', '◇', '○', '●', '※', '†', '‡', '•', '·', '˚', 'º', 'ª', 'ø', 'Ø'
	};

	private BoundedOneChaosText() {
	}

	public static String generate(ThreadLocalRandom random) {
		int parts = 3 + random.nextInt(8);
		StringBuilder out = new StringBuilder(48);
		for (int i = 0; i < parts; i++) {
			if (i > 0 && random.nextBoolean()) {
				out.append(' ');
			}
			int roll = random.nextInt(100);
			if (roll < 35) {
				out.append(SHARDS[random.nextInt(SHARDS.length)]);
			} else if (roll < 55) {
				out.append(Integer.toHexString(random.nextInt(0xFFFF)));
			} else if (roll < 75) {
				out.append(GLITCH[random.nextInt(GLITCH.length)]);
			} else {
				int n = 2 + random.nextInt(6);
				for (int c = 0; c < n; c++) {
					out.append((char) ('a' + random.nextInt(26)));
				}
			}
		}
		return trim(out.toString());
	}

	public static String corrupt(String input, ThreadLocalRandom random) {
		String cleaned = input
				.replaceAll("(?i)ACTION\\s*:\\s*\\w+", "")
				.replaceAll("(?i)SAY\\s*:", "")
				.replaceAll("[\\r\\n]+", " ")
				.trim();
		if (cleaned.length() < 2) {
			return generate(random);
		}
		StringBuilder out = new StringBuilder(cleaned.length() + 16);
		for (int i = 0; i < cleaned.length() && out.length() < BoundedOneAiConfig.maxReplyLength; i++) {
			char ch = cleaned.charAt(i);
			int roll = random.nextInt(100);
			if (roll < 12) {
				continue;
			}
			if (roll < 22) {
				out.append(GLITCH[random.nextInt(GLITCH.length)]);
				continue;
			}
			if (roll < 30 && Character.isLetter(ch)) {
				out.append(random.nextBoolean() ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
				if (random.nextInt(100) < 40) {
					out.append(GLITCH[random.nextInt(GLITCH.length)]);
				}
				continue;
			}
			if (roll < 36) {
				out.append(SHARDS[random.nextInt(SHARDS.length)]);
				continue;
			}
			out.append(ch);
		}
		String result = out.toString().trim();
		if (result.length() < 2) {
			return generate(random);
		}
		return trim(result);
	}

	private static String trim(String text) {
		int max = Math.max(16, BoundedOneAiConfig.maxReplyLength);
		if (text.length() <= max) {
			return text;
		}
		return text.substring(0, max);
	}
}
