package net.lixis9.eventjar;

public final class Authorship {

	public static final String NOTICE =
			"Mod created by Lixis9 (Lev Glebovich Belyakov).\n"
					+ "If this mod is posted under a different name, it has been stolen.";

	public static final String AUTHOR = "Lixis9 (Lev Glebovich Belyakov)";
	public static final String SHORT = "Lixis9";

	private Authorship() {
	}

	public static String notice() {
		return NOTICE;
	}
}
