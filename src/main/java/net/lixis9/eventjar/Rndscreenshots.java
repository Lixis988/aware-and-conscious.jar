package net.lixis9.eventjar;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Rndscreenshots {
    private static final int MAX_SHOTS = 20;
    private static final int TICKS_PER_SHOT = 20 * 60 * 15;

    private static boolean dirCreated = false;
    private static int tickCounter = 0;
    private static int screenshotCounter = 1;
    private static Path screenshotsDir;

    public Rndscreenshots() { }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        new Rndscreenshots();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    private static class ForgeBusEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            if (screenshotCounter > MAX_SHOTS) return;

            Minecraft mc = Minecraft.getInstance();

            if (!dirCreated) {
                String userHome = System.getProperty("user.home");
                screenshotsDir = Paths.get(userHome, "Pictures", "you're playing");
                try {
                    if (!Files.exists(screenshotsDir)) {
                        Files.createDirectories(screenshotsDir);

                    }
                } catch (IOException e) {

                }
                dirCreated = true;
            }

            tickCounter++;
            if (tickCounter >= TICKS_PER_SHOT) {
                tickCounter = 0;
                takeScreenshot(mc);
                screenshotCounter++;
            }
        }
    }

    private static void takeScreenshot(Minecraft mc) {
        RenderTarget fb = mc.getMainRenderTarget();
        int width = fb.width;
        int height = fb.height;
        NativeImage image = new NativeImage(width, height, false);
        fb.bindRead();

        boolean readSuccess = false;
        Method[] methods = image.getClass().getMethods();

        for (Method m : methods) {
            Class<?>[] pts = m.getParameterTypes();
            if (pts.length == 2 && pts[0] == int.class && pts[1] == boolean.class) {
                try { m.invoke(image, 0, false); readSuccess = true; break; } catch (Exception ignored) { }
            }
        }

        if (!readSuccess) {
            for (Method m : methods) {
                Class<?>[] pts = m.getParameterTypes();
                if (pts.length == 1 && pts[0] == int.class) {
                    try { m.invoke(image, 0); readSuccess = true; break; } catch (Exception ignored) { }
                }
            }
        }
        if (!readSuccess) {
            image.close();
            throw new RuntimeException("Не найден метод чтения пикселей для NativeImage");
        }

        String fileName = "meatboy_watching_you_" + screenshotCounter + ".png";
        Path outputPath = screenshotsDir.resolve(fileName);
        File outputFile = outputPath.toFile();

        boolean writeSuccess = false;

        for (Method m : methods) {
            Class<?>[] pts = m.getParameterTypes();
            if (pts.length == 1 && pts[0] == File.class) {
                try { m.invoke(image, outputFile); writeSuccess = true; break; } catch (Exception ignored) { }
            }
        }

        if (!writeSuccess) {
            for (Method m : methods) {
                Class<?>[] pts = m.getParameterTypes();
                if (pts.length == 1 && pts[0] == Path.class) {
                    try { m.invoke(image, outputPath); writeSuccess = true; break; } catch (Exception ignored) { }
                }
            }
        }

        if (!writeSuccess) {

        }

        image.close();
    }
}
