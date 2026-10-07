package net.lixis.outofbound.entity;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface OverworldStalker {

	boolean isActivated();

	@Nullable
	ServerPlayer getLockedTarget();
}
