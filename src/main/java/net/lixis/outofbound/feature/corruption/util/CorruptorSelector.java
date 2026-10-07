package net.lixis.outofbound.feature.corruption.util;

import net.lixis.outofbound.feature.corruption.CorruptionDayStages;
import net.lixis.outofbound.feature.corruption.corruptors.ICorruptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class CorruptorSelector {

	private CorruptorSelector() {
	}

	public static List<ICorruptor> eligibleForLevel(List<ICorruptor> corruptors, float level) {
		return eligibleForLevel(corruptors, level, Long.MAX_VALUE);
	}

	public static List<ICorruptor> eligibleForLevel(List<ICorruptor> corruptors, float level, long daysSince) {
		List<ICorruptor> eligible = new ArrayList<>();
		for (ICorruptor corruptor : corruptors) {
			if (!CorruptionDayStages.allowsAmbient(corruptor, daysSince)) {
				continue;
			}
			if (CorruptionScaling.effectiveWeight(level, corruptor.minLevel(), corruptor.weight()) > 0) {
				eligible.add(corruptor);
			}
		}
		return eligible;
	}

	public static List<ICorruptor> eligiblePreview(List<ICorruptor> corruptors, float level, long daysSince) {
		List<ICorruptor> eligible = new ArrayList<>();
		for (ICorruptor corruptor : corruptors) {
			if (!CorruptionDayStages.allowsPreview(corruptor, daysSince)) {
				continue;
			}
			if (CorruptionScaling.effectiveWeight(level, corruptor.minLevel(), corruptor.weight()) > 0) {
				eligible.add(corruptor);
			}
		}
		return eligible;
	}

	public static ICorruptor pickWeighted(List<ICorruptor> eligible, float level, ThreadLocalRandom random) {
		if (eligible.isEmpty()) {
			throw new IllegalArgumentException("eligible corruptors must not be empty");
		}
		int total = 0;
		for (ICorruptor corruptor : eligible) {
			total += CorruptionScaling.effectiveWeight(level, corruptor.minLevel(), corruptor.weight());
		}
		if (total <= 0) {
			return eligible.get(random.nextInt(eligible.size()));
		}
		int roll = random.nextInt(total);
		for (ICorruptor corruptor : eligible) {
			roll -= CorruptionScaling.effectiveWeight(level, corruptor.minLevel(), corruptor.weight());
			if (roll < 0) {
				return corruptor;
			}
		}
		return eligible.get(eligible.size() - 1);
	}
}
