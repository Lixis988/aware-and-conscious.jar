package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AddresourspackProcedure {

	public static void execute() {
		copyPack("/assets/eventjar/resourses/you.zip", "you.zip");
	}

	static void copyPack(String resourcePath, String fileName) {
		Path gameDir = FMLPaths.GAMEDIR.get();
		Path resourcePacksDir = gameDir.resolve("resourcepacks");
		try {
			Files.createDirectories(resourcePacksDir);
		} catch (IOException e) {
			EventjarMod.LOGGER.warn("Cannot create resourcepacks dir: {}", e.toString());
			return;
		}

		try (InputStream in = AddresourspackProcedure.class.getResourceAsStream(resourcePath)) {
			if (in == null) {
				EventjarMod.LOGGER.warn("Missing pack resource {}", resourcePath);
				return;
			}
			Path targetFile = resourcePacksDir.resolve(fileName);
			if (!Files.exists(targetFile)) {
				Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
				EventjarMod.LOGGER.info("Installed resource pack {}", fileName);
			}
		} catch (IOException e) {
			EventjarMod.LOGGER.warn("Failed copying {}: {}", fileName, e.toString());
		}
	}
}
