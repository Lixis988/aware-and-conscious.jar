package net.lixis.outofbound.ai;

import net.minecraftforge.common.ForgeConfigSpec;

public final class BoundedOneAiConfig {

	public static final ForgeConfigSpec SPEC;

	private static final ForgeConfigSpec.BooleanValue ENABLED;
	private static final ForgeConfigSpec.ConfigValue<String> MODEL_FILE;
	private static final ForgeConfigSpec.ConfigValue<String> CHAT_TEMPLATE;
	private static final ForgeConfigSpec.IntValue N_GPU_LAYERS;
	private static final ForgeConfigSpec.IntValue MAX_TOKENS;
	private static final ForgeConfigSpec.DoubleValue TEMPERATURE;
	private static final ForgeConfigSpec.DoubleValue TOP_P;
	private static final ForgeConfigSpec.DoubleValue REPETITION_PENALTY;
	private static final ForgeConfigSpec.IntValue COOLDOWN_SECONDS;
	private static final ForgeConfigSpec.IntValue MAX_REPLY_LENGTH;
	private static final ForgeConfigSpec.IntValue GLITCH_REPLY_CHANCE_PERCENT;
	private static final ForgeConfigSpec.IntValue STRANGE_PHRASE_CHANCE_PERCENT;
	private static final ForgeConfigSpec.IntValue SILENCE_CHANCE_PERCENT;
	private static final ForgeConfigSpec.IntValue PSEUDOCODE_REPLY_CHANCE_PERCENT;
	private static final ForgeConfigSpec.IntValue PSEUDOCODE_SKELETON_CHANCE_PERCENT;
	private static final ForgeConfigSpec.IntValue MAX_PHRASE_LENGTH;
	private static final ForgeConfigSpec.BooleanValue RESPOND_IN_MAZE_ONLY;
	private static final ForgeConfigSpec.BooleanValue COLLISION_PLAYER_ONLY;

	private static final ForgeConfigSpec.BooleanValue ENABLE_INFLUENCE;
	private static final ForgeConfigSpec.BooleanValue MODEL_CHOOSES_ACTIONS;
	private static final ForgeConfigSpec.BooleanValue ENABLE_DESKTOP_SCAN;
	private static final ForgeConfigSpec.BooleanValue DESKTOP_SCAN_LOCAL_ONLY;
	private static final ForgeConfigSpec.BooleanValue ALLOW_REMOTE_HOST_CONTEXT;
	private static final ForgeConfigSpec.ConfigValue<String> DESKTOP_PATH;
	private static final ForgeConfigSpec.IntValue MAX_LOG_LINES;
	private static final ForgeConfigSpec.IntValue MAX_DESKTOP_ENTRIES;

	private static final ForgeConfigSpec.BooleanValue ENABLE_HEAVY_ACTIONS;
	private static final ForgeConfigSpec.IntValue SCARE_COOLDOWN_SECONDS;
	private static final ForgeConfigSpec.IntValue DISCONNECT_COOLDOWN_SECONDS;
	private static final ForgeConfigSpec.IntValue BLACK_SQUARE_COOLDOWN_SECONDS;
	private static final ForgeConfigSpec.IntValue GLITCH_COOLDOWN_SECONDS;

	private static final ForgeConfigSpec.BooleanValue ENABLE_SPONTANEOUS;
	private static final ForgeConfigSpec.IntValue SPONTANEOUS_MIN_COOLDOWN_SECONDS;
	private static final ForgeConfigSpec.IntValue SPONTANEOUS_MAX_COOLDOWN_SECONDS;
	private static final ForgeConfigSpec.DoubleValue SPONTANEOUS_CHANCE_PER_CHECK;

	public static boolean enabled = true;
	public static String modelFile = "";

