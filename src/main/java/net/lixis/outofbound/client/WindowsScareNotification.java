package net.lixis.outofbound.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

@OnlyIn(Dist.CLIENT)
public final class WindowsScareNotification {

	private static final String TITLE = "C:\\Windows\\System32\\Boot";
	private static final String MESSAGE = "Unable to access the file winload.exe";

	private WindowsScareNotification() {
	}

	public static void show() {
		new Thread(() -> {
			try {
				String command = "Add-Type -AssemblyName System.Windows.Forms; "
						+ "[System.Windows.Forms.MessageBox]::Show('"
						+ MESSAGE
						+ "','"
						+ TITLE
						+ "',0,16)";
				new ProcessBuilder("powershell.exe", "-WindowStyle", "Hidden", "-Command", command).start();
			} catch (Exception ignored) {
				SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
						null,
						MESSAGE,
						TITLE,
						JOptionPane.ERROR_MESSAGE));
			}
		}, "outofbound-scare").start();
	}
}
