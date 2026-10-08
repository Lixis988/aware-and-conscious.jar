package net.lixis.outofbound.world;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MeatGameMode {

	private static final String PENDING_FILE = "eventjar_meat_pending";
	private static boolean pendingCreate;

	private MeatGameMode() {
	}

	public static void setPendingCreate(boolean pending) {
		pendingCreate = pending;
		Path path = pendingPath();
		try {
			if (pending) {
				Files.writeString(path, "1", StandardCharsets.UTF_8);
			} else {
				Files.deleteIfExists(path);
			}
		} catch (Exception ignored) {
		}
	}

	public static boolean isPendingCreate() {
		return pendingCreate || Files.isRegularFile(pendingPath());
	}

	public static boolean consumePendingCreate() {
		boolean pending = isPendingCreate();
		pendingCreate = false;
		try {
			Files.deleteIfExists(pendingPath());
		} catch (Exception ignored) {
		}
		return pending;
	}

	public static boolean isActive(MinecraftServer server) {
		return server != null && WorldInternalConfig.isMeatGameMode(server);
	}

	private static Path pendingPath() {
		return FMLPaths.GAMEDIR.get().resolve(PENDING_FILE);
	}
}
