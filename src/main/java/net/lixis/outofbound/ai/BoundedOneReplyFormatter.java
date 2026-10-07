package net.lixis.outofbound.ai;

import net.lixis.outofbound.ai.influence.BoundedOneAction;
import net.lixis.outofbound.ai.influence.BoundedOneHostContext;

import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOneReplyFormatter {

	private BoundedOneReplyFormatter() {
	}

	public static String format(String playerName, String modelText) {
		return format(playerName, modelText, null, BoundedOneAction.WHISPER);
	}

	public static String format(String playerName, String modelText, BoundedOneHostContext context,
			BoundedOneAction action) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String raw = modelText == null ? "" : modelText.trim();
		if (raw.isEmpty() || random.nextInt(100) < BoundedOneAiConfig.glitchReplyChancePercent) {
			return BoundedOneChaosText.generate(random);
		}
		return BoundedOneChaosText.corrupt(raw, random);
	}
}
