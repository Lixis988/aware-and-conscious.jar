package net.lixis.outofbound.feature.corruption;

import net.lixis9.eventjar.DarknessConfig;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.server.MinecraftServer;

public final class CorruptionFeature {

	private CorruptionFeature() {
	}

	public static void serverTick(MinecraftServer server) {
		CorruptionSnapshots.tick(server);
		if (!DarknessConfig.ENABLE_DAMAGE_CORRUPTION) {
			return;
		}
		if (!OutofboundMod.CORRUPTION.enabled()) {
			return;
		}
		CorruptionEngine.tick(server);
	}

	public static void clientTick() {
		if (!DarknessConfig.ENABLE_DAMAGE_CORRUPTION) {
			return;
		}
		if (!OutofboundMod.CORRUPTION.enabled()) {
			return;
		}
		CorruptionClientEngine.tick();
	}
}
