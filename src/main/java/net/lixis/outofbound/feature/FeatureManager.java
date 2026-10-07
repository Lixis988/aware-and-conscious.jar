package net.lixis.outofbound.feature;

import net.lixis.outofbound.feature.corruption.CorruptionClientEngine;
import net.lixis.outofbound.feature.corruption.CorruptionEngine;
import net.lixis.outofbound.feature.corruption.CorruptionFeature;
import net.minecraft.server.MinecraftServer;

public final class FeatureManager {

	private FeatureManager() {
	}

	public static void init() {
		CorruptionEngine.init();
		CorruptionClientEngine.init();
	}

	public static void serverTick(MinecraftServer server) {
		CorruptionFeature.serverTick(server);
	}

	public static void clientTick() {
		CorruptionFeature.clientTick();
	}
}
