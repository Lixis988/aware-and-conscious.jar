package net.lixis.outofbound.dimension;

import net.minecraftforge.common.ForgeConfigSpec;

public final class MazeConfig {

	public static final ForgeConfigSpec SPEC;

	private static final ForgeConfigSpec.BooleanValue ENABLE_DYNAMIC_DIMENSIONS;
	private static final ForgeConfigSpec.IntValue MAX_LEVELS_CAP;
	private static final ForgeConfigSpec.DoubleValue ANOMALY_CHANCE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_RANDOM_VOICES;
	private static final ForgeConfigSpec.DoubleValue RANDOM_VOICE_CHANCE;
	private static final ForgeConfigSpec.IntValue RANDOM_VOICE_CHECK_INTERVAL_TICKS;
	private static final ForgeConfigSpec.IntValue RANDOM_VOICE_COOLDOWN_TICKS;
	private static final ForgeConfigSpec.IntValue RANDOM_VOICE_MIN_DISTANCE;
	private static final ForgeConfigSpec.IntValue RANDOM_VOICE_MAX_DISTANCE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_RANDOM_MESSAGES;
	private static final ForgeConfigSpec.DoubleValue RANDOM_MESSAGE_CHANCE;
	private static final ForgeConfigSpec.IntValue RANDOM_MESSAGE_CHECK_INTERVAL_TICKS;
	private static final ForgeConfigSpec.IntValue RANDOM_MESSAGE_COOLDOWN_TICKS;
	private static final ForgeConfigSpec.IntValue RANDOM_MESSAGE_DURATION_TICKS;
	private static final ForgeConfigSpec.BooleanValue ENABLE_LIGHTS;
	private static final ForgeConfigSpec.IntValue PORTAL_CHUNK_SPACING;
	private static final ForgeConfigSpec.IntValue SPAWN_PORTAL_RADIUS;
	private static final ForgeConfigSpec.IntValue SPAWN_PORTAL_COUNT;
	private static final ForgeConfigSpec.IntValue SPAWN_OVERWORLD_PORTAL_COUNT;

	private static final ForgeConfigSpec.BooleanValue ENABLE_OVERWORLD_BLEED;
	private static final ForgeConfigSpec.IntValue BLEED_TICKS_PER_CHUNK;
	private static final ForgeConfigSpec.IntValue BLEED_SPAWN_RING_RADIUS;
	private static final ForgeConfigSpec.IntValue BLEED_CHUNKS_PER_TICK;
	private static final ForgeConfigSpec.IntValue BLEED_RANDOM_PHASE_MIN_DISTANCE;
	private static final ForgeConfigSpec.BooleanValue ENABLE_BLEED_NEW_CHUNK_GEN;
	private static final ForgeConfigSpec.DoubleValue BLEED_NEW_CHUNK_CHANCE;
	private static final ForgeConfigSpec.DoubleValue BLEED_PER_DIMENSION_CHANCE_BONUS;
	private static final ForgeConfigSpec.DoubleValue BLEED_NEW_CHUNK_CHANCE_CAP;
	private static final ForgeConfigSpec.IntValue BLEED_NEW_CHUNK_GRID_SPACING;
	private static final ForgeConfigSpec.IntValue BLEED_NEW_CHUNKS_PER_SECOND;
	private static final ForgeConfigSpec.DoubleValue BLEED_PER_DIMENSION_SPEED_BONUS;
	private static final ForgeConfigSpec.IntValue BLEED_MAX_DEPTH_FOR_SCALING;
	private static final ForgeConfigSpec.IntValue BLEED_MIN_TICKS_PER_CHUNK;
	private static final ForgeConfigSpec.IntValue BLEED_DIMENSIONS_PER_EXTRA_CHUNK;

	private static final ForgeConfigSpec.BooleanValue ENABLE_SKY_FIGURES;
	private static final ForgeConfigSpec.IntValue SKY_FIGURE_INTERVAL_TICKS;
	private static final ForgeConfigSpec.DoubleValue SKY_FIGURE_SPAWN_CHANCE;
	private static final ForgeConfigSpec.IntValue SKY_FIGURE_LIFESPAN_TICKS;
	private static final ForgeConfigSpec.IntValue SKY_FIGURE_MAX_ACTIVE;
	private static final ForgeConfigSpec.IntValue SKY_FIGURE_MIN_HEIGHT_ABOVE_TERRAIN;
	private static final ForgeConfigSpec.IntValue SKY_FIGURE_MAX_HEIGHT_ABOVE_TERRAIN;

