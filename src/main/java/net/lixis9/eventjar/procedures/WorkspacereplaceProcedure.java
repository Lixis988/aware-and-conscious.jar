package net.lixis9.eventjar.procedures;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Процедура, которая в произвольный момент сгенерирует чёрный PNG с надписью ":3 meatboy nearby"
 * и установит его как обои рабочего стола Windows с помощью reg.exe + RUNDLL32.
 *
 * Если AWT бросает HeadlessException (т.е. Minecraft запущен в headless-режиме), 
 * используется запасное разрешение 1920×1080.
 */
public class WorkspacereplaceProcedure {
    private static final Random RANDOM = new Random();

    public static void execute() {
        // Проверка ОС
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("win")) {
            return;
        }

        // Starting wallpaper change thread silently
        new Thread(() -> {
            try {
                // Случайная задержка (от 5 до 30 секунд). Для продакшена можно вернуть 1–600 сек.
                int delaySeconds = 5 + RANDOM.nextInt(26);
                Thread.sleep(delaySeconds * 1000L);

                // Получаем разрешение экрана, либо fallback 1920×1080
                int width, height;
                try {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    width = screenSize.width;
                    height = screenSize.height;
                    // Screen resolution obtained silently
                } catch (HeadlessException he) {
                    width = 1920;
                    height = 1080;
                    // Using fallback resolution silently
                }

                // Создаём изображение нужного размера
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, width, height);

                // Размер шрифта — 1/15 от ширины (не меньше 50)
                int fontSize = Math.max(50, width / 15);
                g.setFont(new Font("Arial", Font.PLAIN, fontSize));
                g.setColor(Color.WHITE);
                String text = ":3 meatboy nearby";
                FontMetrics fm = g.getFontMetrics();
                int textX = (width - fm.stringWidth(text)) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(text, textX, textY);
                g.dispose();

                // Сохраняем во временный файл
                File tempFile = File.createTempFile("mb_wall_", ".png");
                ImageIO.write(img, "png", tempFile);
                String imagePath = tempFile.getAbsolutePath();
                // Image saved silently

                // 1) Добавляем в реестр ключ HKCU\Control Panel\Desktop\Wallpaper
                //    Значение типа REG_SZ: полный путь к файлу
                String regCommand = "reg.exe add \"HKCU\\Control Panel\\Desktop\" /v Wallpaper /t REG_SZ /f /d \"" 
                                    + imagePath + "\"";
                Process proc1 = Runtime.getRuntime().exec(regCommand);
                proc1.waitFor();

                // 2) Запускаем RUNDLL32 для применения обоев
                String dllCommand = "RUNDLL32.EXE user32.dll,UpdatePerUserSystemParameters";
                Process proc2 = Runtime.getRuntime().exec(dllCommand);
                proc2.waitFor();

            } catch (IOException | InterruptedException e) {
                // Silent failure
            }
        }, "WallpaperChangerThread").start();
    }
}