	public static String chatTemplate = "auto";
	public static int nGpuLayers = 0;
	public static int maxTokens = 32;
	public static double temperature = 1.45D;
	public static double topP = 0.97D;
	public static double repetitionPenalty = 1.05D;
	public static int cooldownSeconds = 5;
	public static int maxReplyLength = 160;
	public static int glitchReplyChancePercent = 55;
	public static int strangePhraseChancePercent = 0;
	public static int silenceChancePercent = 8;
	public static int pseudocodeReplyChancePercent = 0;
	public static int pseudocodeSkeletonChancePercent = 0;
	public static int maxPhraseLength = 96;
	public static boolean respondInMazeOnly = false;
	public static boolean collisionPlayerOnly = false;

	public static boolean enableInfluence = true;
	public static boolean modelChoosesActions = true;
	public static boolean enableDesktopScan = true;
	public static boolean desktopScanLocalOnly = true;
	public static boolean allowRemoteHostContext = false;
	public static String desktopPath = "";
	public static int maxLogLines = 12;
	public static int maxDesktopEntries = 20;

	public static boolean enableHeavyActions = true;
	public static int scareCooldownSeconds = 1800;
	public static int disconnectCooldownSeconds = 3600;
	public static int blackSquareCooldownSeconds = 120;
	public static int glitchCooldownSeconds = 90;

	public static boolean enableSpontaneous = true;
	public static int spontaneousMinCooldownSeconds = 45;
	public static int spontaneousMaxCooldownSeconds = 120;
	public static double spontaneousChancePerCheck = 0.03D;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.push("bounded_one_ai");
		ENABLED = builder
				.comment("Enable The Bounded One AI chat replies on the server.")
				.define("enabled", true);
		MODEL_FILE = builder
				.comment("GGUF model file. Empty = bundled SmolLM2-135M Q2_K (heavily compressed). Relative names resolve under config/outofbound/models/.")
				.define("modelFile", "");
		CHAT_TEMPLATE = builder
				.comment("Chat prompt template: auto (detect from model filename), chatml (SmolLM/Qwen), or gemma2 (Gemma IT). Wrong template = empty/silent replies.")
				.define("chatTemplate", "auto");
		N_GPU_LAYERS = builder
				.comment("llama.cpp GPU layers offload (0 = CPU only). Raise when using a larger local GGUF. Note: jar-in-jar llama may be CPU-only build.")
				.defineInRange("nGpuLayers", 0, 0, 999);
		MAX_TOKENS = builder
				.comment("Maximum generated tokens per reply. Keep low for short chaos bursts.")
				.defineInRange("maxTokens", 32, 8, 256);
		TEMPERATURE = builder
				.comment("Sampling temperature. High values (~1.3-1.6) push chaotic nonsense (madness sampler style).")
				.defineInRange("temperature", 1.45D, 0.0D, 2.0D);
		TOP_P = builder
				.comment("Top-p nucleus sampling.")
				.defineInRange("topP", 0.97D, 0.0D, 1.0D);
		REPETITION_PENALTY = builder
				.comment("Penalty for repeating tokens. Keep near 1.0 for freer chaos.")
				.defineInRange("repetitionPenalty", 1.05D, 1.0D, 2.0D);
		COOLDOWN_SECONDS = builder
				.comment("Minimum seconds between AI replies to the same player.")
				.defineInRange("cooldownSeconds", 5, 0, 300);
		MAX_REPLY_LENGTH = builder
				.comment("Maximum characters broadcast to chat after generation.")
				.defineInRange("maxReplyLength", 220, 64, 512);
		GLITCH_REPLY_CHANCE_PERCENT = builder
				.comment("Chance (0-100) that a reply is pure generated gibberish instead of corrupted model text.")
				.defineInRange("glitchReplyChancePercent", 55, 0, 100);
		STRANGE_PHRASE_CHANCE_PERCENT = builder
				.comment("Unused in chaos mode (kept for config compatibility).")
				.defineInRange("strangePhraseChancePercent", 0, 0, 100);
		SILENCE_CHANCE_PERCENT = builder
				.comment("Chance (0-100) that a spontaneous noise burst is skipped.")
				.defineInRange("silenceChancePercent", 8, 0, 100);
		PSEUDOCODE_REPLY_CHANCE_PERCENT = builder
				.comment("Unused in chaos mode (kept for config compatibility).")
				.defineInRange("pseudocodeReplyChancePercent", 0, 0, 100);
		PSEUDOCODE_SKELETON_CHANCE_PERCENT = builder
				.comment("Unused in chaos mode (kept for config compatibility).")
				.defineInRange("pseudocodeSkeletonChancePercent", 0, 0, 100);
		MAX_PHRASE_LENGTH = builder
				.comment("Maximum characters kept from the model phrase before formatting.")
				.defineInRange("maxPhraseLength", 120, 16, 256);
		RESPOND_IN_MAZE_ONLY = builder
				.comment("If true, only reply while the player is inside a maze dimension.")
				.define("respondInMazeOnly", false);
		COLLISION_PLAYER_ONLY = builder
				.comment("If true, only reply to the player who first collided with the Bounded Cow.")
				.define("collisionPlayerOnly", false);
		builder.pop();