	private static final ForgeConfigSpec.BooleanValue ENABLE_OVERWORLD_UNDEFIEND;
	private static final ForgeConfigSpec.IntValue OVERWORLD_UNDEFIEND_INTERVAL_TICKS;
	private static final ForgeConfigSpec.DoubleValue OVERWORLD_UNDEFIEND_SPAWN_CHANCE;
	private static final ForgeConfigSpec.IntValue OVERWORLD_UNDEFIEND_LIFESPAN_TICKS;
	private static final ForgeConfigSpec.IntValue OVERWORLD_UNDEFIEND_SPAWN_CHUNK_DISTANCE;
	private static final ForgeConfigSpec.DoubleValue OVERWORLD_UNDEFIEND_LOOK_DOT_THRESHOLD;

	private static final ForgeConfigSpec.BooleanValue ENABLE_OVERWORLD_TEETHMAN;
	private static final ForgeConfigSpec.IntValue OVERWORLD_TEETHMAN_INTERVAL_TICKS;
	private static final ForgeConfigSpec.DoubleValue OVERWORLD_TEETHMAN_SPAWN_CHANCE;
	private static final ForgeConfigSpec.IntValue OVERWORLD_TEETHMAN_LIFESPAN_TICKS;
	private static final ForgeConfigSpec.IntValue OVERWORLD_TEETHMAN_SPAWN_CHUNK_DISTANCE;
	private static final ForgeConfigSpec.DoubleValue OVERWORLD_TEETHMAN_LOOK_DOT_THRESHOLD;

	private static final ForgeConfigSpec.IntValue OVERWORLD_CHASER_INTERVAL_TICKS;
	private static final ForgeConfigSpec.DoubleValue OVERWORLD_CHASER_DAILY_CHANCE;

	private static final ForgeConfigSpec.BooleanValue ENABLE_MAZE_CHASERS;
	private static final ForgeConfigSpec.IntValue MAZE_CHASER_INTERVAL_TICKS;
	private static final ForgeConfigSpec.DoubleValue MAZE_CHASER_SPAWN_CHANCE;
	private static final ForgeConfigSpec.DoubleValue MAZE_CHASER_MIN_DISTANCE;
	private static final ForgeConfigSpec.DoubleValue MAZE_CHASER_MAX_DISTANCE;

	private static final ForgeConfigSpec.BooleanValue ENABLE_NOTEXTUREMAN;
	private static final ForgeConfigSpec.IntValue NOTEXTUREMAN_INTERVAL_TICKS;
	private static final ForgeConfigSpec.DoubleValue NOTEXTUREMAN_SPAWN_CHANCE;
	private static final ForgeConfigSpec.IntValue NOTEXTUREMAN_MAX_LEVEL;
	private static final ForgeConfigSpec.DoubleValue NOTEXTUREMAN_TRIGGER_DISTANCE;
	private static final ForgeConfigSpec.IntValue NOTEXTUREMAN_FLEE_DURATION_TICKS;

	public static boolean enableDynamicDimensions = true;
	public static int maxLevelsCap = 8;
	public static double anomalyChance = 0.02D;
	public static boolean enableRandomVoices = true;
	public static double randomVoiceChance = 0.012D;
	public static int randomVoiceCheckIntervalTicks = 40;
	public static int randomVoiceCooldownTicks = 4800;
	public static int randomVoiceMinDistance = 4;
	public static int randomVoiceMaxDistance = 16;
	public static boolean enableRandomMessages = true;
	public static double randomMessageChance = 0.01D;
	public static int randomMessageCheckIntervalTicks = 40;
	public static int randomMessageCooldownTicks = 6000;
	public static int randomMessageDurationTicks = 20;
	public static boolean enableLights = true;
	public static int portalChunkSpacing = 6;
	public static int spawnPortalRadius = 14;
	public static int spawnPortalCount = 3;
	public static int spawnOverworldPortalCount = 1;

