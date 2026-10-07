package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.systems.RenderSystem;

import net.lixis9.eventjar.init.EventjarModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;

@Mod.EventBusSubscriber(modid = "eventjar", value = Dist.CLIENT)
public class OverdoseprocedureProcedure {

    private static long lastShakeTime = 0;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {

    }

    @SubscribeEvent
    public static void onRenderTick(net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null && player.hasEffect(EventjarModMobEffects.OVERDOSE.get())) {

            float shakeIntensity = 8.0f;

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShakeTime > 20) {
                lastShakeTime = currentTime;

                float randomX = (float) (Math.random() - 0.5) * shakeIntensity;
                float randomY = (float) (Math.random() - 0.5) * shakeIntensity;

                event.setPitch(event.getPitch() + randomY);
                event.setYaw(event.getYaw() + randomX);
            }
        }
    }
}
