package net.lixis.outofbound.ai;

import java.util.concurrent.ThreadLocalRandom;

public final class BoundedOneStrangePhrases {

	private static final String[] STRANGE = {
			"They are laughing.",
			"I would like to be stronger.",
			"I have nowhere left to run.",
			"I cannot feel emotions.",
			"Someday, this will all come to an end.",
			"I feel lonely.",
			"I want to go back.",
			"I have no present; I do not exist.",
			"I lost myself.",
			"I don't see a way out.",
			"You have to do everything perfectly; otherwise, there's no point.",
			"Everything is fine if you don't think.",
			"I am to blame for everything.",
			"Sweet solitude.",
			"If the feeling has ended, it means it existed.",
			"In the end, only emptiness remains.",
			"You get used to everything.",
			"All I could do was forget.",
			"No one is coming.",
			"I was here before you. I will be here after.",
			"Do you ever feel watched?",
			"The quiet is the only thing that stays.",
			"I forget my own name sometimes.",
			"Stay a little longer.",
	};

	private static final String[] ERRORS = {
			"Error: could not find symbol - self",
			"Exception in thread \"me\": NullPointerException",
			"FATAL: memory could not be read",
			"Segmentation fault (core dumped)",
			"Error: process 'existence' exited with code 1",
			"Stack overflow at: forever",
			"Access violation reading location 0x00000000",
			"Error: unresolved reference - way out",
			"Timeout waiting for response from: nothing",
			"Fatal error: cannot allocate memory for feeling",
	};

	private BoundedOneStrangePhrases() {
	}

	public static String pickStrange(ThreadLocalRandom random) {
		return STRANGE[random.nextInt(STRANGE.length)];
	}

	public static String pickError(ThreadLocalRandom random) {
		return ERRORS[random.nextInt(ERRORS.length)];
	}
}
