package net.lixis.outofbound.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public final class OverworldBleedData extends SavedData {

	private static final String DATA_NAME = "outofbound_overworld_bleed";

	private long mazeDwellTicks;
	private int corruptedChunkCount;
	private long deepestMazeIndex;
	private final Set<Long> loadedChunkKeys = new HashSet<>();
	private final Set<Long> corruptedChunkKeys = new HashSet<>();

	public static OverworldBleedData get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(OverworldBleedData::load, OverworldBleedData::new, DATA_NAME);
	}

	private OverworldBleedData() {
	}

	private static OverworldBleedData load(CompoundTag tag) {
		OverworldBleedData data = new OverworldBleedData();
		data.mazeDwellTicks = tag.getLong("mazeDwellTicks");
		data.corruptedChunkCount = tag.getInt("corruptedChunkCount");
		data.deepestMazeIndex = tag.getLong("deepestMazeIndex");
		readLongArray(tag, "loadedChunks", data.loadedChunkKeys);
		readLongArray(tag, "corruptedChunks", data.corruptedChunkKeys);
		if (data.corruptedChunkCount < data.corruptedChunkKeys.size()) {
			data.corruptedChunkCount = data.corruptedChunkKeys.size();
		}
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.putLong("mazeDwellTicks", mazeDwellTicks);
		tag.putInt("corruptedChunkCount", corruptedChunkCount);
		tag.putLong("deepestMazeIndex", deepestMazeIndex);
		tag.put("loadedChunks", new LongArrayTag(loadedChunkKeys.stream().mapToLong(Long::longValue).toArray()));
		tag.put("corruptedChunks", new LongArrayTag(corruptedChunkKeys.stream().mapToLong(Long::longValue).toArray()));
		return tag;
	}

	private static void readLongArray(CompoundTag tag, String key, Set<Long> target) {
		if (!tag.contains(key)) {
			return;
		}
		for (long value : tag.getLongArray(key)) {
			target.add(value);
		}
	}

	public long getMazeDwellTicks() {
		return mazeDwellTicks;
	}

	public int getCorruptedChunkCount() {
		return corruptedChunkCount;
	}

	public long getDeepestMazeIndex() {
		return deepestMazeIndex;
	}

	public Set<Long> loadedChunkKeys() {
		return loadedChunkKeys;
	}

	public Set<Long> corruptedChunkKeys() {
		return corruptedChunkKeys;
	}

	public void recordMazeDepth(long index) {
		if (index > deepestMazeIndex) {
			deepestMazeIndex = index;
			setDirty();
		}
	}

	public void addDwellTicks(int ticks) {
		if (ticks <= 0) {
			return;
		}
		mazeDwellTicks += ticks;
		setDirty();
	}

	public void recordLoadedChunk(int chunkX, int chunkZ) {
		if (loadedChunkKeys.add(OverworldBleedChunkSelector.packChunk(chunkX, chunkZ))) {
			setDirty();
		}
	}

	public void markCorrupted(ChunkPos chunkPos) {
		if (corruptedChunkKeys.add(OverworldBleedChunkSelector.packChunk(chunkPos.x, chunkPos.z))) {
			corruptedChunkCount = corruptedChunkKeys.size();
			setDirty();
		}
	}
}