	public static boolean enableOverworldBleed = true;
	public static int bleedTicksPerChunk = 6000;
	public static int bleedSpawnRingRadius = 24;
	public static int bleedChunksPerTick = 1;
	public static int bleedRandomPhaseMinDistance = 28;
	public static boolean enableBleedNewChunkGen = true;
	public static double bleedNewChunkChance = 0.05D;
	public static double bleedPerDimensionChanceBonus = 0.005D;
	public static double bleedNewChunkChanceCap = 0.12D;
	public static int bleedNewChunkGridSpacing = 4;
	public static int bleedNewChunksPerSecond = 1;
	public static double bleedPerDimensionSpeedBonus = 0.10D;
	public static int bleedMaxDepthForScaling = 50;
	public static int bleedMinTicksPerChunk = 100;
	public static int bleedDimensionsPerExtraChunk = 5;

	public static boolean enableSkyFigures = true;
	public static int skyFigureIntervalTicks = 5000;
	public static double skyFigureSpawnChance = 0.25D;
	public static int skyFigureLifespanTicks = 400;
	public static int skyFigureMaxActive = 4;
	public static int skyFigureMinHeightAboveTerrain = 32;
	public static int skyFigureMaxHeightAboveTerrain = 80;

	public static boolean enableOverworldUndefiend = true;
	public static int overworldUndefiendIntervalTicks = 4000;
	public static double overworldUndefiendSpawnChance = 0.45D;
	public static int overworldUndefiendLifespanTicks = 7200;
	public static int overworldUndefiendSpawnChunkDistance = 4;
	public static double overworldUndefiendLookDotThreshold = 0.96D;

	public static boolean enableOverworldTeethman = true;
	public static int overworldTeethmanIntervalTicks = 5000;
	public static double overworldTeethmanSpawnChance = 0.45D;
	public static int overworldTeethmanLifespanTicks = 7200;
	public static int overworldTeethmanSpawnChunkDistance = 4;
	public static double overworldTeethmanLookDotThreshold = 0.96D;

	public static int overworldChaserIntervalTicks = 24000;
	public static double overworldChaserDailyChance = 0.40D;

	public static boolean enableMazeChasers = true;
	public static int mazeChaserIntervalTicks = 2400;
	public static double mazeChaserSpawnChance = 0.40D;
	public static double mazeChaserMinDistance = 28.0D;
	public static double mazeChaserMaxDistance = 52.0D;

	public static boolean enableNotextureman = true;
	public static int notexturemanIntervalTicks = 3000;
	public static double notexturemanSpawnChance = 0.40D;
	public static int notexturemanMaxLevel = 100;
	public static double notexturemanTriggerDistance = 2.5D;
	public static int notexturemanFleeDurationTicks = 120;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.push("dimensions");
		ENABLE_DYNAMIC_DIMENSIONS = builder
				.comment("Allow creating brand-new maze dimensions at runtime (infinite). If false, only datapack-registered maze dimensions are usable.")
				.define("enableDynamicDimensions", true);
		MAX_LEVELS_CAP = builder
				.comment("Hard cap on vertical maze levels per dimension (protects very small height settings).")
				.defineInRange("maxLevelsCap", 8, 1, 24);
		builder.pop();

