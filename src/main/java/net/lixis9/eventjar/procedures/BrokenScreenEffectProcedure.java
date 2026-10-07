package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.init.EventjarModMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID, value = Dist.CLIENT)
public class BrokenScreenEffectProcedure extends Screen {
    private int tickCounter = 0;
    private static final int DURATION = 100;
    private static final Random random = new Random();

    public BrokenScreenEffectProcedure() {
        super(Component.literal("Сбой экрана"));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        if (minecraft.player.hasEffect(EventjarModMobEffects.BROKEN_SCREAN_EFFECT.get())
                && !(minecraft.screen instanceof BrokenScreenEffectProcedure)) {
            execute();
        }
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
        guiGraphics.fill(0, 0, this.width, this.height, 0x99000000);

        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(Math.max(this.width, 1));
            int y = random.nextInt(Math.max(this.height, 1));
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
