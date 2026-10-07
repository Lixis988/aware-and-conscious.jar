package net.lixis.outofbound.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

@OnlyIn(Dist.CLIENT)
public final class WindowsNotificationUtil {

	private WindowsNotificationUtil() {
	}

	public static void showToast(String title, String message) {
		if (!isWindows()) {
			return;
		}

		new Thread(() -> {
			try {
				String command = buildToastCommand(title, message);
				new ProcessBuilder("powershell.exe", "-WindowStyle", "Hidden", "-STA", "-Command", command).start();
			} catch (Exception ignored) {
				SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
						null,
						message,
						title,
						JOptionPane.WARNING_MESSAGE));
			}
		}, "outofbound-toast").start();
	}

	private static boolean isWindows() {
		String os = System.getProperty("os.name", "");
		return os.toLowerCase().contains("win");
	}

	private static String buildToastCommand(String title, String message) {
		String safeTitle = escapePowerShellSingleQuoted(title);
		String safeMessage = escapePowerShellSingleQuoted(message);
		return "[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null; "
				+ "$template = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent([Windows.UI.Notifications.ToastTemplateType]::ToastText02); "
				+ "$textNodes = $template.GetElementsByTagName('text'); "
				+ "$textNodes.Item(0).AppendChild($template.CreateTextNode('" + safeTitle + "')) | Out-Null; "
				+ "$textNodes.Item(1).AppendChild($template.CreateTextNode('" + safeMessage + "')) | Out-Null; "
				+ "$notifier = [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('outofbound'); "
				+ "$notifier.Show([Windows.UI.Notifications.ToastNotification]::new($template));";
	}

	private static String escapePowerShellSingleQuoted(String value) {
		return value.replace("'", "''");
	}
}