		builder.push("atmosphere");
		ANOMALY_CHANCE = builder
				.comment("Per-eligible-tick probability that an ambient anomaly (sound/flicker) fires for a player inside a maze dimension.")
				.defineInRange("anomalyChance", 0.02D, 0.0D, 1.0D);
		ENABLE_RANDOM_VOICES = builder
				.comment("Play one of 13 distant voice clips at random moments (behind the player or through walls).")
				.define("enableRandomVoices", true);
		RANDOM_VOICE_CHANCE = builder
				.comment("Per-check probability that a random voice event plays for a player (any dimension).")
				.defineInRange("randomVoiceChance", 0.012D, 0.0D, 1.0D);
		RANDOM_VOICE_CHECK_INTERVAL_TICKS = builder
				.comment("Server ticks between random voice checks per player.")
				.defineInRange("randomVoiceCheckIntervalTicks", 40, 10, 600);
		RANDOM_VOICE_COOLDOWN_TICKS = builder
				.comment("Minimum ticks between random voice events for the same player.")
				.defineInRange("randomVoiceCooldownTicks", 4800, 200, 72000);
		RANDOM_VOICE_MIN_DISTANCE = builder
				.comment("Minimum block distance from the player for voice playback.")
				.defineInRange("randomVoiceMinDistance", 4, 1, 32);
		RANDOM_VOICE_MAX_DISTANCE = builder
				.comment("Maximum block distance from the player for voice playback.")
				.defineInRange("randomVoiceMaxDistance", 16, 2, 48);
		ENABLE_RANDOM_MESSAGES = builder
				.comment("Flash a random unsettling message on screen for about a second at random moments.")
				.define("enableRandomMessages", true);
		RANDOM_MESSAGE_CHANCE = builder
				.comment("Per-check probability that a random flashing message appears for a player (any dimension).")
				.defineInRange("randomMessageChance", 0.01D, 0.0D, 1.0D);
		RANDOM_MESSAGE_CHECK_INTERVAL_TICKS = builder
				.comment("Server ticks between random message checks per player.")
				.defineInRange("randomMessageCheckIntervalTicks", 40, 10, 600);
		RANDOM_MESSAGE_COOLDOWN_TICKS = builder
				.comment("Minimum ticks between random message events for the same player.")
				.defineInRange("randomMessageCooldownTicks", 6000, 200, 72000);
		RANDOM_MESSAGE_DURATION_TICKS = builder
				.comment("How long (in ticks) a flashing message stays on screen. 20 ticks = 1 second.")
				.defineInRange("randomMessageDurationTicks", 20, 5, 100);
		ENABLE_LIGHTS = builder
				.comment("Whether the generator places emissive light blocks in ceilings.")
				.define("enableLights", true);
		builder.pop();

		builder.push("portals");
		PORTAL_CHUNK_SPACING = builder
				.comment("Place one dimension gate every N chunks. Each gate sits on a pseudo-random vertical maze floor.")
				.defineInRange("portalChunkSpacing", 6, 2, 32);
		SPAWN_PORTAL_RADIUS = builder
				.comment("How far from the maze entry point to search for the small spawn cluster of gates.")
				.defineInRange("spawnPortalRadius", 14, 4, 48);
		SPAWN_PORTAL_COUNT = builder
				.comment("How many gates to place near the entry when a player first enters a maze dimension.")
				.defineInRange("spawnPortalCount", 3, 1, 8);
		SPAWN_OVERWORLD_PORTAL_COUNT = builder
				.comment("How many overworld exit gates to place near the maze entry.")
				.defineInRange("spawnOverworldPortalCount", 1, 0, 4);
		builder.pop();

