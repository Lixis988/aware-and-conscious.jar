package net.lixis9.eventjar;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.lixis9.eventjar.client.AacTextChaosGuard;

import java.lang.reflect.Field;
import java.util.Random;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MenuChaosClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent evt) {
        MinecraftForge.EVENT_BUS.register(new ChaosHandler());
    }

    private static class ChaosHandler {
        private final Random rnd = new Random();

        @SubscribeEvent
        public void onScreenRender(ScreenEvent.Render.Post event) {
            if (!ConfigManager.isTextDistortionEnabled()) {
                return;
            }

            Screen screen = event.getScreen();
            if (AacTextChaosGuard.shouldSkip(screen)) {
                return;
            }

            if (screen instanceof TitleScreen) {
                randomizeSplash((TitleScreen) screen);
            }

            for (var element : screen.children()) {
                if (element instanceof AbstractWidget widget) {
                    Component old = widget.getMessage();
                    String rand = randomText(old.getString().length());

                    if (widget instanceof Button btn) {
                        btn.setMessage(Component.literal(rand));
                    } else {

                        try {
                            Field msgField = widget.getClass().getDeclaredField("message");
                            msgField.setAccessible(true);
                            msgField.set(widget, Component.literal(rand));
                        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
                    }
                }
            }
        }

        private String randomText(int len) {
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                sb.append((char) (33 + rnd.nextInt(94)));
            }
            return sb.toString();
        }

        private void randomizeSplash(TitleScreen ts) {
            try {
                Field splash = TitleScreen.class.getDeclaredField("splashText");
                splash.setAccessible(true);
                String old = (String) splash.get(ts);
                splash.set(ts, randomText(old.length()));
            } catch (ReflectiveOperationException ignored) {}
        }
    }
}
