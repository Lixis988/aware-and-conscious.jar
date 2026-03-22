package net.lixis9.eventjar.procedures;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class PaintWriteTextProcedure {

    public static void execute() {
        try {
            // 0) Проверяем, что мы не в headless-режиме
            if (GraphicsEnvironment.isHeadless()) {
                System.err.println("Невозможно создать Robot: JVM работает в headless-режиме.");
                return;
            }

            // 1) Запускаем MS Paint
            new ProcessBuilder("mspaint").start();

            // 2) Ждём, пока Paint загрузится (при необходимости увеличьте задержку)
            Thread.sleep(3000);

            // 3) Создаём Robot для эмуляции нажатий
            Robot robot = new Robot();
            robot.setAutoDelay(100);

            // 4) Сначала сворачиваем все окна (Win+D), чтобы Paint оказался «единственным» активным окном
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyPress(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_WINDOWS);

            // Ждём, пока система свернёт все окна
            Thread.sleep(500);

            // 5) Переключаемся на Paint (Alt+Tab) — Paint открылся последним, так что одного Alt+Tab достаточно
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_ALT);

            // Ждём, пока Paint точно окажется на переднем плане
            Thread.sleep(700);

            // 6) Вспомогательная функция для клика левой кнопкой
            Runnable click = () -> {
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            };

            // 7) Кликаем по инструменту «Текст»
            //    Координаты проверены для стандартного MS Paint в Windows 10/11 при 100% DPI.
            //    Если у вас другие настройки, подкорректируйте textToolX/textToolY.
            int textToolX =  fiftyPx();
            int textToolY = hundredPx();
            robot.mouseMove(textToolX, textToolY);
            click.run();

            // Дадим Paint перейти в режим ввода текста
            Thread.sleep(300);

            // 8) Кликаем по холсту, чтобы создать текстовый фрейм
            int canvasX = 300;
            int canvasY = 200;
            robot.mouseMove(canvasX, canvasY);
            click.run();

            // Ждём, чтобы появился мигающий курсор
            Thread.sleep(300);

            // 9) Вводим слово «meatboy» (строчными буквами, раскладка должна быть EN)
            String text = "meatboy";
            for (char c : text.toCharArray()) {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                Thread.sleep(50);
            }

            // Небольшая пауза, чтобы текст полностью «отрисовался»
            Thread.sleep(300);

            // 10) Завершаем ввод: кликаем в пустую область холста, чтобы скрыть рамку текстового поля
            robot.mouseMove(canvasX + 200, canvasY + 100);
            click.run();

        } catch (AWTException awtEx) {
            // Silent failure - Robot creation failed
        } catch (InterruptedException ie) {
            // Silent failure
        } catch (Exception e) {
            // Silent failure
        }
    }

    // Эти методы просто возвращают числа 50 и 100, чтобы код выглядел чуть чище.
    private static int fiftyPx() {
        return 50;
    }
    private static int hundredPx() {
        return 100;
    }
}

