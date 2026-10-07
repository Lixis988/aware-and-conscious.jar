package net.lixis9.eventjar.procedures;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Iseeyou3Procedure {
    @OnlyIn(Dist.CLIENT)
    public static void execute() {

        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return;
        }
        try {

            new ProcessBuilder(
                "cmd.exe", "/c",
                "start", "\"I SEE YOU\"", "/MIN",
                "cmd.exe", "/c",
                "ipconfig & ping -t 127.0.0.1 > nul"
            ).start();
        } catch (Exception e) {

        }
    }
}
