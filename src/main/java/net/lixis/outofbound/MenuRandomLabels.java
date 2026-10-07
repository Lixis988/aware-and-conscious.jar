package net.lixis.outofbound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public final class MenuRandomLabels {

	private static final String CHARS =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*?_-+=/\\|<>[]{}~";

	private MenuRandomLabels() {
	}

	public static String next() {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		int length = 5 + rnd.nextInt(10);
		StringBuilder builder = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			builder.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
		}
		return builder.toString();
	}
}
