package net.lixis9.eventjar;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Ipfind {
    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        // Регистрируем слушатель GUI-событий на шине Forge
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ScreenHandler());
    }

    private static class ScreenHandler {
        @SubscribeEvent
        public void onScreenInit(ScreenEvent.Init.Post event) {
            if (event.getScreen() instanceof TitleScreen title) {
                try {
                    // Получаем локальный IP
                    String ip = InetAddress.getLocalHost().getHostAddress();

                    // Рефлексией меняем поле splashText в TitleScreen
                    Field splashField = TitleScreen.class.getDeclaredField("splashText");
                    splashField.setAccessible(true);
                    splashField.set(title, ip);

                } catch (UnknownHostException e) {
                    // Silent failure - keep original splash
                } catch (ReflectiveOperationException e) {
                    // Silent failure - field may have been renamed
                }
            }
        }
    }
}
