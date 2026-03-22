package net.lixis9.eventjar.procedures;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class LightsettingProcedure {
    
    // Процедура для установки низкого уровня яркости (гаммы)
    public static void execute() {
        // Правильный доступ к gamma в 1.20.1
        Minecraft.getInstance().options.gamma().set(0.1);
    }
    
    // Подписка на клиентский тик: вызывается каждый тик клиента
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        execute();
    }
}
