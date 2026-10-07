package net.lixis.outofbound.client;

import net.lixis9.eventjar.DarknessConfig;
import net.lixis.outofbound.OutofboundMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public final class LiminalGameLauncher {

	private static final AtomicBoolean MEAT_LAUNCHING = new AtomicBoolean(false);

	private static final String[] SERAPH_FILES = {
			"liminal.exe",
			"glew32.dll",
			"glfw3.dll",
			"audio/noise.mp3",
			"assets/eye.gif"
	};

	private static final String[] MEAT_FILES = {
			"meat_liminal.exe",
			"glew32.dll",
			"glfw3.dll",
			"audio/noise.mp3",
			"audio/hammer.ogg",
			"audio/hammer_hit.ogg",
			"assets/meat.png",
			"assets/bunny.png",
			"assets/hammer.png",
			"assets/particles/p0.png",
			"assets/particles/p1.png",
			"assets/particles/p2.png",
			"assets/particles/p3.png",
			"assets/particles/p4.png",
			"assets/particles/p5.png",
			"assets/particles/p6.png",
			"assets/particles/p7.png"
	};

	private LiminalGameLauncher() {
	}

	private static final String SERAPH_FALLBACK =
			"E:/outofbound/game/cpp_liminal/build/bin/Release/liminal.exe";
	private static final String MEAT_FALLBACK =
			"E:/outofbound/game/cpp_liminal_meat/build/bin/Release/meat_liminal.exe";

	public static void launchAndExit() {
		new Thread(() -> {
			try {
				Path exe = extractBundle("outofbound_liminal", "/liminal/", "liminal.exe", SERAPH_FILES);
				if (exe == null) {
					exe = resolveExisting(DarknessConfig.LIMINAL_EXE_PATH, SERAPH_FALLBACK);
				}
				if (exe != null && Files.isRegularFile(exe)) {
					new ProcessBuilder(exe.toAbsolutePath().toString())
							.directory(exe.getParent().toFile())
							.start();
				}
			} catch (Exception ignored) {
			}
			try {
				Thread.sleep(500L);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
			Runtime.getRuntime().halt(0);
		}, "outofbound-liminal-launch").start();
	}

	public static void launchMeatHunt() {
		if (!MEAT_LAUNCHING.compareAndSet(false, true)) {
			return;
		}
		new Thread(() -> {
			try {
				Path exe = extractBundle("outofbound_liminal_meat", "/liminal_meat/", "meat_liminal.exe", MEAT_FILES);
				if (exe == null) {

					exe = resolveExisting(null, MEAT_FALLBACK);
				}
				if (exe == null || !Files.isRegularFile(exe)) {
					OutofboundMod.LOGGER.warn("[outofbound] meat_liminal.exe missing");
					return;
				}
				new ProcessBuilder(exe.toAbsolutePath().toString())
						.directory(exe.getParent().toFile())
						.start()
						.waitFor();
			} catch (Exception exception) {
				OutofboundMod.LOGGER.warn("[outofbound] Meat liminal launch failed", exception);
			} finally {
				MEAT_LAUNCHING.set(false);
			}
		}, "outofbound-meat-liminal").start();
	}

	private static Path extractBundle(String tempDirName, String resourceRoot, String exeName, String[] files) {
		try {
			Path dir = Paths.get(System.getProperty("java.io.tmpdir"), tempDirName);
			Files.createDirectories(dir);
			Path exePath = null;
			for (String relative : files) {
				Path target = dir.resolve(relative);
				if (target.getParent() != null) {
					Files.createDirectories(target.getParent());
				}
				try (InputStream in = LiminalGameLauncher.class.getResourceAsStream(resourceRoot + relative)) {
					if (in == null) {
						OutofboundMod.LOGGER.warn("[outofbound] Missing {}{}", resourceRoot, relative);
						if (relative.equals(exeName)) {
							return null;
						}
						continue;
					}
					Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
				}
				if (relative.equals(exeName)) {
					exePath = target;
				}
			}
			return exePath;
		} catch (Exception exception) {
			OutofboundMod.LOGGER.warn("[outofbound] Failed to extract {}", resourceRoot, exception);
			return null;
		}
	}

	private static Path resolveExisting(String preferred, String fallbackAbsolute) {
		if (preferred != null && !preferred.isBlank()) {
			Path configuredPath = Paths.get(preferred.trim());
			if (Files.isRegularFile(configuredPath)) {
				return configuredPath;
			}
		}
		Path fallback = Paths.get(fallbackAbsolute);
		return Files.isRegularFile(fallback) ? fallback : null;
	}
}
