package net.lixis9.eventjar;

import net.lixis9.eventjar.procedures.MeetboyneerbyProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "eventjar", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GameLoadEventHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // Если активен TitleScreen – вызываем процедуру каждый тик
        if (Minecraft.getInstance().screen instanceof TitleScreen) {
            MeetboyneerbyProcedure.execute();
        }
    }
}
