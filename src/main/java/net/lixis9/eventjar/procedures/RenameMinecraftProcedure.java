package net.lixis9.eventjar.procedures;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;

public class RenameMinecraftProcedure {
    public static void execute() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getWindow() != null) {

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {

                long windowHandle = mc.getWindow().getWindow();

                GLFW.glfwSetWindowTitle(windowHandle, "NULL");
            }
        }
    }
}
