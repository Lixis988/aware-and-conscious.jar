package net.lixis.outofbound.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class DeferredChunkTasks {

	private DeferredChunkTasks() {
	}

	public static void runWhenChunkReady(ServerLevel level, ChunkPos chunkPos, Runnable task) {
		MinecraftServer server = level.getServer();
		if (server == null) {
			return;
		}
		server.execute(() -> {
			if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
				return;
			}
			DeferredChunkWorkQueue.enqueue(task);
		});
	}
}
