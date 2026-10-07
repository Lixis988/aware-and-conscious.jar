package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.feature.corruption.CorruptionClientState;
import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.lixis.outofbound.feature.corruption.util.BitCorruptor;
import net.lixis.outofbound.feature.corruption.util.SafeCorruptor;
import net.minecraft.client.Minecraft;

import java.util.concurrent.ThreadLocalRandom;

public final class RenderCorruptor extends ClientCorruptor {

	@Override
	public float minLevel() {
		return 10.0F;
	}

	@Override
	public int weight() {
		return 2;
	}

	@Override
	protected void runClient(Minecraft minecraft, CorruptionRunContext ctx) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		float scaleX = SafeCorruptor.safeFloat(BitCorruptor.corruptFloat(1.0F));
		float scaleY = SafeCorruptor.safeFloat(BitCorruptor.corruptFloat(1.0F));
		if (Math.abs(scaleX) < 0.05F) {
			scaleX = 0.05F;
		}
		if (Math.abs(scaleY) < 0.05F) {
			scaleY = 0.05F;
		}
		scaleX = Math.max(0.05F, Math.min(3.0F, Math.abs(scaleX)));
		scaleY = Math.max(0.05F, Math.min(3.0F, Math.abs(scaleY)));

		float handScale = SafeCorruptor.safeFloat(BitCorruptor.corruptFloat(1.0F));
		handScale = Math.max(0.0F, Math.min(3.0F, Math.abs(handScale)));

		int ttl = OutofboundMod.CORRUPTION.effectTtl(ctx.level());
		CorruptionClientState.setUiScale(scaleX, scaleY, ttl);
		CorruptionClientState.setHandScale(handScale, ttl);
	}
}
