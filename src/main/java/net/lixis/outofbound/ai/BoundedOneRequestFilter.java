package net.lixis.outofbound.ai;

import java.util.Locale;

public final class BoundedOneRequestFilter {

	private static final String[] CODE_MARKERS = {
			"write code", "write me code", "write a code", "write some code", "give me code",
			"some code", "the code", "your code", "source code", "code for", "code that",
			"напиши код", "написать код", "код на", "программу", "программа", "скрипт",
			"функцию", "функция", "алгоритм",
			"python", "java ", "javascript", "kotlin", "c++", "c#", "html", "css", "sql",
			"def ", "print(", "public static", "system.out", "console.log", "import ",
			"function ", "algorithm", "compile", "snippet", "script", "pseudocode",
	};

	private BoundedOneRequestFilter() {
	}

	public static boolean isCodeRequest(String message) {
		if (message == null || message.isBlank()) {
			return false;
		}
		String lower = message.toLowerCase(Locale.ROOT);
		for (String marker : CODE_MARKERS) {
			if (lower.contains(marker)) {
				return true;
			}
		}
		return false;
	}
}
