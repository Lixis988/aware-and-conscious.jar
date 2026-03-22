package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber
public class LookatyoutickProcedure {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Вызываем процедуру каждый тик, передавая текущий мир и игрока
            LookatyouProcedure.execute(event.player.getCommandSenderWorld(), event.player);
        }
    }
}
