package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.systems.RenderSystem;

import net.lixis9.eventjar.init.EventjarModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;

@Mod.EventBusSubscriber(modid = "eventjar")
public class OverdoseprocedureProcedure {
    
    private static long lastShakeTime = 0;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        // Убрали красную рамку - теперь только тряска
    }

    @SubscribeEvent
    public static void onRenderTick(net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        
        // Очень сильная тряска при эффекте overdose
        if (player != null && player.hasEffect(EventjarModMobEffects.OVERDOSE.get())) {
            // Очень сильная тряска
            float shakeIntensity = 8.0f; // Очень сильная тряска
            
            // Случайные смещения для тряски
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShakeTime > 20) { // Очень быстрая тряска каждые 20мс
                lastShakeTime = currentTime;
                
                float randomX = (float) (Math.random() - 0.5) * shakeIntensity;
                float randomY = (float) (Math.random() - 0.5) * shakeIntensity;
                
                // Применяем очень сильную тряску к углам камеры
                event.setPitch(event.getPitch() + randomY);
                event.setYaw(event.getYaw() + randomX);
            }
        }
    }
}
