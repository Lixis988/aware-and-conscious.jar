package net.lixis.outofbound.ai;

import net.lixis.outofbound.OutofboundMod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class BoundedOneModelExtractor {

	public static final String MODEL_FILE_NAME = "SmolLM2-135M-Instruct-Q2_K.gguf";
	private static final String RESOURCE_PATH = "/assets/outofbound/model/" + MODEL_FILE_NAME;

	private BoundedOneModelExtractor() {
	}

	public static Path getModelsDirectory() {
		return Paths.get("config", OutofboundMod.MODID, "models");
	}

	public static Path getDefaultModelPath() {
		return getModelsDirectory().resolve(MODEL_FILE_NAME);
	}

	public static Path resolveModelPath() throws IOException {
		String configured = BoundedOneAiConfig.modelFile == null ? "" : BoundedOneAiConfig.modelFile.trim();
		if (configured.isEmpty()) {
			return ensureDefaultModelOnDisk();
		}

		Path path = Paths.get(configured);
		if (!path.isAbsolute()) {
			path = getModelsDirectory().resolve(configured).normalize();
		} else {
			path = path.normalize();
		}

		if (!Files.exists(path) || Files.size(path) <= 0L) {
			throw new IOException("Configured Bounded One AI model not found or empty: " + path.toAbsolutePath());
		}
		return path;
	}

	public static Path ensureModelOnDisk() throws IOException {
		return resolveModelPath();
	}

	public static Path ensureDefaultModelOnDisk() throws IOException {
		Path target = getDefaultModelPath();
		Files.createDirectories(target.getParent());

		if (Files.exists(target) && Files.size(target) > 0L) {
			OutofboundMod.LOGGER.info("Bounded One AI model already present at {}", target.toAbsolutePath());
			return target;
		}

		try (InputStream input = BoundedOneModelExtractor.class.getResourceAsStream(RESOURCE_PATH)) {
			if (input == null) {
				throw new IOException("Embedded model not found in mod resources: " + RESOURCE_PATH);
			}

			OutofboundMod.LOGGER.info("Extracting compressed Bounded One AI model (SmolLM2-135M Q2_K) to {}", target.toAbsolutePath());
			Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
		}

		return target;
	}
}
