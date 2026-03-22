package net.lixis9.eventjar.procedures;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "eventjar", value = Dist.CLIENT)
public class ClientTickHandlerProcedure {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // Вызываем процедуру на каждом клиентском тике,
        // чтобы заголовок окна постоянно обновлялся и не перезаписывался Minecraft'ом.
        RenameMinecraftProcedure.execute();
    }
}
