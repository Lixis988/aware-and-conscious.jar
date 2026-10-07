package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MeetcraftProcedure {

	@SuppressWarnings("unused")
	private static final String OWNERSHIP = net.lixis9.eventjar.Authorship.NOTICE;

	private static final String[] BUNDLE = {
			"meat-alpha.jar",
			"run.bat"
	};

	public static void execute() {
		try {
			Path dir = Paths.get(System.getProperty("java.io.tmpdir"), "eventjar_meat_alpha");
			Files.createDirectories(dir);

			Path jar = extractBundle(dir);
			if (jar == null || !Files.isRegularFile(jar)) {
				EventjarMod.LOGGER.warn("meat_alpha/meat-alpha.jar missing from mod resources — build meat-alpha first");
				return;
			}

			String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java.exe").toString();
			if (!Files.isRegularFile(Paths.get(javaBin))) {
				javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
			}

			List<String> cmd = new ArrayList<>();
			cmd.add(javaBin);
			cmd.add("-Xmx1G");
			cmd.add("-jar");
			cmd.add(jar.toAbsolutePath().toString());

			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.directory(dir.toFile());
			pb.redirectErrorStream(true);
			Process process = pb.start();
			boolean finished = process.waitFor(30, TimeUnit.MINUTES);
			if (!finished) {
				process.destroyForcibly();
			}
		} catch (Exception e) {
			EventjarMod.LOGGER.warn("MeetcraftProcedure / MEATCRAFT ALPHA failed: {}", e.toString());
		}
	}

	private static Path extractBundle(Path dir) throws Exception {
		Path jarOut = null;
		for (String relative : BUNDLE) {
			String resource = "/meat_alpha/" + relative;
			try (InputStream in = MeetcraftProcedure.class.getResourceAsStream(resource)) {
				if (in == null) {
					if (relative.endsWith(".jar")) {
						return null;
					}
					continue;
				}
				Path target = dir.resolve(relative);
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
				if (relative.endsWith(".jar")) {
					jarOut = target;
				}
			}
		}
		return jarOut;
	}
}
