package net.lixis9.eventjar.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.Random;

public class GlitchScree extends Screen {
    private int tickCounter = 0;
    private static final int DURATION = 60; // Длительность эффекта в тиках (~3 сек)
    private static final Random random = new Random();

    public GlitchScree() {
        super(Component.literal(""));
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter++;
        if (tickCounter >= DURATION) {
            this.onClose();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Полностью черный фон
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);

        // Эффект глюков - случайные прямоугольники разных цветов
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(this.width);
            int y = random.nextInt(this.height);
            int w = random.nextInt(50) + 10;
            int h = random.nextInt(30) + 5;
            
            // Случайный цвет (красный, зеленый, синий, белый)
            int color;
            int colorType = random.nextInt(4);
            switch (colorType) {
                case 0:
                    color = 0xFF0000 | random.nextInt(0xFF); // Красный
                    break;
                case 1:
                    color = 0x00FF00 | (random.nextInt(0xFF) << 8); // Зеленый
                    break;
                case 2:
                    color = 0x0000FF | (random.nextInt(0xFF) << 16); // Синий
                    break;
                default:
                    color = 0xFFFFFF; // Белый
                    break;
            }
            
            guiGraphics.fill(x, y, x + w, y + h, color);
        }
        
        // Случайные линии
        for (int i = 0; i < 20; i++) {
            int x1 = random.nextInt(this.width);
            int y1 = random.nextInt(this.height);
            int x2 = random.nextInt(this.width);
            int y2 = random.nextInt(this.height);
            int color = 0xFFFFFF | random.nextInt(0xFF);
            
            guiGraphics.hLine(x1, x2, y1, color);
            guiGraphics.vLine(x1, y1, y2, color);
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
