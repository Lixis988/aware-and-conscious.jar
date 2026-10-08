package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.WeakHashMap;

public final class WorldInternalConfig {

	public static final int SINK_DURATION_TICKS = 10 * 60 * 20;
	public static final long TICKS_PER_DAY = 24_000L;

	private static final String FILE_NAME = "internal_config.txt";
	private static final Map<MinecraftServer, Properties> CACHE = new WeakHashMap<>();

	private static final String KEY_GAME_STAGE = "game_stage";
	private static final String KEY_BOUNDEDCOW_COLLISION = "boundedcow_collision";
	private static final String KEY_BOUNDEDCOW_COLLISION_TICK = "boundedcow_collision_tick";
	private static final String KEY_BOUNDEDCOW_COLLISION_DAY_TIME = "boundedcow_collision_day_time";
	private static final String KEY_BOUNDEDCOW_COLLISION_PLAYER = "boundedcow_collision_player";
	private static final String KEY_BOUNDEDCOW_COLLISION_TIME = "boundedcow_collision_time";
	private static final String KEY_SINK_UNTIL_TICK = "sink_until_tick";
	private static final String KEY_MAZE_INDEX = "maze_index";
	private static final String KEY_NATURAL_MAZE_ENTRIES = "natural_maze_entries";
	private static final String KEY_FINALE_STATE = "finale_state";
	private static final String KEY_FINALE_PLAYER = "finale_player";
	private static final String KEY_POST_EYE_WEIRDNESS = "post_eye_weirdness";
	private static final String KEY_MEAT_GAME_MODE = "meat_game_mode";

	private WorldInternalConfig() {
	}

	public static Path getConfigPath(MinecraftServer server) {
		return server.getWorldPath(LevelResource.ROOT).resolve(FILE_NAME);
	}

	public static Properties load(MinecraftServer server) {
		return CACHE.computeIfAbsent(server, WorldInternalConfig::readFromDisk);
	}

