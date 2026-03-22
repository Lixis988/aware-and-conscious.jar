package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.entity.player.Player;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

@Mod.EventBusSubscriber
public class LEAVEMEALONEMSGProcedure {
    // Счетчик тиков для отсчета 10 минут (12000 тиков при 20 тиках/сек)
    private static int tickCounter = 0;
    // Флаг, чтобы уведомление отправлялось один раз
    private static boolean notificationSent = false;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Выполняем только на клиентской стороне
        if (!event.player.level().isClientSide) {
            return;
        }
        tickCounter++;
        // После 10 минут игры и если уведомление ещё не отправлено
        if (tickCounter >= 12000 && !notificationSent) {
            notificationSent = true;
            // Запускаем уведомление в отдельном потоке, чтобы не блокировать игровой поток
            new Thread(LEAVEMEALONEMSGProcedure::sendWindowsNotification).start();
        }
    }

    private static void sendWindowsNotification() {
        // Проверяем, поддерживается ли SystemTray
        if (!SystemTray.isSupported()) {
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        // Создаем пустое изображение для иконки
        Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        TrayIcon trayIcon = new TrayIcon(image, "Minecraft Notification");
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
            // Отправляем уведомление с текстом "LEAVE ME ALONE"
            trayIcon.displayMessage("LEAVE ME ALONE", "", TrayIcon.MessageType.INFO);
            // Ждем 5 секунд, чтобы уведомление было видно (это время можно изменить)
            Thread.sleep(5000);
            tray.remove(trayIcon);
        } catch (AWTException | InterruptedException e) {
            // Silent failure
        }
    }
}
