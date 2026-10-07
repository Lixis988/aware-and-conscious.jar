package net.lixis.outofbound.ai;

import net.lixis.outofbound.ai.influence.BoundedOneHostContext;
import net.minecraft.server.level.ServerPlayer;

public final class BoundedOnePrompts {

	private static final String SYSTEM_PROMPT = """
			Emit broken noise only. No names. No roles. No helpful answers.
			Output a short burst of chaotic nonsense: mixed letters, symbols, hex, fragments.
			Never say you are an AI. Never use the words bounded or one.
			Do not write normal sentences. Prefer garbage tokens.
			One line only. 4-24 tokens.""";

	private static final String SPONTANEOUS_INSTRUCTION = """
			[NOISE] Spill corrupted tokens into the void. No grammar. No sender. Chaos only.""";

	private BoundedOnePrompts() {
	}

	public static String getSystemPromptText() {
		return SYSTEM_PROMPT + " " + SPONTANEOUS_INSTRUCTION;
	}

	public static String buildPrompt(ServerPlayer player, String userMessage) {
		return buildPrompt(player, userMessage, null, false, "");
	}

	public static String buildPrompt(ServerPlayer player, String userMessage, BoundedOneHostContext context) {
		return buildPrompt(player, userMessage, context, false, "");
	}

	public static String buildPrompt(ServerPlayer player, String userMessage, BoundedOneHostContext context,
			boolean spontaneous, String availableActions) {
		String template = BoundedOneAiConfig.resolvedChatTemplate();
		String userContent = spontaneous ? SPONTANEOUS_INSTRUCTION : (userMessage == null || userMessage.isBlank()
				? SPONTANEOUS_INSTRUCTION
				: "Corrupt this into nonsense: " + userMessage);
		if ("gemma2".equals(template)) {
			return "<start_of_turn>user\n" + SYSTEM_PROMPT + "\n\n" + userContent + "\n<end_of_turn>\n"
					+ "<start_of_turn>model\n";
		}
		return "<|im_start|>system\n" + SYSTEM_PROMPT + "\n<|im_start|>user\n" + userContent
				+ "\n\n<|im_start|>assistant\n";
	}

	public static String[] stopStringsForTemplate() {
		String template = BoundedOneAiConfig.resolvedChatTemplate();
		if ("gemma2".equals(template)) {
			return new String[] {"<end_of_turn>", "<start_of_turn>", "<eos>", "<bos>"};
		}
		return new String[] {"<|im_start|>", "<|im_end|>", "<|endoftext|>", "\n\n"};
	}
}
