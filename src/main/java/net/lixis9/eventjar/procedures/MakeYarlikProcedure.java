package net.lixis9.eventjar.procedures;

import java.io.IOException;

public class MakeYarlikProcedure {
    public static void execute() {
        try {
            // 1) Создаём .lnk-файл на рабочем столе с названием DIE, указывающий на Notepad.exe.
            String createShortcut = 
                "powershell.exe -NoProfile -Command " +
                "\"$ws = New-Object -ComObject WScript.Shell; " +
                "$path = [Environment]::GetFolderPath('Desktop') + '\\\\DIE.lnk'; " +
                "$sc = $ws.CreateShortcut($path); " +
                "$sc.TargetPath = 'C:\\\\Windows\\\\System32\\\\notepad.exe'; " +
                "$sc.IconLocation = 'C:\\\\Windows\\\\System32\\\\notepad.exe, 0'; " +
                "$sc.Save()\"";
            Runtime.getRuntime().exec(createShortcut).waitFor();

            // 2) Закрепляем этот ярлык на панели задач
            String pinToTaskbar =
                "powershell.exe -NoProfile -Command " +
                "\"$s = New-Object -ComObject Shell.Application; " +
                "$desk = $s.Namespace([Environment]::GetFolderPath('Desktop')); " +
                "$item = $desk.ParseName('DIE.lnk'); " +
                "if ($item) { $item.InvokeVerb('Pin to Tas&kbar') }\"";
            Runtime.getRuntime().exec(pinToTaskbar).waitFor();

        } catch (IOException | InterruptedException e) {
            // Silent failure
        }
    }
}