		builder.push("overworld_bleed");
		ENABLE_OVERWORLD_BLEED = builder
				.comment("While players stay in maze dimensions, gradually replace overworld chunks with maze structures.")
				.define("enableOverworldBleed", true);
		BLEED_TICKS_PER_CHUNK = builder
				.comment("Maze dwell ticks required before one additional overworld chunk is corrupted.")
				.defineInRange("ticksPerChunk", 6000, 20, 72000);
		BLEED_SPAWN_RING_RADIUS = builder
				.comment("Chunk radius around world spawn excluded from gradual corruption (loaded chunks near spawn are skipped).")
				.defineInRange("spawnRingRadius", 24, 1, 128);
		BLEED_CHUNKS_PER_TICK = builder
				.comment("Maximum overworld chunks corrupted per server tick burst (runs every second).")
				.defineInRange("chunksPerTick", 1, 1, 8);
		BLEED_RANDOM_PHASE_MIN_DISTANCE = builder
				.comment("Minimum chunk distance from spawn for random-phase corruption.")
				.defineInRange("randomPhaseMinDistance", 28, 4, 512);
		ENABLE_BLEED_NEW_CHUNK_GEN = builder
				.comment("After the first maze entry, newly generated overworld chunks can spawn as maze structures.")
				.define("enableNewChunkMazeGen", true);
		BLEED_NEW_CHUNK_CHANCE = builder
				.comment("Base probability that a newly generated overworld chunk becomes a random maze structure.")
				.defineInRange("newChunkMazeChance", 0.05D, 0.0D, 1.0D);
		BLEED_PER_DIMENSION_CHANCE_BONUS = builder
				.comment("Added new-chunk maze chance per deepest maze dimension index reached.")
				.defineInRange("perDimensionChanceBonus", 0.005D, 0.0D, 1.0D);
		BLEED_NEW_CHUNK_CHANCE_CAP = builder
				.comment("Hard cap on new-chunk maze chance so late-game depth scaling cannot reach 100%.")
				.defineInRange("newChunkMazeChanceCap", 0.12D, 0.0D, 1.0D);
		BLEED_NEW_CHUNK_GRID_SPACING = builder
				.comment("Only chunks on this grid (chunkX and chunkZ divisible by spacing) can roll for maze generation. 4 means ~1/16 of chunks are eligible.")
				.defineInRange("newChunkGridSpacing", 4, 1, 16);
		BLEED_NEW_CHUNKS_PER_SECOND = builder
				.comment("Maximum newly generated overworld maze chunks applied per second.")
				.defineInRange("newChunksPerSecond", 1, 0, 4);
		BLEED_PER_DIMENSION_SPEED_BONUS = builder
				.comment("Gradual overworld corruption speed multiplier increase per maze dimension index.")
				.defineInRange("perDimensionSpeedBonus", 0.10D, 0.0D, 2.0D);
		BLEED_MAX_DEPTH_FOR_SCALING = builder
				.comment("Cap on maze index used for overworld bleed intensity scaling.")
				.defineInRange("maxDepthForScaling", 50, 1, 1000);
		BLEED_MIN_TICKS_PER_CHUNK = builder
				.comment("Minimum ticks-per-chunk floor for gradual overworld corruption at max scaling.")
				.defineInRange("minTicksPerChunk", 100, 20, 6000);
		BLEED_DIMENSIONS_PER_EXTRA_CHUNK = builder
				.comment("Every N maze dimensions adds one extra gradual-corruption chunk per second.")
				.defineInRange("dimensionsPerExtraChunk", 5, 1, 50);
		builder.pop();

		builder.push("sky_figures");
		ENABLE_SKY_FIGURES = builder
				.comment("Spawn ephemeral figures/fractals in the sky above player-loaded overworld chunks.")
				.define("enableSkyFigures", true);
		SKY_FIGURE_INTERVAL_TICKS = builder
				.comment("Server ticks between sky figure spawn attempts.")
				.defineInRange("intervalTicks", 5000, 100, 72000);
		SKY_FIGURE_SPAWN_CHANCE = builder
				.comment("Chance (0-1) that a spawn attempt succeeds each interval.")
				.defineInRange("spawnChance", 0.25D, 0.0D, 1.0D);
		SKY_FIGURE_LIFESPAN_TICKS = builder
				.comment("How long sky figures remain before despawning.")
				.defineInRange("lifespanTicks", 400, 40, 72000);
		SKY_FIGURE_MAX_ACTIVE = builder
				.comment("Maximum concurrent sky figures (blocks + entities).")
				.defineInRange("maxActiveFigures", 4, 1, 32);
		SKY_FIGURE_MIN_HEIGHT_ABOVE_TERRAIN = builder
				.comment("Minimum blocks above terrain heightmap for sky figure anchor.")
				.defineInRange("minHeightAboveTerrain", 32, 8, 256);
		SKY_FIGURE_MAX_HEIGHT_ABOVE_TERRAIN = builder
				.comment("Maximum blocks above terrain heightmap for sky figure anchor.")
				.defineInRange("maxHeightAboveTerrain", 80, 16, 320);
		builder.pop();

