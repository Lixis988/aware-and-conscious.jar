package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Batch1Procedure {

	private static final String CONTENT = "@echo off\r\necho Is that your %USERNAME%?\r\ncmd /k\r\n";

	public static void execute() {
		try {
			Path desktop = WindowsDesktopPaths.resolveDesktop();
			Files.createDirectories(desktop);
			Path bat = desktop.resolve("here_i_am.bat");
			Files.writeString(bat, CONTENT, StandardCharsets.UTF_8);
		} catch (Exception e) {
			EventjarMod.LOGGER.warn("Batch1Procedure failed: {}", e.toString());
		}
	}
}
