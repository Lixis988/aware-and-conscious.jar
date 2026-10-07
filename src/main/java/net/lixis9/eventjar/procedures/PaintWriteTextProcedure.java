package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public class PaintWriteTextProcedure {

	private static final String TEXT = "meatboy";

	public static void execute() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (!os.contains("win")) {
			return;
		}
		if (GraphicsEnvironment.isHeadless()) {
			EventjarMod.LOGGER.warn("PaintWriteTextProcedure: headless JVM");
			return;
		}

		try {
			int width = 800;
			int height = 500;
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 72));
			FontMetrics fm = g.getFontMetrics();
			int x = (width - fm.stringWidth(TEXT)) / 2;
			int y = (height - fm.getHeight()) / 2 + fm.getAscent();
			g.drawString(TEXT, x, y);
			g.dispose();

			Path dir = Path.of(System.getProperty("user.home"), "AppData", "Local", "eventjar");
			Files.createDirectories(dir);
			Path png = dir.resolve("meatboy_paint.png");
			ImageIO.write(img, "png", png.toFile());

			new ProcessBuilder("mspaint.exe", png.toAbsolutePath().toString()).start();
		} catch (Exception e) {
			EventjarMod.LOGGER.warn("PaintWriteTextProcedure failed: {}", e.toString());
		}
	}
}
