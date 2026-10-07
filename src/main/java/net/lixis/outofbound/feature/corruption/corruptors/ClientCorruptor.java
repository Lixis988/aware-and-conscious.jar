package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.minecraft.client.Minecraft;

public abstract class ClientCorruptor implements ICorruptor {

	@Override
	public void run(CorruptionRunContext ctx) {
		Minecraft minecraft = ctx.minecraft();
		if (minecraft != null) {
			runClient(minecraft, ctx);
		}
	}

	protected abstract void runClient(Minecraft minecraft, CorruptionRunContext ctx);
}
