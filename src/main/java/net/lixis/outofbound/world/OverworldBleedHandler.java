package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeConfig;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.gen.MazeChunkWriter;
import net.lixis.outofbound.dimension.OverworldPortalPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class OverworldBleedHandler {

	private record PendingNewChunkMaze(ServerLevel level, ChunkPos chunkPos, long dimensionIndex) {
	}

	private static final Queue<PendingNewChunkMaze> NEW_CHUNK_QUEUE = new ConcurrentLinkedQueue<>();

	private OverworldBleedHandler() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player instanceof ServerPlayer player)) {
			return;
		}
		if (!MazeDimensions.isMazeDimension(player.serverLevel().dimension().location())) {
			return;
		}

		MinecraftServer server = player.getServer();
		if (server == null || !WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}
		if (WorldInternalConfig.getGameStage(server) != WorldGameStage.IN_MAZE) {
			return;
		}

		OverworldBleedData data = OverworldBleedData.get(server.overworld());
		long mazeIndex = MazeDimensions.indexFromLocation(player.serverLevel().dimension().location());
		data.recordMazeDepth(mazeIndex);
		data.addDwellTicks(1);
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (!(event.getLevel() instanceof ServerLevel overworld)) {
			return;
		}
		if (overworld.dimension() != Level.OVERWORLD) {
			return;
		}

		MinecraftServer server = overworld.getServer();
		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}

		ChunkPos chunkPos = event.getChunk().getPos();
		OverworldBleedData.get(overworld).recordLoadedChunk(chunkPos.x, chunkPos.z);

		if (!event.isNewChunk()) {
			return;
		}
		if (!MazeConfig.enableOverworldBleed || !MazeConfig.enableBleedNewChunkGen) {
			return;
		}
		if (WorldInternalConfig.getGameStage(server) != WorldGameStage.IN_MAZE) {
			return;
		}

		long worldSeed = overworld.getSeed();
		OverworldBleedData data = OverworldBleedData.get(overworld);
		double chance = OverworldBleedIntensity.newChunkChance(data.getDeepestMazeIndex());
		if (!OverworldBleedChunkSelector.shouldGenerateAsMaze(worldSeed, chunkPos.x, chunkPos.z, chance)) {
			return;
		}

		long dimensionIndex = OverworldBleedChunkSelector.dimensionIndexForChunk(worldSeed, chunkPos.x, chunkPos.z);
		NEW_CHUNK_QUEUE.offer(new PendingNewChunkMaze(overworld, chunkPos, dimensionIndex));
	}

	private static void applyMazeChunk(ServerLevel overworld, ChunkPos chunkPos, long dimensionIndex) {
		ChunkAccess chunk = overworld.getChunk(chunkPos.x, chunkPos.z);
		MazeChunkWriter.fillOverworldChunk(chunk, dimensionIndex);
		chunk.setUnsaved(true);
		OverworldPortalPlacer.tryPlaceInOverworldChunk(overworld, chunk, dimensionIndex);
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (event.getServer().getTickCount() % 20 != 0) {
			return;
		}

		MinecraftServer server = event.getServer();
		if (!MazeConfig.enableOverworldBleed) {
			return;
		}
		if (!WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}
		if (WorldInternalConfig.getGameStage(server) != WorldGameStage.IN_MAZE) {
			return;
		}

		ServerLevel overworld = server.overworld();
		OverworldBleedData data = OverworldBleedData.get(overworld);
		refreshDeepestMazeIndex(server, data);

		long depthIndex = data.getDeepestMazeIndex();
		int ticksPerChunk = OverworldBleedIntensity.ticksPerChunk(depthIndex);
		int targetChunks = (int) (data.getMazeDwellTicks() / Math.max(1, ticksPerChunk));
		int budget = OverworldBleedIntensity.chunksPerTick(depthIndex);

		while (data.getCorruptedChunkCount() < targetChunks && budget > 0) {
			corruptNextChunk(overworld, data);
			budget--;
		}

		processNewChunkQueue(server);
	}

	private static void processNewChunkQueue(MinecraftServer server) {
		int budget = MazeConfig.bleedNewChunksPerSecond;
		for (int processed = 0; processed < budget; processed++) {
			PendingNewChunkMaze pending = NEW_CHUNK_QUEUE.poll();
			if (pending == null) {
				return;
			}
			if (!pending.level.hasChunk(pending.chunkPos.x, pending.chunkPos.z)) {
				continue;
			}
			server.execute(() -> applyMazeChunk(pending.level, pending.chunkPos, pending.dimensionIndex));
		}
	}

	private static void refreshDeepestMazeIndex(MinecraftServer server, OverworldBleedData data) {
		long worldIndex = WorldInternalConfig.getMazeIndex(server);
		if (worldIndex > 0L) {
			data.recordMazeDepth(worldIndex);
		}

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			long dimensionIndex = MazeDimensions.indexFromLocation(player.serverLevel().dimension().location());
			if (dimensionIndex >= 0L) {
				data.recordMazeDepth(dimensionIndex);
			}
			long savedIndex = player.getPersistentData().getLong("outofbound_maze_index");
			if (savedIndex > 0L) {
				data.recordMazeDepth(savedIndex);
			}
		}
	}

	private static void corruptNextChunk(ServerLevel overworld, OverworldBleedData data) {
		BlockPos spawnPos = overworld.getSharedSpawnPos();
		long worldSeed = overworld.getSeed();

		ChunkPos chunkPos = OverworldBleedChunkSelector.selectLoadedChunk(worldSeed, data.getCorruptedChunkCount(),
				spawnPos, MazeConfig.bleedSpawnRingRadius, data.loadedChunkKeys(), data.corruptedChunkKeys());
		if (chunkPos == null) {
			return;
		}

		long dimensionIndex = OverworldBleedChunkSelector.dimensionIndexForChunk(worldSeed, chunkPos.x, chunkPos.z);

		ChunkAccess chunk = overworld.getChunk(chunkPos.x, chunkPos.z);
		MazeChunkWriter.fillOverworldChunk(chunk, dimensionIndex);
		chunk.setUnsaved(true);
		clearNonPlayerEntities(overworld, chunkPos);
		OverworldPortalPlacer.tryPlaceInOverworldChunk(overworld, chunk, dimensionIndex);
		data.markCorrupted(chunkPos);
	}

	private static void clearNonPlayerEntities(ServerLevel level, ChunkPos chunkPos) {
		AABB bounds = new AABB(
				chunkPos.getMinBlockX(), level.getMinBuildHeight(), chunkPos.getMinBlockZ(),
				chunkPos.getMaxBlockX() + 1, level.getMaxBuildHeight(), chunkPos.getMaxBlockZ() + 1);
		for (Entity entity : level.getEntitiesOfClass(Entity.class, bounds, e -> !(e instanceof ServerPlayer))) {
			entity.discard();
		}
	}
}
