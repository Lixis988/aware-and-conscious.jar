package net.lixis9.eventjar.procedures;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class CalculatorFindProcedure {
    /** 
     * Запускает calc.exe и вводит "666" 
     */
    public static void execute() {
        try {
            // Запускаем Windows-калькулятор
            Runtime.getRuntime().exec("calc.exe");
            // Ждём, пока окно откроется
            Thread.sleep(500);

            // Инициализируем Robot для нажатий клавиш
            Robot robot = new Robot();
            typeKey(robot, KeyEvent.VK_6);
            typeKey(robot, KeyEvent.VK_6);
            typeKey(robot, KeyEvent.VK_6);

        } catch (IOException e) {
            // Silent failure
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Silent failure
        } catch (AWTException e) {
            // Silent failure
        }
    }

    private static void typeKey(Robot robot, int keycode) {
        robot.keyPress(keycode);
        robot.keyRelease(keycode);
        robot.delay(100);
    }
}
