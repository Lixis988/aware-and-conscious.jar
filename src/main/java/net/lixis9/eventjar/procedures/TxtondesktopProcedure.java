package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mod.EventBusSubscriber
public class TxtondesktopProcedure {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        execute(event);
    }

    public static void execute() {
        execute(null);
    }

    private static void execute(@Nullable Event event) {
        // Формирование пути к файлу на рабочем столе
        String userHome = System.getProperty("user.home");
        File desktopFile = new File(userHome + File.separator + "Desktop", "hello.txt");

        try {
            // Создаем родительские директории (если их нет) и новый файл
            desktopFile.getParentFile().mkdirs();
            desktopFile.createNewFile();

            // Создаем BufferedWriter для записи в файл
            BufferedWriter writer = new BufferedWriter(new FileWriter(desktopFile));
            writer.write("Hi, I'm meetboy :3333, did you miss me? Anyway, your minecraft is here:");
            writer.newLine();
            writer.write(Minecraft.getInstance().gameDirectory.getAbsolutePath());
            writer.newLine();
            writer.write("sorry i accidentally deleted a couple of files there :33333");
            writer.newLine();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
