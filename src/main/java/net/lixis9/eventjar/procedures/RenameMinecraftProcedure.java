package net.lixis9.eventjar.procedures;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;

public class RenameMinecraftProcedure {
    public static void execute() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getWindow() != null) {
            // Проверяем, что игра запущена на Windows
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Используем существующий метод getWindow() для получения дескриптора окна
                long windowHandle = mc.getWindow().getWindow();
                // Устанавливаем новый заголовок окна
                GLFW.glfwSetWindowTitle(windowHandle, "NULL");
            }
        }
    }
}