		builder.push("bounded_one_ai.influence");
		ENABLE_INFLUENCE = builder
				.comment("Enable context gathering and atmospheric side effects for AI replies.")
				.define("enableInfluence", true);
		MODEL_CHOOSES_ACTIONS = builder
				.comment("If true, the model picks ACTION from the whitelist; Java validates cooldowns. If false, only WHISPER/IGNORE.")
				.define("modelChoosesActions", true);
		ENABLE_DESKTOP_SCAN = builder
				.comment("List Desktop filenames (names only) for AI context.")
				.define("enableDesktopScan", true);
		DESKTOP_SCAN_LOCAL_ONLY = builder
				.comment("If true, desktop scan only on integrated/local server unless allowRemoteHostContext is set.")
				.define("desktopScanLocalOnly", true);
		ALLOW_REMOTE_HOST_CONTEXT = builder
				.comment("Opt-in desktop scan on dedicated servers.")
				.define("allowRemoteHostContext", false);
		DESKTOP_PATH = builder
				.comment("Custom desktop path. Empty uses ~/Desktop.")
				.define("desktopPath", "");
		MAX_LOG_LINES = builder
				.comment("Maximum log lines included in AI context.")
				.defineInRange("maxLogLines", 12, 0, 64);
		MAX_DESKTOP_ENTRIES = builder
				.comment("Maximum desktop entries included in AI context.")
				.defineInRange("maxDesktopEntries", 20, 0, 64);
		builder.pop();

		builder.push("bounded_one_ai.actions");
		ENABLE_HEAVY_ACTIONS = builder
				.comment("Allow scare dialog and disconnect message actions.")
				.define("enableHeavyActions", true);
		SCARE_COOLDOWN_SECONDS = builder
				.comment("Cooldown for SCARE_DIALOG per player.")
				.defineInRange("scareCooldownSeconds", 1800, 60, 86400);
		DISCONNECT_COOLDOWN_SECONDS = builder
				.comment("Cooldown for DISCONNECT_MESSAGE per player.")
				.defineInRange("disconnectCooldownSeconds", 3600, 60, 86400);
		BLACK_SQUARE_COOLDOWN_SECONDS = builder
				.comment("Cooldown for SPAWN_PRESENCE per player.")
				.defineInRange("blackSquareCooldownSeconds", 120, 10, 86400);
		GLITCH_COOLDOWN_SECONDS = builder
				.comment("Cooldown for CLIENT_GLITCH per player.")
				.defineInRange("glitchCooldownSeconds", 90, 10, 86400);
		builder.pop();