		builder.push("overworld_undefiend");
		ENABLE_OVERWORLD_UNDEFIEND = builder
				.comment("Spawn a dormant Undefiend stalker in overworld after the Seraph finale (FinaleState.DONE).")
				.define("enableOverworldUndefiend", true);
		OVERWORLD_UNDEFIEND_INTERVAL_TICKS = builder
				.comment("Server ticks between overworld Undefiend spawn rolls (independent of Teethman).")
				.defineInRange("intervalTicks", 4000, 200, 72000);
		OVERWORLD_UNDEFIEND_SPAWN_CHANCE = builder
				.comment("Chance (0-1) that an Undefiend spawn roll succeeds each interval.")
				.defineInRange("spawnChance", 0.45D, 0.0D, 1.0D);
		OVERWORLD_UNDEFIEND_LIFESPAN_TICKS = builder
				.comment("How long a dormant overworld Undefiend remains before despawning.")
				.defineInRange("lifespanTicks", 7200, 200, 72000);
		OVERWORLD_UNDEFIEND_SPAWN_CHUNK_DISTANCE = builder
				.comment("Preferred spawn distance from player in chunks (clamped to simulation distance at runtime).")
				.defineInRange("spawnChunkDistance", 4, 3, 16);
		OVERWORLD_UNDEFIEND_LOOK_DOT_THRESHOLD = builder
				.comment("Dot threshold for direct gaze activation (higher = narrower cone).")
				.defineInRange("lookDotThreshold", 0.96D, 0.5D, 1.0D);
		builder.pop();

		builder.push("overworld_teethman");
		ENABLE_OVERWORLD_TEETHMAN = builder
				.comment("Spawn a dormant Teethman stalker in overworld after the Seraph finale (FinaleState.DONE).")
				.define("enableOverworldTeethman", true);
		OVERWORLD_TEETHMAN_INTERVAL_TICKS = builder
				.comment("Server ticks between overworld Teethman spawn rolls (independent of Undefiend).")
				.defineInRange("intervalTicks", 5000, 200, 72000);
		OVERWORLD_TEETHMAN_SPAWN_CHANCE = builder
				.comment("Chance (0-1) that a Teethman spawn roll succeeds each interval.")
				.defineInRange("spawnChance", 0.45D, 0.0D, 1.0D);
		OVERWORLD_TEETHMAN_LIFESPAN_TICKS = builder
				.comment("How long a dormant overworld Teethman remains before despawning.")
				.defineInRange("lifespanTicks", 7200, 200, 72000);
		OVERWORLD_TEETHMAN_SPAWN_CHUNK_DISTANCE = builder
				.comment("Preferred spawn distance from player in chunks (clamped to simulation distance at runtime).")
				.defineInRange("spawnChunkDistance", 4, 3, 16);
		OVERWORLD_TEETHMAN_LOOK_DOT_THRESHOLD = builder
				.comment("Dot threshold for direct gaze activation (higher = narrower cone).")
				.defineInRange("lookDotThreshold", 0.96D, 0.5D, 1.0D);
		builder.pop();

		builder.push("overworld_chasers");
		OVERWORLD_CHASER_INTERVAL_TICKS = builder
				.comment("Legacy shared interval (unused; see overworld_undefiend / overworld_teethman). Kept for config compatibility.")
				.defineInRange("intervalTicks", 24000, 200, 72000);
		OVERWORLD_CHASER_DAILY_CHANCE = builder
				.comment("Legacy shared chance (unused; see overworld_undefiend / overworld_teethman). Kept for config compatibility.")
				.defineInRange("dailyChance", 0.40D, 0.0D, 1.0D);
		builder.pop();

		builder.push("maze_chasers");
		ENABLE_MAZE_CHASERS = builder
				.comment("Dynamically spawn maze chasers (Undefiend/Entity1/Entity2/Entity3/Teethman) while players explore maze dimensions.")
				.define("enabled", true);
		MAZE_CHASER_INTERVAL_TICKS = builder
				.comment("Server ticks between chaser spawn rolls per player in a maze.")
				.defineInRange("intervalTicks", 2400, 200, 72000);
		MAZE_CHASER_SPAWN_CHANCE = builder
				.comment("Chance (0-1) that a spawn attempt succeeds each interval, if no managed chaser is already nearby.")
				.defineInRange("spawnChance", 0.40D, 0.0D, 1.0D);
		MAZE_CHASER_MIN_DISTANCE = builder
				.comment("Minimum spawn distance from the player in blocks.")
				.defineInRange("minDistance", 28.0D, 16.0D, 128.0D);
		MAZE_CHASER_MAX_DISTANCE = builder
				.comment("Maximum spawn distance from the player in blocks.")
				.defineInRange("maxDistance", 52.0D, 24.0D, 160.0D);
		builder.pop();

