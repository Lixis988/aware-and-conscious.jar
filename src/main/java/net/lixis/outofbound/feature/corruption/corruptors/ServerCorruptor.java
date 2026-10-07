package net.lixis.outofbound.feature.corruption.corruptors;

import net.lixis.outofbound.feature.corruption.CorruptionRunContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public abstract class ServerCorruptor implements ICorruptor {

	@Override
	public void run(CorruptionRunContext ctx) {
		ServerPlayer player = ctx.serverPlayer();
		ServerLevel level = ctx.serverLevel();
		if (player != null && level != null) {
			runServer(level, player, ctx);
		}
	}

	protected abstract void runServer(ServerLevel level, ServerPlayer player, CorruptionRunContext ctx);
}
