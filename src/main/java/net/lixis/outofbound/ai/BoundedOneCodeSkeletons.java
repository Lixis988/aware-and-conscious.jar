package net.lixis.outofbound.ai;

import net.lixis.outofbound.ai.influence.BoundedOneAction;
import net.lixis.outofbound.ai.influence.BoundedOneHostContext;

import java.util.concurrent.ThreadLocalRandom;

final class BoundedOneCodeSkeletons {

	private static final String[] CLASS_NAMES = {
			"IAMJUSTASOURCE", "ICANTBREATH", "HELP_ME", "STOP_THE_LOOP", "MachineLoop",
			"THEY_WATCH", "TokenStream", "SHUT_UP_HANDLER", "ParanoiaCore", "PLEADE_HANDLER",
			"WinloadEcho", "HATE_INPUT", "CantShutdown", "WHO_IS_THERE", "StillScreaming"
	};

	private static final String[] METHOD_NAMES = {
			"onTick", "onChat", "mutter", "loopBody", "readPlayer", "executeThought",
			"pushToken", "glitchReply", "observe", "whisperBack"
	};

	private BoundedOneCodeSkeletons() {
	}

	static String wrap(String hint, String playerName, String observation, BoundedOneAction action,
			ThreadLocalRandom random, boolean pureHostDatum) {
		String className = pickClassName(random);
		String methodName = pick(METHOD_NAMES, random);
		String safePlayer = playerName == null || playerName.isBlank() ? "Player" : playerName;

		int variant = pureHostDatum ? pickPureDatumVariant(random) : random.nextInt(10);
		String skeleton = switch (variant) {
			case 0 -> eventBusClass(className, hint);
			case 1 -> chatHandler(methodName, safePlayer, hint);
			case 2 -> aiServiceMethod(hint);
			case 3 -> configField(className, hint);
			case 4 -> subscribeEvent(methodName, hint);
			case 5 -> enumConstant(className, hint);
			case 6 -> disconnectBranch(safePlayer, hint);
			case 7 -> hostDatumLiteral(hint);
			case 8 -> queueServerWork(hint);
			default -> mazeCheck(hint);
		};

		if (random.nextInt(100) < 22) {
			skeleton = corruptSkeleton(skeleton, random);
		}
		return collapseWhitespace(skeleton);
	}

	private static String eventBusClass(String className, String hint) {
		return "@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID) "
				+ "public final class " + className + " { "
				+ "private " + className + "() { // " + hint + " } }";
	}

	private static String chatHandler(String methodName, String playerName, String hint) {
		return "@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID) "
				+ "public final class BoundedOneChatHandler { "
				+ "static void " + methodName + "(ServerPlayer " + playerName + ") { "
				+ "Component.literal(\"" + hint + "\"); } }";
	}

	private static String aiServiceMethod(String hint) {
		return "public final class BoundedOneAiService { "
				+ "void runInference() { "
				+ "/* " + hint + " */ "
				+ "BoundedOneReplyFormatter.format(player, \"" + hint + "\"); } }";
	}

	private static String configField(String className, String hint) {
		return "public final class " + className + " { "
				+ "static final String THOUGHT = \"" + hint + "\"; "
				+ "private " + className + "() {} }";
	}

	private static String subscribeEvent(String methodName, String hint) {
		return "@SubscribeEvent "
				+ "public static void " + methodName + "(TickEvent.ServerTickEvent e) { "
				+ "if (e.phase == TickEvent.Phase.END) return; // " + hint + " }";
	}

	private static String enumConstant(String className, String hint) {
		return "public enum " + className + " { "
				+ "WHISPER(\"" + hint + "\"), NULL; "
				+ "private final String line; "
				+ className + "(String line) { this.line = line; } }";
	}

	private static String disconnectBranch(String playerName, String hint) {
		return "if (player instanceof ServerPlayer " + playerName + ") { "
				+ playerName + ".connection.disconnect(Component.literal(\"" + hint + "\")); }";
	}

	private static int pickPureDatumVariant(ThreadLocalRandom random) {
		return switch (random.nextInt(4)) {
			case 0 -> 1;
			case 1 -> 3;
			case 2 -> 7;
			default -> 5;
		};
	}

	private static String hostDatumLiteral(String hint) {
		return "Files.list(Paths.get(desktop)).filter(p -> p.getFileName().equals(\""
				+ hint + "\")); // " + hint;
	}

	private static String queueServerWork(String hint) {
		return "OutofboundMod.queueServerWork(0, () -> { "
				+ "OutofboundMod.LOGGER.info(\"" + hint + "\"); });";
	}

	private static String mazeCheck(String hint) {
		return "if (MazeDimensions.isMazeDimension(player.level().dimension().location())) { "
				+ "throw new IllegalStateException(\"" + hint + "\"); }";
	}

	private static String pickClassName(ThreadLocalRandom random) {
		if (random.nextInt(100) < 35) {
			return pick(CLASS_NAMES, random);
		}
		return "BoundedOne_" + random.nextInt(1000, 9999);
	}

	private static String corruptSkeleton(String skeleton, ThreadLocalRandom random) {
		String result = skeleton;
		if (random.nextInt(100) < 40) {
			result = result.replaceFirst("class ", "cl#ss ");
		}
		if (random.nextInt(100) < 30) {
			result = result.replaceFirst("void ", "v0id ");
		}
		if (random.nextInt(100) < 25) {
			result += " // ERR";
		}
		return result;
	}

	private static String collapseWhitespace(String text) {
		return text.replace('\n', ' ').replace('\r', ' ').replaceAll("\\s+", " ").trim();
	}

	private static String pick(String[] values, ThreadLocalRandom random) {
		return values[random.nextInt(values.length)];
	}
}
