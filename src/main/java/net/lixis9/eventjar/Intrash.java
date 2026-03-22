package net.lixis9.eventjar;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Intrash {
    public Intrash() {
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        // Получаем путь до корня директории игры
        Path gameDir = FMLPaths.GAMEDIR.get();

        // Формируем полный путь до die.bat внутри директории игры
        Path dieBat = gameDir.resolve("die.bat");

        // Содержимое .bat-файла
        String line1 = "@echo off";
        String line2 = "echo Your PC will die in 60 seconds...";
        String line3 = "timeout /t 60 /nobreak >nul";
        String line4 = "echo It\'s just a joke";
        String line5 = "pause";

        try {
            // Записываем все строки в die.bat (UTF-8)
            Files.write(dieBat, java.util.Arrays.asList(line1, line2, line3, line4, line5), StandardCharsets.UTF_8);
            // die.bat file created silently
        } catch (IOException e) {
            // Silent failure
        }
    }
}
