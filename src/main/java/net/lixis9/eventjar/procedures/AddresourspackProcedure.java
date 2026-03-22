package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.loading.FMLPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;
import java.io.IOException;

public class AddresourspackProcedure {

    public static void execute() {
        // Получаем директорию игры
        Path gameDir = FMLPaths.GAMEDIR.get();
        // Определяем директорию с ресурспаками
        Path resourcePacksDir = gameDir.resolve("resourcepacks");
        try {
            if (!Files.exists(resourcePacksDir)) {
                Files.createDirectories(resourcePacksDir);
            }
        } catch (IOException e) {
            return;
        }
        
        // Путь до ресурспака внутри jar (убедитесь, что путь и имя файла указаны правильно)
        String resourcePath = "/assets/eventjar/resourses/you.zip";
        
        try (InputStream in = AddresourspackProcedure.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return;
            }
            // Определяем путь для копирования в папке ресурспаков
            Path targetFile = resourcePacksDir.resolve("you.zip");
            
            // Копируем файл, если его ещё нет
            if (!Files.exists(targetFile)) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
        } catch (IOException e) {
            // Silent failure
        }
    }
}
