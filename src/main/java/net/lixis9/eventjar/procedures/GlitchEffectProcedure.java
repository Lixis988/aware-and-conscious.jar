package net.lixis9.eventjar.procedures;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.lixis9.eventjar.client.gui.GlitchScree;

public class GlitchEffectProcedure {
    public static void execute() {
        Minecraft minecraft = Minecraft.getInstance();

        // Чтобы экран всегда вызывался
        if (minecraft.screen != null) {
            minecraft.setScreen(null);
        }

        RenderSystem.recordRenderCall(() -> {
            minecraft.setScreen(new GlitchScree());
        });
    }
}
