package net.lixis.outofbound.ai;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;
import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.ai.influence.BoundedOneAction;
import net.lixis.outofbound.ai.influence.BoundedOneActionResolver;
import net.lixis.outofbound.ai.influence.BoundedOneActionSelector;
import net.lixis.outofbound.ai.influence.BoundedOneHostContext;
import net.lixis.outofbound.ai.influence.BoundedOneTurnParser;
import net.lixis.outofbound.ai.influence.BoundedOneTurnResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class BoundedOneAiService {

	private enum State {
		NOT_STARTED,
		LOADING,
		READY,
		FAILED
	}

	private static final BoundedOneAiService INSTANCE = new BoundedOneAiService();

	private final AtomicReference<State> state = new AtomicReference<>(State.NOT_STARTED);
	private final Map<UUID, Long> cooldownUntilMs = new ConcurrentHashMap<>();
	private final AtomicBoolean reloadQueued = new AtomicBoolean(false);

	private final ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor(r -> {
		Thread thread = new Thread(r, "outofbound-bounded-one-lifecycle");
		thread.setDaemon(true);
		thread.setContextClassLoader(BoundedOneAiService.class.getClassLoader());
		return thread;
	});

	private volatile LlamaModel model;
	private volatile ExecutorService inferenceExecutor;
	private volatile String loadedModelPath = "";
	private volatile int loadedGpuLayers = -1;

	private BoundedOneAiService() {
	}

	public static BoundedOneAiService get() {
		return INSTANCE;
	}

	public boolean isReady() {
		return state.get() == State.READY && model != null;
	}

	public boolean isOnCooldown(ServerPlayer player) {
		long now = System.currentTimeMillis();
		Long until = cooldownUntilMs.get(player.getUUID());
		return until != null && until > now;
	}

	public String runtimeStateLabel() {
		return state.get().name();
	}

	public String loadedModelPathLabel() {
		if (loadedModelPath == null || loadedModelPath.isEmpty()) {
			return "(none — not loaded)";
		}
		return loadedModelPath;
	}

	public int loadedGpuLayers() {
		return Math.max(0, loadedGpuLayers);
	}

	public void onEnabledChanged(boolean enabled) {
		if (enabled) {
			tryStartIfNeeded();
		}
	}

	public void requestModelReload() {
		if (!BoundedOneAiConfig.enabled) {
			OutofboundMod.LOGGER.info("Bounded One AI reload skipped (disabled)");
			return;
		}
		if (!reloadQueued.compareAndSet(false, true)) {
			return;
		}
		lifecycleExecutor.execute(() -> {
			try {
				withModClassLoader(() -> {
					OutofboundMod.LOGGER.info("Bounded One AI reloading model...");
					unloadInternal();
					loadInternal();
				});
			} catch (Throwable throwable) {
				OutofboundMod.LOGGER.error("Bounded One AI reload crashed", throwable);
				failInternal();
			} finally {
				reloadQueued.set(false);
			}
		});
	}

	public void tryStartIfNeeded() {
		if (!BoundedOneAiConfig.enabled) {
			return;
		}
		State current = state.get();
		if (current == State.READY || current == State.LOADING) {
			return;
		}
		beginLoad();
	}

	@SubscribeEvent
	public static void onServerAboutToStart(ServerAboutToStartEvent event) {
		if (!BoundedOneAiConfig.enabled) {
			OutofboundMod.LOGGER.info("Bounded One AI disabled in config");
			return;
		}

		INSTANCE.beginLoad();
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		INSTANCE.shutdownBlocking();
	}

	private void beginLoad() {
		lifecycleExecutor.execute(() -> {
			try {
				withModClassLoader(() -> {
					State current = state.get();
					if (current == State.READY || current == State.LOADING) {
						return;
					}
					OutofboundMod.LOGGER.info("Bounded One AI loading in background...");
					loadInternal();
				});
			} catch (Throwable throwable) {
				OutofboundMod.LOGGER.error("Bounded One AI load crashed", throwable);
				failInternal();
			}
		});
	}

	private void loadInternal() {
		state.set(State.LOADING);
		try {
			Path modelPath = BoundedOneModelExtractor.resolveModelPath();
			String absolutePath = modelPath.toAbsolutePath().toString();
			int gpuLayers = Math.max(0, BoundedOneAiConfig.nGpuLayers);

			if (model != null
					&& Objects.equals(loadedModelPath, absolutePath)
					&& loadedGpuLayers == gpuLayers) {
				state.set(State.READY);
				OutofboundMod.LOGGER.info("Bounded One AI already loaded {}", absolutePath);
				return;
			}

			closeModelUnlocked();

			inferenceExecutor = Executors.newSingleThreadExecutor(r -> {
				Thread thread = new Thread(r, "outofbound-bounded-one-ai");
				thread.setDaemon(true);
				thread.setContextClassLoader(BoundedOneAiService.class.getClassLoader());
				return thread;
			});

			ModelParameters modelParameters = new ModelParameters()
					.setModelFilePath(absolutePath)
					.setNGpuLayers(gpuLayers)
					.setNPredict(BoundedOneAiConfig.maxTokens);

			model = new LlamaModel(modelParameters);
			loadedModelPath = absolutePath;
			loadedGpuLayers = gpuLayers;
			state.set(State.READY);
			OutofboundMod.LOGGER.info("Bounded One AI ready (path={}, nGpuLayers={})", absolutePath, gpuLayers);
		} catch (Throwable throwable) {
			OutofboundMod.LOGGER.error("Bounded One AI failed to load", throwable);
			failInternal();
		}
	}

	private void failInternal() {
		closeModelUnlocked();
		loadedModelPath = "";
		loadedGpuLayers = -1;
		state.set(State.NOT_STARTED);
	}

	private void unloadInternal() {
		cooldownUntilMs.clear();
		BoundedOneSpontaneousHandler.clearCooldowns();
		closeModelUnlocked();
		loadedModelPath = "";
		loadedGpuLayers = -1;
		state.set(State.NOT_STARTED);
	}

	public void generateReply(ServerPlayer player, String userMessage, Consumer<String> onComplete) {
		generateTurn(player, userMessage, null, result -> onComplete.accept(result.displayText()));
	}

	public void generateTurn(ServerPlayer player, String userMessage, BoundedOneHostContext context,
			Consumer<BoundedOneTurnResult> onComplete) {
		if (!isReady() || inferenceExecutor == null) {
			return;
		}

		inferenceExecutor.execute(() -> {
			try {
				BoundedOneTurnResult result = runTurn(player, userMessage, context, false);
				cooldownUntilMs.put(player.getUUID(), System.currentTimeMillis() + BoundedOneAiConfig.cooldownSeconds * 1000L);
				onComplete.accept(result);
			} catch (Exception exception) {
				OutofboundMod.LOGGER.warn("Bounded One AI inference failed for {}", player.getGameProfile().getName(), exception);
			}
		});
	}

	public void generateSpontaneousTurn(ServerPlayer player, BoundedOneHostContext context,
			Consumer<BoundedOneTurnResult> onComplete) {
		if (!isReady() || inferenceExecutor == null) {
			return;
		}

		inferenceExecutor.execute(() -> {
			try {
				BoundedOneTurnResult result = runTurn(player, "", context, true);
				onComplete.accept(result);
			} catch (Exception exception) {
				OutofboundMod.LOGGER.warn("Bounded One spontaneous inference failed for {}",
						player.getGameProfile().getName(), exception);
			}
		});
	}

	private BoundedOneTurnResult runTurn(ServerPlayer player, String userMessage, BoundedOneHostContext context,
			boolean spontaneous) {
		String availableActions = BoundedOneActionSelector.formatAvailableActions(player);
		String prompt = BoundedOnePrompts.buildPrompt(player, userMessage, context, spontaneous, availableActions);
		InferenceParameters inferenceParameters = new InferenceParameters(prompt)
				.setTemperature((float) BoundedOneAiConfig.temperature)
				.setTopP((float) BoundedOneAiConfig.topP)
				.setRepeatPenalty((float) BoundedOneAiConfig.repetitionPenalty)
				.setNPredict(BoundedOneAiConfig.maxTokens)
				.setStopStrings(BoundedOnePrompts.stopStringsForTemplate());

		LlamaModel active = model;
		if (active == null) {
			OutofboundMod.LOGGER.warn("Bounded One AI: model became null mid-turn");
			return new BoundedOneTurnResult(BoundedOneAction.IGNORE, "", true, "");
		}

		OutofboundMod.LOGGER.info("Bounded One AI generating (template={}, spontaneous={}, player={})",
				BoundedOneAiConfig.resolvedChatTemplate(), spontaneous, player.getGameProfile().getName());

		StringBuilder response = new StringBuilder();
		for (LlamaOutput output : active.generate(inferenceParameters)) {
			response.append(output.text);
		}

		String raw = response.toString();
		OutofboundMod.LOGGER.info("Bounded One AI raw reply ({} chars): {}", raw.length(), preview(raw));
		return processTurn(player, userMessage, raw, context, spontaneous);
	}

	private BoundedOneTurnResult processTurn(ServerPlayer player, String userMessage, String raw,
			BoundedOneHostContext context, boolean spontaneous) {
		ServerLevel overworld = player.server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
		if (overworld == null) {
			OutofboundMod.LOGGER.warn("Bounded One AI: overworld missing, dropping reply");
			return new BoundedOneTurnResult(BoundedOneAction.IGNORE, "", true, "");
		}

		BoundedOneWorldChatData chatData = BoundedOneWorldChatData.get(overworld);
		String cleaned = cleanModelText(raw);
		BoundedOneTurnParser.ParsedTurn parsed = BoundedOneTurnParser.parse(cleaned);
		BoundedOneAction resolved = BoundedOneActionResolver.resolve(player, parsed.action(), parsed.sayText(), context);

		if (parsed.silent() || resolved == BoundedOneAction.IGNORE) {
			if (!spontaneous && !cleaned.isBlank() && resolved == BoundedOneAction.IGNORE) {
				String say = cleaned.length() > BoundedOneAiConfig.maxPhraseLength
						? cleaned.substring(0, BoundedOneAiConfig.maxPhraseLength)
						: cleaned;
				if (!chatData.isReplyBlocked(say)) {
					OutofboundMod.LOGGER.info("Bounded One AI: model IGNORE/unparsable — whispering raw fragment");
					String display = BoundedOneReplyFormatter.format(player.getGameProfile().getName(), say, context,
							BoundedOneAction.WHISPER);
					chatData.recordTurn(spontaneous ? "" : userMessage, say, overworld.getGameTime());
					return new BoundedOneTurnResult(BoundedOneAction.WHISPER, say, false, display);
				}
			}
			OutofboundMod.LOGGER.info("Bounded One AI: silent turn (IGNORE/empty/blocked)");
			return new BoundedOneTurnResult(BoundedOneAction.IGNORE, parsed.sayText(), true, "");
		}

		if (spontaneous
				&& BoundedOneAiConfig.silenceChancePercent > 0
				&& java.util.concurrent.ThreadLocalRandom.current().nextInt(100) < BoundedOneAiConfig.silenceChancePercent) {
			OutofboundMod.LOGGER.info("Bounded One AI: spontaneous silence roll");
			return new BoundedOneTurnResult(BoundedOneAction.IGNORE, "", true, "");
		}

		if (chatData.isReplyBlocked(parsed.sayText())) {
			OutofboundMod.LOGGER.warn("Bounded One AI: reply blocked by guard");
			return new BoundedOneTurnResult(BoundedOneAction.IGNORE, "", true, "");
		}

		String playerName = player.getGameProfile().getName();
		String display = BoundedOneReplyFormatter.format(playerName, parsed.sayText(), context, resolved);
		BoundedOneAction chatAction = BoundedOneAction.WHISPER;
		String recordedUser = spontaneous ? "" : userMessage;
		chatData.recordTurn(recordedUser, parsed.sayText(), overworld.getGameTime());
		return new BoundedOneTurnResult(chatAction, parsed.sayText(), false, display);
	}

	private static String preview(String text) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		String oneLine = text.replace('\n', ' ').trim();
		return oneLine.length() <= 120 ? oneLine : oneLine.substring(0, 120) + "...";
	}

	private static final String IM_END = "<|" + "im_end" + "|>";

	private static String cleanModelText(String raw) {
		if (raw == null || raw.isBlank()) {
			return "";
		}

		return raw
				.replace("<|im_start|>", "")
				.replace(IM_END, "")
				.replace("<|endoftext|>", "")
				.replace("<start_of_turn>", "")
				.replace("<end_of_turn>", "")
				.replace("<eos>", "")
				.replace("<bos>", "")
				.trim();
	}

	private static void withModClassLoader(Runnable action) {
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		ClassLoader modCl = BoundedOneAiService.class.getClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(modCl);
			action.run();
		} finally {
			Thread.currentThread().setContextClassLoader(previous);
		}
	}

	private void shutdownBlocking() {
		try {
			lifecycleExecutor.submit(() -> withModClassLoader(this::unloadInternal)).get(30, TimeUnit.SECONDS);
		} catch (Exception exception) {
			OutofboundMod.LOGGER.warn("Bounded One AI shutdown interrupted", exception);
			unloadInternal();
		}
	}

	private void closeModelUnlocked() {
		ExecutorService executor = inferenceExecutor;
		inferenceExecutor = null;
		if (executor != null) {
			executor.shutdownNow();
			try {
				if (!executor.awaitTermination(8, TimeUnit.SECONDS)) {
					OutofboundMod.LOGGER.warn("Bounded One AI inference executor did not stop in time");
				}
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
			}
		}

		LlamaModel loadedModel = model;
		model = null;
		if (loadedModel != null) {
			try {
				loadedModel.close();
			} catch (Throwable throwable) {
				OutofboundMod.LOGGER.warn("Failed to close Bounded One AI model", throwable);
			}
		}
	}
}