	private static Properties readFromDisk(MinecraftServer server) {
		Properties properties = new Properties();
		Path path = getConfigPath(server);
		if (!Files.exists(path)) {
			properties.setProperty(KEY_GAME_STAGE, WorldGameStage.START.configValue());

			properties.setProperty(KEY_BOUNDEDCOW_COLLISION, "true");
			properties.setProperty(KEY_SINK_UNTIL_TICK, "0");
			properties.setProperty(KEY_MAZE_INDEX, "0");
			properties.setProperty(KEY_NATURAL_MAZE_ENTRIES, "0");
			properties.setProperty(KEY_FINALE_STATE, FinaleState.NONE.configValue());
			properties.setProperty(KEY_POST_EYE_WEIRDNESS, "false");
			return properties;
		}

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			properties.load(reader);
		} catch (IOException exception) {
			OutofboundMod.LOGGER.warn("Failed to read {}", path, exception);
		}
		return properties;
	}

	public static void save(MinecraftServer server, Properties properties) {
		Path path = getConfigPath(server);
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				writer.write("# Outofbound world state (per-save, not global launcher config)\n");
				properties.store(writer, "outofbound internal config");
			}
			CACHE.put(server, properties);
		} catch (IOException exception) {
			OutofboundMod.LOGGER.warn("Failed to write {}", path, exception);
		}
	}

	public static WorldGameStage getGameStage(MinecraftServer server) {
		return WorldGameStage.fromString(load(server).getProperty(KEY_GAME_STAGE, WorldGameStage.START.configValue()));
	}

	public static boolean hasBoundedcowCollision(MinecraftServer server) {
		return Boolean.parseBoolean(load(server).getProperty(KEY_BOUNDEDCOW_COLLISION, "false"));
	}

	public static long getSinkUntilTick(MinecraftServer server) {
		try {
			return Long.parseLong(load(server).getProperty(KEY_SINK_UNTIL_TICK, "0"));
		} catch (NumberFormatException exception) {
			return 0L;
		}
	}

	public static long getMazeIndex(MinecraftServer server) {
		try {
			return Long.parseLong(load(server).getProperty(KEY_MAZE_INDEX, "0"));
		} catch (NumberFormatException exception) {
			return 0L;
		}
	}

	public static void recordBoundedcowCollision(MinecraftServer server, ServerPlayer player) {
		Properties properties = load(server);
		properties.setProperty(KEY_BOUNDEDCOW_COLLISION, "true");
		properties.setProperty(KEY_BOUNDEDCOW_COLLISION_TICK, String.valueOf(server.overworld().getGameTime()));
		properties.setProperty(KEY_BOUNDEDCOW_COLLISION_DAY_TIME, String.valueOf(server.overworld().getDayTime()));
		properties.setProperty(KEY_BOUNDEDCOW_COLLISION_PLAYER, player.getUUID().toString());
		properties.setProperty(KEY_BOUNDEDCOW_COLLISION_TIME, Instant.now().toString());
		save(server, properties);
	}

	public static void ensureBoundedcowCollisionOnJoin(MinecraftServer server, ServerPlayer player) {
		if (!hasBoundedcowCollision(server)) {
			return;
		}
		if (getBoundedcowCollisionPlayerUuid(server) == null) {
			recordBoundedcowCollision(server, player);
		}
	}

	public static void beginSinkingPhase(MinecraftServer server) {
		Properties properties = load(server);
		long sinkUntil = server.overworld().getGameTime() + SINK_DURATION_TICKS;
		properties.setProperty(KEY_GAME_STAGE, WorldGameStage.SINKING.configValue());
		properties.setProperty(KEY_SINK_UNTIL_TICK, String.valueOf(sinkUntil));
		save(server, properties);
	}

	public static void markTeleportedToMaze(MinecraftServer server, long mazeIndex) {
		Properties properties = load(server);
		properties.setProperty(KEY_GAME_STAGE, WorldGameStage.IN_MAZE.configValue());
		properties.setProperty(KEY_MAZE_INDEX, String.valueOf(mazeIndex));
		save(server, properties);
	}

	public static void migrateIfNeeded(MinecraftServer server) {
		if (!hasBoundedcowCollision(server)) {
			return;
		}
		migrateCollisionAnchors(server);
		WorldGameStage stage = getGameStage(server);
		if (stage == WorldGameStage.START) {
			beginSinkingPhase(server);
			return;
		}
		if (stage == WorldGameStage.SINKING && getSinkUntilTick(server) <= 0L) {
			beginSinkingPhase(server);
		}
	}

	public static boolean isUndefiendMaze(MinecraftServer server) {
		return MazeDimensions.isUndefiendIndex(getMazeIndex(server));
	}

	public static UUID getBoundedcowCollisionPlayerUuid(MinecraftServer server) {
		String value = load(server).getProperty(KEY_BOUNDEDCOW_COLLISION_PLAYER, "");
		if (value.isEmpty()) {
			return null;
		}
		try {
			return UUID.fromString(value);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	public static long getBoundedcowCollisionTick(MinecraftServer server) {
		try {
			return Long.parseLong(load(server).getProperty(KEY_BOUNDEDCOW_COLLISION_TICK, "0"));
		} catch (NumberFormatException exception) {
			return 0L;
		}
	}

	public static long getBoundedcowCollisionDayTime(MinecraftServer server) {
		try {
			return Long.parseLong(load(server).getProperty(KEY_BOUNDEDCOW_COLLISION_DAY_TIME, "0"));
		} catch (NumberFormatException exception) {
			return 0L;
		}
	}

	public static long getDaysSinceBoundedcowCollision(MinecraftServer server) {
		if (!hasBoundedcowCollision(server)) {
			return 0L;
		}
		long gameTime = server.overworld().getGameTime();
		long dayTime = server.overworld().getDayTime();
		long collisionGameTick = getBoundedcowCollisionTick(server);
		long collisionDayTime = getBoundedcowCollisionDayTime(server);

		long daysFromGame = 0L;
		if (collisionGameTick > 0L && gameTime >= collisionGameTick) {
			daysFromGame = (gameTime - collisionGameTick) / TICKS_PER_DAY;
		}

		long daysFromDay = 0L;
		if (collisionDayTime > 0L && dayTime >= collisionDayTime) {
			daysFromDay = (dayTime - collisionDayTime) / TICKS_PER_DAY;
		}

		return Math.max(daysFromGame, daysFromDay);
	}

	private static void migrateCollisionAnchors(MinecraftServer server) {
		Properties properties = load(server);
		boolean changed = false;
		long gameTime = server.overworld().getGameTime();
		long dayTime = server.overworld().getDayTime();

		long collisionGameTick = getBoundedcowCollisionTick(server);
		if (collisionGameTick <= 0L) {
			properties.setProperty(KEY_BOUNDEDCOW_COLLISION_TICK, String.valueOf(gameTime));
			collisionGameTick = gameTime;
			changed = true;
		}

		if (!properties.containsKey(KEY_BOUNDEDCOW_COLLISION_DAY_TIME)) {
			long estimatedCollisionDayTime = dayTime - (gameTime - collisionGameTick);
			properties.setProperty(KEY_BOUNDEDCOW_COLLISION_DAY_TIME, String.valueOf(estimatedCollisionDayTime));
			changed = true;
		}

		if (changed) {
			save(server, properties);
		}
	}

	public static int getNaturalMazeEntries(MinecraftServer server) {
		try {
			return Integer.parseInt(load(server).getProperty(KEY_NATURAL_MAZE_ENTRIES, "0"));
		} catch (NumberFormatException exception) {
			return 0;
		}
	}

	public static void incrementNaturalMazeEntries(MinecraftServer server) {
		Properties properties = load(server);
		int current = getNaturalMazeEntries(server);
		properties.setProperty(KEY_NATURAL_MAZE_ENTRIES, String.valueOf(current + 1));
		save(server, properties);
	}

	public static FinaleState getFinaleState(MinecraftServer server) {
		return FinaleState.fromString(load(server).getProperty(KEY_FINALE_STATE, FinaleState.NONE.configValue()));
	}

	public static void setFinaleState(MinecraftServer server, FinaleState state) {
		Properties properties = load(server);
		properties.setProperty(KEY_FINALE_STATE, state.configValue());
		save(server, properties);
	}

	public static void setFinalePlayer(MinecraftServer server, ServerPlayer player) {
		Properties properties = load(server);
		properties.setProperty(KEY_FINALE_PLAYER, player.getUUID().toString());
		save(server, properties);
	}

	public static UUID getFinalePlayerUuid(MinecraftServer server) {
		String value = load(server).getProperty(KEY_FINALE_PLAYER, "");
		if (value.isEmpty()) {
			return null;
		}
		try {
			return UUID.fromString(value);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	public static boolean isPostEyeWeirdness(MinecraftServer server) {
		return Boolean.parseBoolean(load(server).getProperty(KEY_POST_EYE_WEIRDNESS, "false"));
	}

	public static void enablePostEyeWeirdness(MinecraftServer server) {
		Properties properties = load(server);
		if (Boolean.parseBoolean(properties.getProperty(KEY_POST_EYE_WEIRDNESS, "false"))) {
			return;
		}
		properties.setProperty(KEY_POST_EYE_WEIRDNESS, "true");
		save(server, properties);
	}

	public static boolean isMeatGameMode(MinecraftServer server) {
		return Boolean.parseBoolean(load(server).getProperty(KEY_MEAT_GAME_MODE, "false"));
	}

	public static void setMeatGameMode(MinecraftServer server, boolean enabled) {
		Properties properties = load(server);
		properties.setProperty(KEY_MEAT_GAME_MODE, Boolean.toString(enabled));
		save(server, properties);
	}
}
