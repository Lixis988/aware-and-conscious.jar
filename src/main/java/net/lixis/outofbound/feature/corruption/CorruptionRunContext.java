package net.lixis.outofbound.feature.corruption;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public final class CorruptionRunContext {

	@Nullable
	private final ServerPlayer serverPlayer;
	@Nullable
	private final ServerLevel serverLevel;
	@Nullable
	private final Minecraft minecraft;
	private final float level;

	private CorruptionRunContext(@Nullable ServerPlayer serverPlayer, @Nullable ServerLevel serverLevel,
			@Nullable Minecraft minecraft, float level) {
		this.serverPlayer = serverPlayer;
		this.serverLevel = serverLevel;
		this.minecraft = minecraft;
		this.level = level;
	}

	public static CorruptionRunContext server(ServerPlayer player, float level) {
		return new CorruptionRunContext(player, player.serverLevel(), null, level);
	}

	public static CorruptionRunContext client(Minecraft minecraft, float level) {
		return new CorruptionRunContext(null, null, minecraft, level);
	}

	@Nullable
	public ServerPlayer serverPlayer() {
		return serverPlayer;
	}

	@Nullable
	public ServerLevel serverLevel() {
		return serverLevel;
	}

	@Nullable
	public Minecraft minecraft() {
		return minecraft;
	}

	public float level() {
		return level;
	}
}
