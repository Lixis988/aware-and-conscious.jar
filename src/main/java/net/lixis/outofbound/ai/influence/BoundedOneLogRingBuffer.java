package net.lixis.outofbound.ai.influence;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class BoundedOneLogRingBuffer {

	private static final Deque<String> LINES = new ArrayDeque<>();
	private static final int MAX_LINES = 64;

	private BoundedOneLogRingBuffer() {
	}

	public static synchronized void append(String line) {
		if (line == null || line.isBlank()) {
			return;
		}
		String trimmed = line.trim();
		if (trimmed.length() > 240) {
			trimmed = trimmed.substring(0, 240);
		}
		LINES.addLast(trimmed);
		while (LINES.size() > MAX_LINES) {
			LINES.removeFirst();
		}
	}

	public static synchronized List<String> tail(int count) {
		int limit = Math.max(0, count);
		List<String> result = new ArrayList<>(limit);
		for (String line : LINES) {
			result.add(line);
		}
		if (result.size() <= limit) {
			return result;
		}
		return result.subList(result.size() - limit, result.size());
	}
}
