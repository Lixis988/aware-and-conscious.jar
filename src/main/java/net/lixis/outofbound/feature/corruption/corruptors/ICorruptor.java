package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;

public interface ICorruptor {

	void run(CorruptionRunContext ctx);

	float minLevel();

	default int weight() {
		return 1;
	}
}
