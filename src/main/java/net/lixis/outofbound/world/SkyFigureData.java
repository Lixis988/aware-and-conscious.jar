package net.lixis.outofbound.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SkyFigureData extends SavedData {

	private static final String DATA_NAME = "outofbound_sky_figures";

	private final List<ActiveBlockFigure> activeFigures = new ArrayList<>();

	public static SkyFigureData get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(SkyFigureData::load, SkyFigureData::new, DATA_NAME);
	}

	private SkyFigureData() {
	}

	private static SkyFigureData load(CompoundTag tag) {
		SkyFigureData data = new SkyFigureData();
		ListTag list = tag.getList("figures", CompoundTag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entry = list.getCompound(i);
			long expiresAt = entry.getLong("expiresAt");
			ListTag blocks = entry.getList("blocks", CompoundTag.TAG_COMPOUND);
			List<BlockSnapshot> snapshots = new ArrayList<>();
			for (int j = 0; j < blocks.size(); j++) {
				CompoundTag blockTag = blocks.getCompound(j);
				BlockPos pos = NbtUtils.readBlockPos(blockTag.getCompound("pos"));
				BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), blockTag.getCompound("state"));
				snapshots.add(new BlockSnapshot(pos, state));
			}
			if (!snapshots.isEmpty()) {
				data.activeFigures.add(new ActiveBlockFigure(snapshots, expiresAt));
			}
		}
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag list = new ListTag();
		for (ActiveBlockFigure figure : activeFigures) {
			CompoundTag entry = new CompoundTag();
			entry.putLong("expiresAt", figure.expiresAtGameTime);
			ListTag blocks = new ListTag();
			for (BlockSnapshot snapshot : figure.snapshots) {
				CompoundTag blockTag = new CompoundTag();
				blockTag.put("pos", NbtUtils.writeBlockPos(snapshot.pos));
				blockTag.put("state", NbtUtils.writeBlockState(snapshot.state));
				blocks.add(blockTag);
			}
			entry.put("blocks", blocks);
			list.add(entry);
		}
		tag.put("figures", list);
		return tag;
	}

	public int activeBlockFigureCount() {
		return activeFigures.size();
	}

	public void registerBlockFigure(List<BlockSnapshot> snapshots, long expiresAtGameTime) {
		if (snapshots.isEmpty()) {
			return;
		}
		activeFigures.add(new ActiveBlockFigure(snapshots, expiresAtGameTime));
		setDirty();
	}

	public void tickCleanup(ServerLevel level) {
		if (activeFigures.isEmpty()) {
			return;
		}
		long now = level.getGameTime();
		boolean changed = false;
		Iterator<ActiveBlockFigure> iterator = activeFigures.iterator();
		while (iterator.hasNext()) {
			ActiveBlockFigure figure = iterator.next();
			if (figure.expiresAtGameTime > now) {
				continue;
			}
			restoreFigure(level, figure);
			iterator.remove();
			changed = true;
		}
		if (changed) {
			setDirty();
		}
	}

	private static void restoreFigure(ServerLevel level, ActiveBlockFigure figure) {
		for (BlockSnapshot snapshot : figure.snapshots) {
			if (!level.isLoaded(snapshot.pos)) {
				continue;
			}
			level.setBlock(snapshot.pos, snapshot.state, 3);
		}
	}

	public record BlockSnapshot(BlockPos pos, BlockState state) {
	}

	private record ActiveBlockFigure(List<BlockSnapshot> snapshots, long expiresAtGameTime) {
	}
}
