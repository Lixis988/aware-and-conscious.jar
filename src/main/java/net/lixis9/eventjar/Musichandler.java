package net.lixis9.eventjar;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.ScreenEvent.Closing;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.TitleScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Musichandler {
    private static final ResourceLocation PIANO_RL =
        new ResourceLocation("eventjar", "gamemenu");
    private static final SoundEvent PIANO_MUSIC =
        SoundEvent.createVariableRangeEvent(PIANO_RL);

    // Флаг, чтобы подменять музыку только один раз
    private static boolean isCustomMenuPlaying = false;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onMenuMusic(PlaySoundEvent event) {
        SoundInstance orig = event.getSound();
        if (orig == null) {
            return;
        }
        // Если это уже наша музыка — ничего не делаем
        if (orig.getLocation().equals(PIANO_RL)) {
            return;
        }
        // Обрабатываем только стандартный трек меню
        if (orig.getSource() == SoundSource.MUSIC
            && orig.getLocation().equals(new ResourceLocation("minecraft", "music.menu"))
        ) {
            if (!isCustomMenuPlaying) {
                // Первый раз — запускаем нашу зацикленную музыку
                event.setSound(SimpleSoundInstance.forMusic(PIANO_MUSIC));
                isCustomMenuPlaying = true;
            } else {
                // Повторные события — отменяем, чтобы не накладывать звук
                event.setSound(null);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onScreenClosing(Closing event) {
        // При закрытии TitleScreen сбрасываем флаг,
        // чтобы при возвращении в меню музыка запустилась заново
        if (event.getScreen() instanceof TitleScreen) {
            isCustomMenuPlaying = false;
        }
    }
}
