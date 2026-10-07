package net.lixis.outofbound.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {

	@Accessor("executor")
	Executor outofbound$getExecutor();

	@Accessor("storageSource")
	LevelStorageSource.LevelStorageAccess outofbound$getStorageSource();

	@Accessor("progressListenerFactory")
	ChunkProgressListenerFactory outofbound$getProgressListenerFactory();

	@Accessor("worldData")
	WorldData outofbound$getWorldData();
}
