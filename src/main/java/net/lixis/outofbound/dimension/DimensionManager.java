package net.lixis.outofbound.dimension;

import com.mojang.serialization.Lifecycle;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.gen.MazeChunkGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;

import net.lixis.outofbound.mixin.MinecraftServerAccessor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public final class DimensionManager {

	private DimensionManager() {
	}

	public static ServerLevel getOrCreateMazeLevel(MinecraftServer server, long index) {
		ResourceKey<Level> key = MazeDimensions.levelKey(index);

		ServerLevel existing = server.getLevel(key);
		if (existing != null) {
			return existing;
		}

		if (!MazeConfig.enableDynamicDimensions) {
			return null;
		}

		try {
			MinecraftServerAccessor internals = (MinecraftServerAccessor) server;
			Executor executor = internals.outofbound$getExecutor();
			LevelStorageSource.LevelStorageAccess storage = internals.outofbound$getStorageSource();
			ChunkProgressListenerFactory progressFactory = internals.outofbound$getProgressListenerFactory();
			WorldData worldData = internals.outofbound$getWorldData();

			if (executor == null || storage == null || progressFactory == null || worldData == null) {
				OutofboundMod.LOGGER.error("[outofbound] Could not access server internals to create dimension {}", index);
				return null;
			}

			Holder<DimensionType> typeHolder = server.registryAccess()
					.registryOrThrow(Registries.DIMENSION_TYPE)
					.getHolderOrThrow(MazeDimensions.DIMENSION_TYPE);

			Holder<Biome> voidBiome = server.registryAccess()
					.registryOrThrow(Registries.BIOME)
					.getHolderOrThrow(Biomes.THE_VOID);

			long seed = MazeDimensions.seedForIndex(index);
			MazeChunkGenerator generator = new MazeChunkGenerator(new FixedBiomeSource(voidBiome), seed, index);
			LevelStem stem = new LevelStem(typeHolder, generator);
			ResourceKey<LevelStem> stemKey = MazeDimensions.stemKey(index);

			registerLevelStem(server, stemKey, stem);

			ServerLevel overworld = server.overworld();
			ServerLevelData overworldData = (ServerLevelData) overworld.getLevelData();
			DerivedLevelData derived = new DerivedLevelData(worldData, overworldData);

			ChunkProgressListener progressListener = progressFactory.create(11);

			ServerLevel newLevel = new ServerLevel(
					server,
					executor,
					storage,
					derived,
					key,
					stem,
					progressListener,
					false,
					BiomeManager.obfuscateSeed(seed),
					List.of(),
					false,
					null
			);

			overworld.getWorldBorder().addListener(
					new BorderChangeListener.DelegateBorderChangeListener(newLevel.getWorldBorder()));

			Map<ResourceKey<Level>, ServerLevel> levels = server.forgeGetWorldMap();
			levels.put(key, newLevel);
			server.markWorldsDirty();
			MinecraftForge.EVENT_BUS.post(new LevelEvent.Load(newLevel));

			OutofboundMod.LOGGER.info("[outofbound] Created maze dimension {} ({} worlds ticking)",
					key.location(), levels.size());
			return newLevel;
		} catch (Throwable t) {
			OutofboundMod.LOGGER.error("[outofbound] Failed to create maze dimension {}", index, t);
			return null;
		}
	}

	private static void registerLevelStem(MinecraftServer server, ResourceKey<LevelStem> stemKey, LevelStem stem) {
		Registry<LevelStem> registry = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
		if (registry.containsKey(stemKey)) {
			return;
		}

		try {
			if (registry instanceof MappedRegistry<LevelStem> mapped) {
				mapped.unfreeze();
				Registry.register(registry, stemKey, stem);
				mapped.freeze();
			} else if (registry instanceof WritableRegistry<LevelStem> writable) {
				writable.register(stemKey, stem, Lifecycle.stable());
			} else {
				OutofboundMod.LOGGER.warn("[outofbound] Could not register LevelStem {} (registry not writable)",
						stemKey.location());
			}
		} catch (Throwable t) {
			OutofboundMod.LOGGER.warn("[outofbound] Failed to register LevelStem {}: {}",
					stemKey.location(), t.getMessage());
		}
	}
}
