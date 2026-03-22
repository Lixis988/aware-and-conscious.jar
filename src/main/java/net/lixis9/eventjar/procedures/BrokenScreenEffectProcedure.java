package net.lixis9.eventjar.procedures;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.Random;

public class BrokenScreenEffectProcedure extends Screen {
    private int tickCounter = 0;
    private static final int DURATION = 100; // Длительность эффекта в тиках (~5 сек)
    private static final Random random = new Random();

    public BrokenScreenEffectProcedure() {
        super(Component.literal("Сбой экрана"));
    }

    public static void execute() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.setScreen(new BrokenScreenEffectProcedure());
        }
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter++;
        if (tickCounter >= DURATION) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Затемнённый фон
        guiGraphics.fill(0, 0, this.width, this.height, 0x99000000);

        // Эффект глюков - случайные прямоугольники
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(this.width);
            int y = random.nextInt(this.height);
            int w = random.nextInt(100);
            int h = random.nextInt(50);
            int color = 0xFF000000 | random.nextInt(0xFFFFFF);
            guiGraphics.fill(x, y, x + w, y + h, color);
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

