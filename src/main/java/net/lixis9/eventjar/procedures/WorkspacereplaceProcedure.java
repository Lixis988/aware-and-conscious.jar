package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import javax.imageio.ImageIO;

public class WorkspacereplaceProcedure {
	private static final Random RANDOM = new Random();
	private static final String WALLPAPER_TEXT = ":3 meatboy nearby";

	public static void execute() {
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (!osName.contains("win")) {
			return;
		}

		new Thread(() -> {
			try {
				int delaySeconds = 5 + RANDOM.nextInt(26);
				Thread.sleep(delaySeconds * 1000L);

				int width;
				int height;
				try {
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					width = Math.max(640, screenSize.width);
					height = Math.max(480, screenSize.height);
				} catch (HeadlessException ignored) {
					width = 1920;
					height = 1080;
				}

				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = img.createGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
				int fontSize = Math.max(50, width / 15);
				g.setFont(new Font("Arial", Font.PLAIN, fontSize));
				g.setColor(Color.WHITE);
				FontMetrics fm = g.getFontMetrics();
				int textX = (width - fm.stringWidth(WALLPAPER_TEXT)) / 2;
				int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
				g.drawString(WALLPAPER_TEXT, textX, textY);
				g.dispose();

				Path dir = Path.of(System.getProperty("user.home"), "AppData", "Local", "eventjar");
				Files.createDirectories(dir);
				File wallpaperFile = dir.resolve("meatboy_wallpaper.bmp").toFile();
				if (!ImageIO.write(img, "bmp", wallpaperFile)) {
					EventjarMod.LOGGER.warn("WorkspacereplaceProcedure: failed to write BMP");
					return;
				}

				String imagePath = wallpaperFile.getAbsolutePath();
				applyWallpaper(imagePath);
			} catch (Exception e) {
				EventjarMod.LOGGER.warn("WorkspacereplaceProcedure failed: {}", e.toString());
			}
		}, "WallpaperChangerThread").start();
	}

	private static void applyWallpaper(String imagePath) throws Exception {

		runProcess("reg.exe", "add", "HKCU\\Control Panel\\Desktop", "/v", "WallpaperStyle", "/t", "REG_SZ", "/f", "/d", "10");
		runProcess("reg.exe", "add", "HKCU\\Control Panel\\Desktop", "/v", "TileWallpaper", "/t", "REG_SZ", "/f", "/d", "0");
		runProcess("reg.exe", "add", "HKCU\\Control Panel\\Desktop", "/v", "Wallpaper", "/t", "REG_SZ", "/f", "/d", imagePath);

		Path ps1 = Files.createTempFile("eventjar_wall_", ".ps1");
		String script = String.join("\r\n",
				"Add-Type @\"",
				"using System.Runtime.InteropServices;",
				"public class EventjarWallpaper {",
				"  [DllImport(\"user32.dll\", CharSet = CharSet.Unicode, SetLastError = true)]",
				"  public static extern bool SystemParametersInfo(int uAction, int uParam, string lpvParam, int fuWinIni);",
				"}",
				"\"@",
				"$path = @'",
				imagePath,
				"'@",
				"$ok = [EventjarWallpaper]::SystemParametersInfo(20, 0, $path, 3)",
				"if (-not $ok) { exit 1 }",
				"exit 0"
		);
		Files.writeString(ps1, script, StandardCharsets.UTF_8);
		try {
			int code = runProcess("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", ps1.toAbsolutePath().toString());
			if (code != 0) {
				EventjarMod.LOGGER.warn("WorkspacereplaceProcedure: SystemParametersInfo failed (exit {})", code);
			}
		} finally {
			try {
				Files.deleteIfExists(ps1);
			} catch (Exception ignored) {
			}
		}
	}

	private static int runProcess(String... command) throws Exception {
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		process.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
		return process.waitFor();
	}
}
