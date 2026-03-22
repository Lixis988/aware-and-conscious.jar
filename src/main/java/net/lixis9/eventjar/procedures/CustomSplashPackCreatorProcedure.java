package net.lixis9.eventjar.procedures;

import net.minecraft.client.Minecraft;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class CustomSplashPackCreatorProcedure {
    public static File createCustomSplashPack() {
        Minecraft mc = Minecraft.getInstance();
        // Используем gameDirectory (в новых версиях) или runDirectory для более старых
        File gameDir = mc.gameDirectory;
        File packFolder = new File(gameDir, "resourcepacks/Null");
        if (!packFolder.exists()) {
            packFolder.mkdirs();
            try {
                // Создаем pack.mcmeta
                File packMcmeta = new File(packFolder, "pack.mcmeta");
                String mcmetaContent = "{\n" +
                        "  \"pack\": {\n" +
                        "    \"pack_format\": 6,\n" +
                        "    \"description\": \"err=Null\"\n" +
                        "  }\n" +
                        "}";
                Files.write(packMcmeta.toPath(), mcmetaContent.getBytes(), StandardOpenOption.CREATE);
                
                // Создаем директорию assets/minecraft/texts
                File textsFolder = new File(packFolder, "assets/minecraft/texts");
                textsFolder.mkdirs();
                
                // Создаем файл splashes.txt с вашим сплеш‑текстом
                File splashes = new File(textsFolder, "splashes.txt");
                Files.write(splashes.toPath(), "meetboy nearby".getBytes(), StandardOpenOption.CREATE);
                
                // CustomSplashPack created silently
            } catch (IOException e) {
                // Silent failure
            }
        }
        return packFolder;
    }
}
