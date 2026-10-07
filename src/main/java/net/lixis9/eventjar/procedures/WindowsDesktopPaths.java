package net.lixis9.eventjar.procedures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public final class WindowsDesktopPaths {

	private WindowsDesktopPaths() {
	}

	public static Path resolveDesktop() {
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"powershell.exe", "-NoProfile", "-Command",
					"[Environment]::GetFolderPath('Desktop')");
			pb.redirectErrorStream(true);
			Process process = pb.start();
			String line;
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
				line = reader.readLine();
			}
			process.waitFor(5, TimeUnit.SECONDS);
			if (line != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					Path path = Path.of(line);
					if (Files.isDirectory(path)) {
						return path;
					}
				}
			}
		} catch (Exception ignored) {
		}

		Path homeDesktop = Path.of(System.getProperty("user.home"), "Desktop");
		if (Files.isDirectory(homeDesktop)) {
			return homeDesktop;
		}
		Path oneDrive = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop");
		if (Files.isDirectory(oneDrive)) {
			return oneDrive;
		}
		try {
			Files.createDirectories(homeDesktop);
		} catch (Exception ignored) {
		}
		return homeDesktop;
	}

	public static int runProcess(String... command) throws Exception {
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		process.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
		return process.waitFor();
	}

	public static int runPowerShellFile(Path script) throws Exception {
		return runProcess(
				"powershell.exe",
				"-NoProfile",
				"-ExecutionPolicy", "Bypass",
				"-File", script.toAbsolutePath().toString());
	}
}
