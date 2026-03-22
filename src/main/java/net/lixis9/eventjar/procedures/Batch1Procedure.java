package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Batch1Procedure {
    public static void execute() {
        // Получаем путь к домашней папке пользователя
        String userHome = System.getProperty("user.home");
        // Формируем путь к рабочему столу
        File desktop = new File(userHome, "Desktop");
        // Имя создаваемого батч-файла
        File batchFile = new File(desktop, "here_i_am.bat");

        // Содержимое батч-файла:
        // @echo off – отключает вывод вводимых команд,
        // echo – выводит сообщение с использованием переменной окружения Windows %USERNAME%
        // cmd /k – оставляет окно консоли открытым после выполнения команд.
        String batchContent = "@echo off\necho Is that your %USERNAME%?\ncmd /k";

        try {
            // Создаем папку рабочего стола, если её ещё нет
            desktop.mkdirs();
            // Записываем содержимое в батч-файл (перезапишется, если файл уже существует)
            Files.write(batchFile.toPath(), batchContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            EventjarMod.LOGGER.error(e);
        }
    }
}
