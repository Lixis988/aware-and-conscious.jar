package net.lixis.outofbound.feature.corruption;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.lixis.outofbound.feature.corruption.corruptors.MobFlashCorruptor;
import net.lixis.outofbound.feature.corruption.corruptors.MobSwapCorruptor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class CorruptionSnapshots {

	private CorruptionSnapshots() {
	}

	private static final List<EntityVelocitySnapshot> VELOCITY_RESTORE = new ArrayList<>();
	private static final List<EntityPosSnapshot> POS_RESTORE = new ArrayList<>();
	private static final List<BlockSnapshot> BLOCK_RESTORE = new ArrayList<>();
	private static final List<BlockEntitySnapshot> BLOCK_ENTITY_RESTORE = new ArrayList<>();
	private static final List<InventorySnapshot> INVENTORY_RESTORE = new ArrayList<>();
	private static final List<EntityNameSnapshot> ENTITY_NAME_RESTORE = new ArrayList<>();
	private static final List<MobSwapSnapshot> MOB_SWAP_RESTORE = new ArrayList<>();
	private static final List<EntityDiscardSnapshot> ENTITY_DISCARD = new ArrayList<>();
	private static final List<BossBarSnapshot> BOSS_BAR_REMOVE = new ArrayList<>();

	public static void scheduleVelocityRestore(int entityId, Vec3 velocity, int ticks) {
		VELOCITY_RESTORE.add(new EntityVelocitySnapshot(entityId, velocity, ticks));
	}

	public static void schedulePosRestore(int entityId, Vec3 position, float yRot, int ticks) {
		POS_RESTORE.add(new EntityPosSnapshot(entityId, position, yRot, ticks));
	}

	public static void scheduleBlockRestore(ResourceKey<Level> dimension, BlockPos pos, BlockState originalState, int ticks) {
		scheduleBlockRestore(dimension, pos, originalState, null, ticks);
	}

	public static void scheduleBlockRestore(ResourceKey<Level> dimension, BlockPos pos, BlockState originalState,
			@Nullable BlockState expectedCorrupted, int ticks) {
		BLOCK_RESTORE.add(new BlockSnapshot(dimension, pos, originalState, expectedCorrupted, ticks));
	}

	public static void scheduleBlockEntityRestore(ResourceKey<Level> dimension, BlockPos pos, CompoundTag tag, int ticks) {
		BLOCK_ENTITY_RESTORE.add(new BlockEntitySnapshot(dimension, pos, tag.copy(), ticks));
	}

	public static void scheduleInventoryRestore(UUID playerId, int slot, ItemStack stack, int ticks) {
		INVENTORY_RESTORE.add(new InventorySnapshot(playerId, slot, stack, ticks));
	}

	public static void scheduleEntityNameRestore(int entityId, @Nullable Component name, boolean visible, int ticks) {
		ENTITY_NAME_RESTORE.add(new EntityNameSnapshot(entityId, name, visible, ticks));
	}

	public static void scheduleMobSwapRestore(ResourceKey<Level> dimension, EntityType<?> originalType,
			CompoundTag originalNbt, int replacementId, int ticks) {
		MOB_SWAP_RESTORE.add(new MobSwapSnapshot(dimension, originalType, originalNbt.copy(), replacementId, ticks));
	}

	public static void scheduleEntityDiscard(int entityId, int ticks) {
		ENTITY_DISCARD.add(new EntityDiscardSnapshot(entityId, ticks));
	}

	public static void scheduleBossBarRemove(UUID playerId, ServerBossEvent bossEvent, int ticks) {
		BOSS_BAR_REMOVE.add(new BossBarSnapshot(playerId, bossEvent, ticks));
	}

	public static void tick(MinecraftServer server) {
		tickEntityVelocity(server);
		tickEntityPos(server);
		tickBlocks(server);
		tickBlockEntities(server);
		tickInventory(server);
		tickEntityNames(server);
		tickMobSwaps(server);
		tickEntityDiscards(server);
		tickBossBars(server);
		if (server.getTickCount() % 100 == 0) {
			purgeOrphanCorruptionSpawns(server);
		}
	}

	public static void restoreAll(MinecraftServer server) {
		for (EntityVelocitySnapshot snapshot : VELOCITY_RESTORE) {
			restoreVelocity(server, snapshot);
		}
		VELOCITY_RESTORE.clear();

		for (EntityPosSnapshot snapshot : POS_RESTORE) {
			restorePos(server, snapshot);
		}
		POS_RESTORE.clear();

		for (BlockSnapshot snapshot : BLOCK_RESTORE) {
			restoreBlock(server, snapshot);
		}
		BLOCK_RESTORE.clear();

		for (BlockEntitySnapshot snapshot : BLOCK_ENTITY_RESTORE) {
			restoreBlockEntity(server, snapshot);
		}
		BLOCK_ENTITY_RESTORE.clear();

		for (InventorySnapshot snapshot : INVENTORY_RESTORE) {
			restoreInventory(server, snapshot);
		}
		INVENTORY_RESTORE.clear();

		for (EntityNameSnapshot snapshot : ENTITY_NAME_RESTORE) {
			restoreEntityName(server, snapshot);
		}
		ENTITY_NAME_RESTORE.clear();

		for (MobSwapSnapshot snapshot : MOB_SWAP_RESTORE) {
			restoreMobSwap(server, snapshot);
		}
		MOB_SWAP_RESTORE.clear();

		for (EntityDiscardSnapshot snapshot : ENTITY_DISCARD) {
			discardEntity(server, snapshot);
		}
		ENTITY_DISCARD.clear();

		for (BossBarSnapshot snapshot : BOSS_BAR_REMOVE) {
			removeBossBar(server, snapshot);
		}
		BOSS_BAR_REMOVE.clear();
	}

	private static void tickEntityVelocity(MinecraftServer server) {
		Iterator<EntityVelocitySnapshot> iterator = VELOCITY_RESTORE.iterator();
		while (iterator.hasNext()) {
			EntityVelocitySnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				restoreVelocity(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void tickEntityPos(MinecraftServer server) {
		Iterator<EntityPosSnapshot> iterator = POS_RESTORE.iterator();
		while (iterator.hasNext()) {
			EntityPosSnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				restorePos(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void tickBlocks(MinecraftServer server) {
		Iterator<BlockSnapshot> iterator = BLOCK_RESTORE.iterator();
		while (iterator.hasNext()) {
			BlockSnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				restoreBlock(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void tickInventory(MinecraftServer server) {
		Iterator<InventorySnapshot> iterator = INVENTORY_RESTORE.iterator();
		while (iterator.hasNext()) {
			InventorySnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				restoreInventory(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void tickEntityNames(MinecraftServer server) {
		Iterator<EntityNameSnapshot> iterator = ENTITY_NAME_RESTORE.iterator();
		while (iterator.hasNext()) {
			EntityNameSnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				restoreEntityName(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void tickMobSwaps(MinecraftServer server) {
		Iterator<MobSwapSnapshot> iterator = MOB_SWAP_RESTORE.iterator();
		while (iterator.hasNext()) {
			MobSwapSnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				restoreMobSwap(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void tickEntityDiscards(MinecraftServer server) {
		Iterator<EntityDiscardSnapshot> iterator = ENTITY_DISCARD.iterator();
		while (iterator.hasNext()) {
			EntityDiscardSnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				discardEntity(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void tickBossBars(MinecraftServer server) {
		Iterator<BossBarSnapshot> iterator = BOSS_BAR_REMOVE.iterator();
		while (iterator.hasNext()) {
			BossBarSnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				removeBossBar(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void restoreVelocity(MinecraftServer server, EntityVelocitySnapshot snapshot) {
		for (ServerLevel level : server.getAllLevels()) {
			Entity entity = level.getEntity(snapshot.entityId);
			if (entity != null) {
				entity.setDeltaMovement(snapshot.velocity);
				return;
			}
		}
	}

	private static void restorePos(MinecraftServer server, EntityPosSnapshot snapshot) {
		for (ServerLevel level : server.getAllLevels()) {
			Entity entity = level.getEntity(snapshot.entityId);
			if (entity != null) {
				entity.setPos(snapshot.position.x, snapshot.position.y, snapshot.position.z);
				entity.setYRot(snapshot.yRot);
				entity.yRotO = snapshot.yRot;
				return;
			}
		}
	}

	private static void tickBlockEntities(MinecraftServer server) {
		Iterator<BlockEntitySnapshot> iterator = BLOCK_ENTITY_RESTORE.iterator();
		while (iterator.hasNext()) {
			BlockEntitySnapshot snapshot = iterator.next();
			snapshot.ticksRemaining--;
			if (snapshot.ticksRemaining <= 0) {
				restoreBlockEntity(server, snapshot);
				iterator.remove();
			}
		}
	}

	private static void restoreBlockEntity(MinecraftServer server, BlockEntitySnapshot snapshot) {
		ServerLevel level = server.getLevel(snapshot.dimension);
		if (level == null || !level.isLoaded(snapshot.pos)) {
			return;
		}
		BlockEntity blockEntity = level.getBlockEntity(snapshot.pos);
		if (blockEntity == null) {
			return;
		}
		blockEntity.load(snapshot.tag);
		blockEntity.setChanged();
		level.sendBlockUpdated(snapshot.pos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
	}

	private static void restoreBlock(MinecraftServer server, BlockSnapshot snapshot) {
		ServerLevel level = server.getLevel(snapshot.dimension);
		if (level == null || !level.isLoaded(snapshot.pos)) {
			return;
		}

		if (snapshot.expectedCorrupted != null) {
			BlockState current = level.getBlockState(snapshot.pos);
			if (!current.equals(snapshot.expectedCorrupted)) {
				return;
			}
		}
		level.setBlockAndUpdate(snapshot.pos, snapshot.state);
	}

	private static void restoreInventory(MinecraftServer server, InventorySnapshot snapshot) {
		ServerPlayer player = server.getPlayerList().getPlayer(snapshot.playerId);
		if (player == null) {
			return;
		}
		player.getInventory().setItem(snapshot.slot, snapshot.stack.copy());
		player.inventoryMenu.broadcastChanges();
	}

	private static void restoreEntityName(MinecraftServer server, EntityNameSnapshot snapshot) {
		for (ServerLevel level : server.getAllLevels()) {
			Entity entity = level.getEntity(snapshot.entityId);
			if (entity instanceof LivingEntity living) {
				living.setCustomName(snapshot.customName);
				living.setCustomNameVisible(snapshot.customNameVisible);
				return;
			}
		}
	}

	private static void restoreMobSwap(MinecraftServer server, MobSwapSnapshot snapshot) {
		ServerLevel level = server.getLevel(snapshot.dimension);
		if (level == null) {
			return;
		}

		Entity replacement = level.getEntity(snapshot.replacementId);
		if (replacement != null) {
			replacement.discard();
		}

		if (!canRestoreOriginal(level, snapshot.originalNbt)) {
			return;
		}

		Entity restored = snapshot.originalType.create(level);
		if (restored == null) {
			return;
		}
		restored.load(snapshot.originalNbt);
		level.addFreshEntity(restored);
	}

	private static void discardEntity(MinecraftServer server, EntityDiscardSnapshot snapshot) {
		for (ServerLevel level : server.getAllLevels()) {
			Entity entity = level.getEntity(snapshot.entityId);
			if (entity != null) {
				entity.discard();
				return;
			}
		}
	}

	private static void removeBossBar(MinecraftServer server, BossBarSnapshot snapshot) {
		ServerPlayer player = server.getPlayerList().getPlayer(snapshot.playerId);
		if (player != null) {
			snapshot.bossEvent.removePlayer(player);
		}
		snapshot.bossEvent.removeAllPlayers();
	}

	private static boolean canRestoreOriginal(ServerLevel level, CompoundTag nbt) {
		if (!nbt.contains("Pos", Tag.TAG_LIST)) {
			return true;
		}
		ListTag pos = nbt.getList("Pos", Tag.TAG_DOUBLE);
		if (pos.size() < 3) {
			return true;
		}
		BlockPos blockPos = BlockPos.containing(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
		return level.hasChunkAt(blockPos);
	}

	private static void purgeOrphanCorruptionSpawns(MinecraftServer server) {
		java.util.Set<Integer> tracked = new java.util.HashSet<>();
		for (MobSwapSnapshot snapshot : MOB_SWAP_RESTORE) {
			tracked.add(snapshot.replacementId);
		}
		for (EntityDiscardSnapshot snapshot : ENTITY_DISCARD) {
			tracked.add(snapshot.entityId);
		}

		for (ServerLevel level : server.getAllLevels()) {
			for (Entity entity : level.getAllEntities()) {
				if (!entity.getPersistentData().getBoolean(MobFlashCorruptor.FLASH_TAG)) {
					continue;
				}
				if (tracked.contains(entity.getId())) {
					continue;
				}
				entity.discard();
			}
		}
	}

	private static final class EntityVelocitySnapshot {
		private final int entityId;
		private final Vec3 velocity;
		private int ticksRemaining;

		private EntityVelocitySnapshot(int entityId, Vec3 velocity, int ticksRemaining) {
			this.entityId = entityId;
			this.velocity = velocity;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class EntityPosSnapshot {
		private final int entityId;
		private final Vec3 position;
		private final float yRot;
		private int ticksRemaining;

		private EntityPosSnapshot(int entityId, Vec3 position, float yRot, int ticksRemaining) {
			this.entityId = entityId;
			this.position = position;
			this.yRot = yRot;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class BlockEntitySnapshot {
		private final ResourceKey<Level> dimension;
		private final BlockPos pos;
		private final CompoundTag tag;
		private int ticksRemaining;

		private BlockEntitySnapshot(ResourceKey<Level> dimension, BlockPos pos, CompoundTag tag, int ticksRemaining) {
			this.dimension = dimension;
			this.pos = pos;
			this.tag = tag;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class BlockSnapshot {
		private final ResourceKey<Level> dimension;
		private final BlockPos pos;

		private final BlockState state;

		@Nullable
		private final BlockState expectedCorrupted;
		private int ticksRemaining;

		private BlockSnapshot(ResourceKey<Level> dimension, BlockPos pos, BlockState state,
				@Nullable BlockState expectedCorrupted, int ticksRemaining) {
			this.dimension = dimension;
			this.pos = pos;
			this.state = state;
			this.expectedCorrupted = expectedCorrupted;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class InventorySnapshot {
		private final UUID playerId;
		private final int slot;
		private final ItemStack stack;
		private int ticksRemaining;

		private InventorySnapshot(UUID playerId, int slot, ItemStack stack, int ticksRemaining) {
			this.playerId = playerId;
			this.slot = slot;
			this.stack = stack;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class EntityNameSnapshot {
		private final int entityId;
		@Nullable
		private final Component customName;
		private final boolean customNameVisible;
		private int ticksRemaining;

		private EntityNameSnapshot(int entityId, @Nullable Component customName, boolean customNameVisible, int ticksRemaining) {
			this.entityId = entityId;
			this.customName = customName;
			this.customNameVisible = customNameVisible;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class MobSwapSnapshot {
		private final ResourceKey<Level> dimension;
		private final EntityType<?> originalType;
		private final CompoundTag originalNbt;
		private final int replacementId;
		private int ticksRemaining;

		private MobSwapSnapshot(ResourceKey<Level> dimension, EntityType<?> originalType, CompoundTag originalNbt,
				int replacementId, int ticksRemaining) {
			this.dimension = dimension;
			this.originalType = originalType;
			this.originalNbt = originalNbt;
			this.replacementId = replacementId;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class EntityDiscardSnapshot {
		private final int entityId;
		private int ticksRemaining;

		private EntityDiscardSnapshot(int entityId, int ticksRemaining) {
			this.entityId = entityId;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private static final class BossBarSnapshot {
		private final UUID playerId;
		private final ServerBossEvent bossEvent;
		private int ticksRemaining;

		private BossBarSnapshot(UUID playerId, ServerBossEvent bossEvent, int ticksRemaining) {
			this.playerId = playerId;
			this.bossEvent = bossEvent;
			this.ticksRemaining = ticksRemaining;
		}
	}
}
