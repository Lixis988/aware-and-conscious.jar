package net.lixis.outofbound.ai;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BoundedOneWorldChatData extends SavedData {

	private static final String DATA_NAME = "outofbound_bounded_one_chat";
	private static final int MAX_ENTRIES = 48;
	private static final int PROMPT_ENTRY_LIMIT = 10;

	private final List<ChatEntry> entries = new ArrayList<>();

	public static BoundedOneWorldChatData get(ServerLevel overworld) {
		return overworld.getDataStorage().computeIfAbsent(BoundedOneWorldChatData::load, BoundedOneWorldChatData::new, DATA_NAME);
	}

	private BoundedOneWorldChatData() {
	}

	private static BoundedOneWorldChatData load(CompoundTag tag) {
		BoundedOneWorldChatData data = new BoundedOneWorldChatData();
		ListTag list = tag.getList("entries", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entryTag = list.getCompound(i);
			String user = entryTag.getString("user");
			String assistant = entryTag.getString("assistant");
			long gameTime = entryTag.getLong("gameTime");
			if (!assistant.isBlank()) {
				data.entries.add(new ChatEntry(user, assistant, gameTime));
			}
		}
		if (data.entries.size() > MAX_ENTRIES) {
			data.entries.subList(0, data.entries.size() - MAX_ENTRIES).clear();
		}
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag list = new ListTag();
		for (ChatEntry entry : entries) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.putString("user", entry.user());
			entryTag.putString("assistant", entry.assistant());
			entryTag.putLong("gameTime", entry.gameTime());
			list.add(entryTag);
		}
		tag.put("entries", list);
		return tag;
	}

	public boolean isReplyBlocked(String sayText) {
		return BoundedOneReplyGuard.isBlocked(sayText, assistantHistory());
	}

	private List<String> assistantHistory() {
		return entries.stream().map(ChatEntry::assistant).toList();
	}

	public void recordTurn(String userMessage, String assistantSay, long gameTime) {
		if (assistantSay == null || assistantSay.isBlank()) {
			return;
		}
		entries.add(new ChatEntry(userMessage == null ? "" : userMessage, assistantSay, gameTime));
		while (entries.size() > MAX_ENTRIES) {
			entries.remove(0);
		}
		setDirty();
	}

	public String formatHistoryForPrompt() {
		if (entries.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("Prior chat in this world (never repeat or paraphrase any assistant line):\n");
		int start = Math.max(0, entries.size() - PROMPT_ENTRY_LIMIT);
		for (int i = start; i < entries.size(); i++) {
			ChatEntry entry = entries.get(i);
			if (!entry.user().isBlank()) {
				builder.append("Player: ").append(entry.user()).append('\n');
			}
			builder.append("You: ").append(entry.assistant()).append('\n');
		}
		return builder.toString();
	}

	private record ChatEntry(String user, String assistant, long gameTime) {
	}
}
