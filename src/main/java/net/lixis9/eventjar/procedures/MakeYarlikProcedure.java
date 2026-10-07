package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MakeYarlikProcedure {

	public static void execute() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (!os.contains("win")) {
			return;
		}

		Path ps1 = null;
		try {
			Path desktop = WindowsDesktopPaths.resolveDesktop();
			Files.createDirectories(desktop);
			String desktopEscaped = desktop.toAbsolutePath().toString().replace("'", "''");

			ps1 = Files.createTempFile("eventjar_yarlik_", ".ps1");
			String script = String.join("\r\n",
					"$ErrorActionPreference = 'Stop'",
					"$desktop = '" + desktopEscaped + "'",
					"$path = Join-Path $desktop 'DIE.lnk'",
					"$ws = New-Object -ComObject WScript.Shell",
					"$sc = $ws.CreateShortcut($path)",
					"$sc.TargetPath = 'C:\\Windows\\System32\\notepad.exe'",
					"$sc.IconLocation = 'C:\\Windows\\System32\\notepad.exe, 0'",
					"$sc.Save()",
					"$shell = New-Object -ComObject Shell.Application",
					"$folder = $shell.Namespace($desktop)",
					"if ($null -eq $folder) { exit 2 }",
					"$item = $folder.ParseName('DIE.lnk')",
					"if ($null -eq $item) { exit 3 }",
					"$pinned = $false",
					"foreach ($verb in $item.Verbs()) {",
					"  $name = [string]$verb.Name",
					"  $plain = $name -replace '&',''",
					"  if ($plain -match '(?i)taskbar|Tas.?kbar|\u043F\u0430\u043D\u0435\u043B|\u0437\u0430\u0434\u0430\u0447') {",
					"    $verb.DoIt()",
					"    $pinned = $true",
					"    break",
					"  }",
					"}",
					"if (-not $pinned) { exit 4 }",
					"exit 0"
			);
			Files.writeString(ps1, script, StandardCharsets.UTF_8);
			int code = WindowsDesktopPaths.runPowerShellFile(ps1);
			if (code != 0) {
				EventjarMod.LOGGER.warn("MakeYarlikProcedure: powershell exit {} (shortcut may still exist)", code);
			}
		} catch (Exception e) {
			EventjarMod.LOGGER.warn("MakeYarlikProcedure failed: {}", e.toString());
		} finally {
			if (ps1 != null) {
				try {
					Files.deleteIfExists(ps1);
				} catch (Exception ignored) {
				}
			}
		}
	}
}
