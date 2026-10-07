package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CalculatorFindProcedure {

	public static void execute() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (!os.contains("win")) {
			return;
		}

		Path ps1 = null;
		try {
			ps1 = Files.createTempFile("eventjar_calc_", ".ps1");
			String script = String.join("\r\n",
					"$ErrorActionPreference = 'SilentlyContinue'",
					"Start-Process calc",
					"Start-Sleep -Milliseconds 1200",
					"$shell = New-Object -ComObject WScript.Shell",
					"$titles = @('Calculator', 'calc')",
					"$activated = $false",
					"foreach ($t in $titles) {",
					"  if ($shell.AppActivate($t)) { $activated = $true; break }",
					"}",
					"if (-not $activated) {",
					"  Get-Process | Where-Object { $_.MainWindowTitle -match 'Calc|Kal' } | ForEach-Object {",
					"    if ($_.MainWindowTitle -and $shell.AppActivate($_.MainWindowTitle)) { $activated = $true }",
					"  }",
					"}",
					"Start-Sleep -Milliseconds 300",
					"if ($activated) { $shell.SendKeys('666') }",
					"exit 0"
			);
			Files.writeString(ps1, script, StandardCharsets.UTF_8);
			int code = WindowsDesktopPaths.runPowerShellFile(ps1);
			if (code != 0) {
				EventjarMod.LOGGER.warn("CalculatorFindProcedure: powershell exit {}", code);
			}
		} catch (Exception e) {
			EventjarMod.LOGGER.warn("CalculatorFindProcedure failed: {}", e.toString());
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