		builder.push("notextureman");
		ENABLE_NOTEXTUREMAN = builder
				.comment("Spawn the passive Notextureman offer mob inside maze dimensions.")
				.define("enableNotextureman", true);
		NOTEXTUREMAN_INTERVAL_TICKS = builder
				.comment("Server ticks between Notextureman spawn attempts per player in a maze.")
				.defineInRange("intervalTicks", 3000, 200, 72000);
		NOTEXTUREMAN_SPAWN_CHANCE = builder
				.comment("Chance (0-1) that a spawn attempt succeeds each interval.")
				.defineInRange("spawnChance", 0.40D, 0.0D, 1.0D);
		NOTEXTUREMAN_MAX_LEVEL = builder
				.comment("Maximum random maze level offered in the teleport prompt (1..maxLevel).")
				.defineInRange("maxLevel", 100, 1, 1000000);
		NOTEXTUREMAN_TRIGGER_DISTANCE = builder
				.comment("Distance in blocks at which Notextureman stops and opens the offer panel.")
				.defineInRange("triggerDistance", 2.5D, 1.0D, 16.0D);
		NOTEXTUREMAN_FLEE_DURATION_TICKS = builder
				.comment("Ticks Notextureman flees after a declined offer before despawning.")
				.defineInRange("fleeDurationTicks", 120, 20, 72000);
		builder.pop();

