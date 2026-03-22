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
public class BatchProcedure {
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
        File desktopFile = new File(userHome + File.separator + "Desktop", "here_i_am.bat");

        try {
            // Создаем родительские директории (если их нет) и новый файл
            desktopFile.getParentFile().mkdirs();
            desktopFile.createNewFile();

            // Создаем BufferedWriter для записи в файл
            BufferedWriter writer = new BufferedWriter(new FileWriter(desktopFile));
            writer.write("@Echo off");
            writer.newLine();
            writer.write("Echo Your name is %USERNAME%?");
            writer.newLine();
            writer.write("pause");
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
