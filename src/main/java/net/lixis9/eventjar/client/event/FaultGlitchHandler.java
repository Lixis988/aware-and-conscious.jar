package net.lixis9.eventjar.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class FaultGlitchHandler implements IGuiOverlay {
    private static boolean glitchActive = false;
    private static int glitchTimer = 0;
    private static final int MAX_GLITCH_TIME = 60; // 3 секунды при 20 TPS

    public static void triggerActivationGlitch() {
        glitchActive = true;
        glitchTimer = 0;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!glitchActive) return;

        glitchTimer++;
        
        // Простой эффект глитча - случайные смещения экрана
        if (glitchTimer <= MAX_GLITCH_TIME) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                // Случайные смещения экрана для эффекта глитча
                // int offsetX = (int) (Math.random() * 4) - 2;
                // int offsetY = (int) (Math.random() * 4) - 2;
                
                // Здесь можно добавить более сложные эффекты глитча
                // например, искажение цветов, мерцание и т.д.
            }
        } else {
            glitchActive = false;
            glitchTimer = 0;
        }
    }
}