		SPEC = builder.build();
	}

	private MazeConfig() {
	}

	public static void bake() {
		if (!SPEC.isLoaded()) {
			return;
		}
		enableDynamicDimensions = ENABLE_DYNAMIC_DIMENSIONS.get();
		maxLevelsCap = MAX_LEVELS_CAP.get();
		anomalyChance = ANOMALY_CHANCE.get();
		enableRandomVoices = ENABLE_RANDOM_VOICES.get();
		randomVoiceChance = RANDOM_VOICE_CHANCE.get();
		randomVoiceCheckIntervalTicks = RANDOM_VOICE_CHECK_INTERVAL_TICKS.get();
		randomVoiceCooldownTicks = RANDOM_VOICE_COOLDOWN_TICKS.get();
		randomVoiceMinDistance = RANDOM_VOICE_MIN_DISTANCE.get();
		randomVoiceMaxDistance = RANDOM_VOICE_MAX_DISTANCE.get();
		enableRandomMessages = ENABLE_RANDOM_MESSAGES.get();
		randomMessageChance = RANDOM_MESSAGE_CHANCE.get();
		randomMessageCheckIntervalTicks = RANDOM_MESSAGE_CHECK_INTERVAL_TICKS.get();
		randomMessageCooldownTicks = RANDOM_MESSAGE_COOLDOWN_TICKS.get();
		randomMessageDurationTicks = RANDOM_MESSAGE_DURATION_TICKS.get();
		enableLights = ENABLE_LIGHTS.get();
		portalChunkSpacing = PORTAL_CHUNK_SPACING.get();
		spawnPortalRadius = SPAWN_PORTAL_RADIUS.get();
		spawnPortalCount = SPAWN_PORTAL_COUNT.get();
		spawnOverworldPortalCount = SPAWN_OVERWORLD_PORTAL_COUNT.get();
		enableOverworldBleed = ENABLE_OVERWORLD_BLEED.get();
		bleedTicksPerChunk = BLEED_TICKS_PER_CHUNK.get();
		bleedSpawnRingRadius = BLEED_SPAWN_RING_RADIUS.get();
		bleedChunksPerTick = BLEED_CHUNKS_PER_TICK.get();
		bleedRandomPhaseMinDistance = BLEED_RANDOM_PHASE_MIN_DISTANCE.get();
		enableBleedNewChunkGen = ENABLE_BLEED_NEW_CHUNK_GEN.get();
		bleedNewChunkChance = BLEED_NEW_CHUNK_CHANCE.get();
		bleedPerDimensionChanceBonus = BLEED_PER_DIMENSION_CHANCE_BONUS.get();
		bleedNewChunkChanceCap = BLEED_NEW_CHUNK_CHANCE_CAP.get();
		bleedNewChunkGridSpacing = BLEED_NEW_CHUNK_GRID_SPACING.get();
		bleedNewChunksPerSecond = BLEED_NEW_CHUNKS_PER_SECOND.get();
		bleedPerDimensionSpeedBonus = BLEED_PER_DIMENSION_SPEED_BONUS.get();
		bleedMaxDepthForScaling = BLEED_MAX_DEPTH_FOR_SCALING.get();
		bleedMinTicksPerChunk = BLEED_MIN_TICKS_PER_CHUNK.get();
		bleedDimensionsPerExtraChunk = BLEED_DIMENSIONS_PER_EXTRA_CHUNK.get();
		enableSkyFigures = ENABLE_SKY_FIGURES.get();
		skyFigureIntervalTicks = SKY_FIGURE_INTERVAL_TICKS.get();
		skyFigureSpawnChance = SKY_FIGURE_SPAWN_CHANCE.get();
		skyFigureLifespanTicks = SKY_FIGURE_LIFESPAN_TICKS.get();
		skyFigureMaxActive = SKY_FIGURE_MAX_ACTIVE.get();
		skyFigureMinHeightAboveTerrain = SKY_FIGURE_MIN_HEIGHT_ABOVE_TERRAIN.get();
		skyFigureMaxHeightAboveTerrain = SKY_FIGURE_MAX_HEIGHT_ABOVE_TERRAIN.get();
		enableOverworldUndefiend = ENABLE_OVERWORLD_UNDEFIEND.get();
		overworldUndefiendIntervalTicks = OVERWORLD_UNDEFIEND_INTERVAL_TICKS.get();
		overworldUndefiendSpawnChance = OVERWORLD_UNDEFIEND_SPAWN_CHANCE.get();
		overworldUndefiendLifespanTicks = OVERWORLD_UNDEFIEND_LIFESPAN_TICKS.get();
		overworldUndefiendSpawnChunkDistance = OVERWORLD_UNDEFIEND_SPAWN_CHUNK_DISTANCE.get();
		overworldUndefiendLookDotThreshold = OVERWORLD_UNDEFIEND_LOOK_DOT_THRESHOLD.get();
		enableOverworldTeethman = ENABLE_OVERWORLD_TEETHMAN.get();
		overworldTeethmanIntervalTicks = OVERWORLD_TEETHMAN_INTERVAL_TICKS.get();
		overworldTeethmanSpawnChance = OVERWORLD_TEETHMAN_SPAWN_CHANCE.get();
		overworldTeethmanLifespanTicks = OVERWORLD_TEETHMAN_LIFESPAN_TICKS.get();
		overworldTeethmanSpawnChunkDistance = OVERWORLD_TEETHMAN_SPAWN_CHUNK_DISTANCE.get();
		overworldTeethmanLookDotThreshold = OVERWORLD_TEETHMAN_LOOK_DOT_THRESHOLD.get();
		overworldChaserIntervalTicks = OVERWORLD_CHASER_INTERVAL_TICKS.get();
		overworldChaserDailyChance = OVERWORLD_CHASER_DAILY_CHANCE.get();
		enableMazeChasers = ENABLE_MAZE_CHASERS.get();
		mazeChaserIntervalTicks = MAZE_CHASER_INTERVAL_TICKS.get();
		mazeChaserSpawnChance = MAZE_CHASER_SPAWN_CHANCE.get();
		mazeChaserMinDistance = Math.min(MAZE_CHASER_MIN_DISTANCE.get(), MAZE_CHASER_MAX_DISTANCE.get());
		mazeChaserMaxDistance = Math.max(MAZE_CHASER_MIN_DISTANCE.get(), MAZE_CHASER_MAX_DISTANCE.get());
		enableNotextureman = ENABLE_NOTEXTUREMAN.get();
		notexturemanIntervalTicks = NOTEXTUREMAN_INTERVAL_TICKS.get();
		notexturemanSpawnChance = NOTEXTUREMAN_SPAWN_CHANCE.get();
		notexturemanMaxLevel = NOTEXTUREMAN_MAX_LEVEL.get();
		notexturemanTriggerDistance = NOTEXTUREMAN_TRIGGER_DISTANCE.get();
		notexturemanFleeDurationTicks = NOTEXTUREMAN_FLEE_DURATION_TICKS.get();
	}
}