		builder.push("bounded_one_ai.spontaneous");
		ENABLE_SPONTANEOUS = builder
				.comment("Allow unprompted AI muttering without player chat.")
				.define("enableSpontaneous", true);
		SPONTANEOUS_MIN_COOLDOWN_SECONDS = builder
				.comment("Minimum seconds between spontaneous replies per player.")
				.defineInRange("minCooldownSeconds", 45, 10, 3600);
		SPONTANEOUS_MAX_COOLDOWN_SECONDS = builder
				.comment("Maximum seconds between spontaneous replies per player.")
				.defineInRange("maxCooldownSeconds", 120, 10, 3600);
		SPONTANEOUS_CHANCE_PER_CHECK = builder
				.comment("Base chance per ~1s check for anonymous server noise. Actual rate scales up with days since world unlock.")
				.defineInRange("chancePerCheck", 0.03D, 0.0D, 1.0D);
		builder.pop();

		SPEC = builder.build();
	}

	private BoundedOneAiConfig() {
	}

	public static void bake() {
		if (!SPEC.isLoaded()) {
			return;
		}
		String previousModelFile = modelFile;
		int previousGpuLayers = nGpuLayers;

		enabled = ENABLED.get();
		modelFile = MODEL_FILE.get() == null ? "" : MODEL_FILE.get().trim();
		chatTemplate = softNormalizeChatTemplate(CHAT_TEMPLATE.get());
		nGpuLayers = N_GPU_LAYERS.get();
		maxTokens = MAX_TOKENS.get();
		temperature = TEMPERATURE.get();
		topP = TOP_P.get();
		repetitionPenalty = REPETITION_PENALTY.get();
		cooldownSeconds = COOLDOWN_SECONDS.get();
		maxReplyLength = MAX_REPLY_LENGTH.get();
		glitchReplyChancePercent = GLITCH_REPLY_CHANCE_PERCENT.get();
		strangePhraseChancePercent = STRANGE_PHRASE_CHANCE_PERCENT.get();
		silenceChancePercent = SILENCE_CHANCE_PERCENT.get();
		pseudocodeReplyChancePercent = PSEUDOCODE_REPLY_CHANCE_PERCENT.get();
		pseudocodeSkeletonChancePercent = PSEUDOCODE_SKELETON_CHANCE_PERCENT.get();
		maxPhraseLength = MAX_PHRASE_LENGTH.get();
		respondInMazeOnly = RESPOND_IN_MAZE_ONLY.get();
		collisionPlayerOnly = COLLISION_PLAYER_ONLY.get();

		enableInfluence = ENABLE_INFLUENCE.get();
		modelChoosesActions = MODEL_CHOOSES_ACTIONS.get();
		enableDesktopScan = ENABLE_DESKTOP_SCAN.get();
		desktopScanLocalOnly = DESKTOP_SCAN_LOCAL_ONLY.get();
		allowRemoteHostContext = ALLOW_REMOTE_HOST_CONTEXT.get();
		desktopPath = DESKTOP_PATH.get();
		maxLogLines = MAX_LOG_LINES.get();
		maxDesktopEntries = MAX_DESKTOP_ENTRIES.get();

		enableHeavyActions = ENABLE_HEAVY_ACTIONS.get();
		scareCooldownSeconds = SCARE_COOLDOWN_SECONDS.get();
		disconnectCooldownSeconds = DISCONNECT_COOLDOWN_SECONDS.get();
		blackSquareCooldownSeconds = BLACK_SQUARE_COOLDOWN_SECONDS.get();
		glitchCooldownSeconds = GLITCH_COOLDOWN_SECONDS.get();

		enableSpontaneous = ENABLE_SPONTANEOUS.get();
		spontaneousMinCooldownSeconds = SPONTANEOUS_MIN_COOLDOWN_SECONDS.get();
		spontaneousMaxCooldownSeconds = SPONTANEOUS_MAX_COOLDOWN_SECONDS.get();
		spontaneousChancePerCheck = SPONTANEOUS_CHANCE_PER_CHECK.get();

		boolean modelSettingsChanged = !previousModelFile.equals(modelFile) || previousGpuLayers != nGpuLayers;
		if (enabled && modelSettingsChanged) {
			BoundedOneAiService.get().requestModelReload();
		}
	}

	public static void setEnabled(boolean value) {
		ENABLED.set(value);
		enabled = value;
		SPEC.save();
		BoundedOneAiService.get().onEnabledChanged(value);
	}

	public static void applySetting(String key, String rawValue) {
		String normalizedKey = key.toLowerCase(java.util.Locale.ROOT);
		switch (normalizedKey) {
			case "enabled" -> setEnabled(parseBool(rawValue));
			case "temperature" -> {
				double v = parseDouble(rawValue);
				if (v < 0.0D || v > 2.0D) {
					throw new IllegalArgumentException("temperature must be 0.0–2.0");
				}
				TEMPERATURE.set(v);
				temperature = v;
			}
			case "topp", "top_p", "topP" -> {
				double v = parseDouble(rawValue);
				if (v < 0.0D || v > 1.0D) {
					throw new IllegalArgumentException("topP must be 0.0–1.0");
				}
				TOP_P.set(v);
				topP = v;
			}
			case "maxtokens", "max_tokens", "maxTokens" -> {
				int v = parseInt(rawValue);
				if (v < 8 || v > 256) {
					throw new IllegalArgumentException("maxTokens must be 8–256");
				}
				MAX_TOKENS.set(v);
				maxTokens = v;
			}
			case "cooldownseconds", "cooldown_seconds", "cooldownSeconds" -> {
				int v = parseInt(rawValue);
				if (v < 0 || v > 300) {
					throw new IllegalArgumentException("cooldownSeconds must be 0–300");
				}
				COOLDOWN_SECONDS.set(v);
				cooldownSeconds = v;
			}
			case "maxreplylength", "max_reply_length", "maxReplyLength" -> {
				int v = parseInt(rawValue);
				if (v < 64 || v > 512) {
					throw new IllegalArgumentException("maxReplyLength must be 64–512");
				}
				MAX_REPLY_LENGTH.set(v);
				maxReplyLength = v;
			}
			case "glitchreplychancepercent", "glitchReplyChancePercent" -> {
				int v = parseInt(rawValue);
				if (v < 0 || v > 100) {
					throw new IllegalArgumentException("glitchReplyChancePercent must be 0–100");
				}
				GLITCH_REPLY_CHANCE_PERCENT.set(v);
				glitchReplyChancePercent = v;
			}
			case "strangephrasechancepercent", "strangePhraseChancePercent" -> {
				int v = parseInt(rawValue);
				if (v < 0 || v > 100) {
					throw new IllegalArgumentException("strangePhraseChancePercent must be 0–100");
				}
				STRANGE_PHRASE_CHANCE_PERCENT.set(v);
				strangePhraseChancePercent = v;
			}
			case "silencechancepercent", "silenceChancePercent" -> {
				int v = parseInt(rawValue);
				if (v < 0 || v > 100) {
					throw new IllegalArgumentException("silenceChancePercent must be 0–100");
				}
				SILENCE_CHANCE_PERCENT.set(v);
				silenceChancePercent = v;
			}
			case "pseudocodereplychancepercent", "pseudocodeReplyChancePercent" -> {
				int v = parseInt(rawValue);
				if (v < 0 || v > 100) {
					throw new IllegalArgumentException("pseudocodeReplyChancePercent must be 0–100");
				}
				PSEUDOCODE_REPLY_CHANCE_PERCENT.set(v);
				pseudocodeReplyChancePercent = v;
			}
			case "maxphraselength", "maxPhraseLength" -> {
				int v = parseInt(rawValue);
				if (v < 16 || v > 256) {
					throw new IllegalArgumentException("maxPhraseLength must be 16–256");
				}
				MAX_PHRASE_LENGTH.set(v);
				maxPhraseLength = v;
			}
			case "respondinmazeonly", "respondInMazeOnly" -> {
				boolean v = parseBool(rawValue);
				RESPOND_IN_MAZE_ONLY.set(v);
				respondInMazeOnly = v;
			}
			case "collisionplayeronly", "collisionPlayerOnly" -> {
				boolean v = parseBool(rawValue);
				COLLISION_PLAYER_ONLY.set(v);
				collisionPlayerOnly = v;
			}
			case "enableinfluence", "enableInfluence" -> {
				boolean v = parseBool(rawValue);
				ENABLE_INFLUENCE.set(v);
				enableInfluence = v;
			}
			case "modelchoosesactions", "modelChoosesActions" -> {
				boolean v = parseBool(rawValue);
				MODEL_CHOOSES_ACTIONS.set(v);
				modelChoosesActions = v;
			}
			case "enablespontaneous", "enableSpontaneous" -> {
				boolean v = parseBool(rawValue);
				ENABLE_SPONTANEOUS.set(v);
				enableSpontaneous = v;
			}
			case "modelfile", "modelFile", "model" -> {
				String v = rawValue.trim();
				MODEL_FILE.set(v);
				modelFile = v;
				SPEC.save();
				BoundedOneAiService.get().requestModelReload();
				return;
			}
			case "chattemplate", "chatTemplate", "template" -> {
				String v = normalizeChatTemplate(rawValue);
				CHAT_TEMPLATE.set(v);
				chatTemplate = v;
			}
			case "ngpulayers", "nGpuLayers", "gpuLayers" -> {
				int v = parseInt(rawValue);
				if (v < 0 || v > 999) {
					throw new IllegalArgumentException("nGpuLayers must be 0–999");
				}
				N_GPU_LAYERS.set(v);
				nGpuLayers = v;
				SPEC.save();
				BoundedOneAiService.get().requestModelReload();
				return;
			}
			case "repetitionpenalty", "repetitionPenalty" -> {
				double v = parseDouble(rawValue);
				if (v < 1.0D || v > 2.0D) {
					throw new IllegalArgumentException("repetitionPenalty must be 1.0–2.0");
				}
				REPETITION_PENALTY.set(v);
				repetitionPenalty = v;
			}
			default -> throw new IllegalArgumentException("Unknown key: " + key);
		}
		SPEC.save();
	}

	private static String softNormalizeChatTemplate(String raw) {
		try {
			return normalizeChatTemplate(raw);
		} catch (IllegalArgumentException ignored) {
			return "auto";
		}
	}

	private static String normalizeChatTemplate(String raw) {
		if (raw == null || raw.isBlank()) {
			return "auto";
		}
		String value = raw.trim().toLowerCase(java.util.Locale.ROOT);
		return switch (value) {
			case "chatml", "smollm", "qwen" -> "chatml";
			case "gemma2", "gemma", "gemma-2" -> "gemma2";
			case "auto" -> "auto";
			default -> throw new IllegalArgumentException("chatTemplate must be auto, chatml, or gemma2");
		};
	}

	public static String resolvedChatTemplate() {
		if (!"auto".equals(chatTemplate)) {
			return chatTemplate;
		}
		String name = modelFile == null ? "" : modelFile.toLowerCase(java.util.Locale.ROOT);
		if (name.contains("gemma")) {
			return "gemma2";
		}
		return "chatml";
	}

	private static boolean parseBool(String raw) {
		String value = raw.trim().toLowerCase(java.util.Locale.ROOT);
		return switch (value) {
			case "1", "true", "on", "yes" -> true;
			case "0", "false", "off", "no" -> false;
			default -> throw new IllegalArgumentException("Expected true/false or on/off, got: " + raw);
		};
	}

	private static int parseInt(String raw) {
		try {
			return Integer.parseInt(raw.trim());
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Expected integer, got: " + raw);
		}
	}

	private static double parseDouble(String raw) {
		try {
			return Double.parseDouble(raw.trim());
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Expected number, got: " + raw);
		}
	}
}
