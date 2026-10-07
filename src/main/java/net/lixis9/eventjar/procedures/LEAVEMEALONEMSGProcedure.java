package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.TickThrottle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber
public class LEAVEMEALONEMSGProcedure {
	private static int tickCounter = 0;
	private static boolean notificationSent = false;

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!event.player.level().isClientSide) {
			return;
		}
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (notificationSent) {
			return;
		}
		if (!TickThrottle.due(event.player)) {
			return;
		}
		tickCounter += TickThrottle.INTERVAL;
		if (tickCounter >= 12000) {
			notificationSent = true;
			new Thread(LEAVEMEALONEMSGProcedure::sendWindowsNotification, "eventjar-leave-msg").start();
		}
	}

	private static void sendWindowsNotification() {
		if (!java.awt.SystemTray.isSupported()) {
			EventjarMod.LOGGER.warn("LEAVE ME ALONE tray notification skipped: SystemTray not supported");
			return;
		}
		java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
		java.awt.Image image = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Minecraft Notification");
		trayIcon.setImageAutoSize(true);
		try {
			tray.add(trayIcon);
			trayIcon.displayMessage("LEAVE ME ALONE", "", java.awt.TrayIcon.MessageType.INFO);
			Thread.sleep(5000);
			tray.remove(trayIcon);
		} catch (java.awt.AWTException | InterruptedException e) {
			EventjarMod.LOGGER.warn("LEAVE ME ALONE tray notification failed: {}", e.toString());
		}
	}
}
