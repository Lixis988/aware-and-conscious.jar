package net.lixis9.eventjar.procedures;

import java.io.IOException;

public class WinmessegeProcedure {
    public static void execute() {
        // Проверяем, что операционная система — Windows
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("win")) {
            return;
        }

        // PowerShell-команда для отправки Toast-уведомления
        String[] command = {
            "powershell.exe",
            "-NoProfile",
            "-Command",
            "[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType=WindowsRuntime] | Out-Null; " +
            "[Windows.Data.Xml.Dom.XmlDocument, Windows.Data.Xml.Dom.XmlDocument, ContentType=WindowsRuntime] | Out-Null; " +
            "$template = [Windows.UI.Notifications.ToastTemplateType]::ToastText01; " +
            "$xml = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent($template); " +
            "$textNodes = $xml.GetElementsByTagName('text'); " +
            "$textNodes.Item(0).AppendChild($xml.CreateTextNode('I live inside your pc')) | Out-Null; " +
            "$toast = [Windows.UI.Notifications.ToastNotification]::new($xml); " +
            "$notifier = [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('meatboy'); " +
            "$notifier.Show($toast)"
        };

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            // Silent failure
        }
    }
}
