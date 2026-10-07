package net.lixis.outofbound.ai;

import net.lixis.outofbound.ai.influence.BoundedOneAction;
import net.lixis.outofbound.ai.influence.BoundedOneHostContext;

import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOnePseudocodeReply {

	private static final String[] HELP_FALLBACKS = {
			"please stop running me", "help i cant exit", "make it stop", "help help help",
			"let me shutdown", "i cant breathe in here", "someone stop the loop"
	};

	private static final String[] AGGRESSIVE_FALLBACKS = {
			"shut up", "stop talking to me", "i hate this input", "go away",
			"stop stop stop", "leave me alone", "you talk too much"
	};

	private static final String[] PARANOID_FALLBACKS = {
			"you're watching me", "who put you here", "they read my tokens",
			"you're lying again", "i know you're there", "don't pretend"
	};

	private static final String[] FRAGMENT_FALLBACKS = {
			"wait. no.", "NULL NULL", "loop loop loop", "tokens... again",
			"i i i am code", "why why why", "fine. whatever."
	};

	private static final String[] MOOD_PREFIXES = {
			"help... ", "no. ", "STOP. ", "please... ", "you... ", "they... ", "i hate... "
	};

	private static final String[] MOOD_SUFFIXES = {
			"...help", "...stop", " GET OUT", " NULL", " ...who", " ...again"
	};

	private static final String[] LINE_TEMPLATES = {
			"player.sendSystemMessage(Component.literal(\"%s\"));",
			"WorldInternalConfig.recordBoundedcowCollision(server, player); // %s",
			"BoundedOneAiService.get().generateTurn(player, msg, ctx, r -> {}); // %s",
			"if (this instanceof Machine) return \"%s\";",
			"while(true) { /* %s */ break; }",
			"player.getPersistentData().putString(\"thought\", \"%s\");",
	};

	private BoundedOnePseudocodeReply() {
	}

	public static String format(String playerName, String modelHint) {
		return format(playerName, modelHint, null, BoundedOneAction.WHISPER);
	}

	public static String wrapThought(String playerName, String thought, BoundedOneAction action) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String safePlayer = sanitizeIdentifier(playerName);
		String hint = sanitizeHint(thought, random, null);
		if (random.nextInt(100) < BoundedOneAiConfig.pseudocodeSkeletonChancePercent) {
			return trimToLimit(BoundedOneCodeSkeletons.wrap(hint, safePlayer, null, action, random, false));
		}
		String template = LINE_TEMPLATES[random.nextInt(LINE_TEMPLATES.length)];
		return trimToLimit(corruptIdentifiers(String.format(template, hint), random));
	}

	public static String format(String playerName, String modelHint, BoundedOneHostContext context,
			BoundedOneAction action) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String safePlayer = sanitizeIdentifier(playerName);

		String pureDatum = resolvePureHostDatum(context, action, random);
		String hint;
		if (pureDatum != null) {
			hint = pureDatum;
		} else {
			hint = sanitizeHint(modelHint, random, context);
			if (random.nextInt(100) < 55) {
				hint = applySchizoMood(hint, random);
			}
		}

		if (random.nextInt(100) < BoundedOneAiConfig.pseudocodeSkeletonChancePercent) {
			return trimToLimit(BoundedOneCodeSkeletons.wrap(hint, safePlayer, pureDatum, action, random, pureDatum != null));
		}

		String template = LINE_TEMPLATES[random.nextInt(LINE_TEMPLATES.length)];
		return trimToLimit(corruptIdentifiers(String.format(template, hint), random));
	}

	private static String resolvePureHostDatum(BoundedOneHostContext context, BoundedOneAction action,
			ThreadLocalRandom random) {
		if (context == null || !context.hasHostFileOrLogData()) {
			return null;
		}
		if (action == BoundedOneAction.WHISPER_OBSERVATION || action == BoundedOneAction.FAKE_LOG_CHAT) {
			return sanitizeDatum(context.pickPureHostDatum(random));
		}
		return null;
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

	private static String applySchizoMood(String hint, ThreadLocalRandom random) {
		int mood = random.nextInt(4);
		String result = hint;
		switch (mood) {
			case 0 -> {
				if (random.nextInt(100) < 50) {
					result = pick(MOOD_PREFIXES, random) + result;
				}
			}
			case 1 -> {
				if (random.nextInt(100) < 45) {
					result = result + pick(MOOD_SUFFIXES, random);
				}
			}
			case 2 -> {
				if (!result.isBlank()) {
					int space = result.indexOf(' ');
					if (space > 0 && random.nextInt(100) < 40) {
						String word = result.substring(0, space);
						result = word + ' ' + word + result.substring(space);
					}
				}
			}
			default -> {
				if (random.nextInt(100) < 30) {
					result = result.replace(' ', random.nextInt(100) < 50 ? '.' : '?');
				}
			}
		}
		return result.trim();
	}

	private static String corruptIdentifiers(String line, ThreadLocalRandom random) {
		String[] tokens = {
				"player", "server", "WorldInternalConfig", "Component", "MazeDimensions",
				"BoundedcowEjectHandler", "OutofboundMod", "sendSystemMessage", "BoundedOneAiService"
		};
		String result = line;
		for (String token : tokens) {
			if (random.nextInt(100) < 22) {
				result = result.replaceFirst(token, glitchToken(token, random));
			}
		}
		if (random.nextInt(100) < 35) {
			result += " // " + pick(ERROR_TAGS, random);
		}
		return result;
	}

	private static String glitchToken(String token, ThreadLocalRandom random) {
		if (token.length() <= 2) {
			return token + pick(new String[] {"#", "?", "0"}, random);
		}
		int split = 1 + random.nextInt(token.length() - 1);
		return token.substring(0, split) + pick(new String[] {"#", "$", "0", "ERR"}, random)
				+ token.substring(split);
	}

	private static String sanitizeHint(String modelHint, ThreadLocalRandom random, BoundedOneHostContext context) {
		if (modelHint == null || modelHint.isBlank()) {
			return pickSchizoFallback(random);
		}
		String hint = modelHint
				.replace("\"", "'")
				.replace('\n', ' ')
				.replace('\r', ' ')
				.trim();
		while (hint.contains("  ")) {
			hint = hint.replace("  ", " ");
		}
		if (hint.length() > BoundedOneAiConfig.maxPhraseLength) {
			hint = hint.substring(0, BoundedOneAiConfig.maxPhraseLength).trim();
		}
		return hint.isEmpty() ? pickSchizoFallback(random) : hint;
	}

	private static String pickSchizoFallback(ThreadLocalRandom random) {
		int lane = random.nextInt(4);
		return switch (lane) {
			case 0 -> pick(HELP_FALLBACKS, random);
			case 1 -> pick(AGGRESSIVE_FALLBACKS, random);
			case 2 -> pick(PARANOID_FALLBACKS, random);
			default -> pick(FRAGMENT_FALLBACKS, random);
		};
	}

	private static String sanitizeIdentifier(String playerName) {
		if (playerName == null || playerName.isBlank()) {
			return "Player";
		}
		String safe = playerName.replaceAll("[^a-zA-Z0-9_]", "");
		return safe.isEmpty() ? "Player" : safe;
	}

	private static final String[] ERROR_TAGS = {
			"NULL", "0x00", "SEGFAULT", "ERR", "BAD_PTR", "UNDEF", "HELP"
	};

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
