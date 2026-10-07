package net.lixis.outofbound.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class MazeBlackSquareSpawnData extends SavedData {

	private static final String DATA_NAME = "outofbound_black_square_spawn";

	private boolean spawned;

	public static MazeBlackSquareSpawnData get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(MazeBlackSquareSpawnData::load, MazeBlackSquareSpawnData::new, DATA_NAME);
	}

	private MazeBlackSquareSpawnData() {
	}

	private static MazeBlackSquareSpawnData load(CompoundTag tag) {
		MazeBlackSquareSpawnData data = new MazeBlackSquareSpawnData();
		data.spawned = tag.getBoolean("spawned");
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.putBoolean("spawned", spawned);
		return tag;
	}

	public boolean hasSpawned() {
		return spawned;
	}

	public void markSpawned() {
		spawned = true;
		setDirty();
	}
}
