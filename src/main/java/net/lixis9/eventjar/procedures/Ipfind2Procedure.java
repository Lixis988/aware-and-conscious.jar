package net.lixis9.eventjar.procedures;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Ipfind2Procedure {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Проверяем, что вошедший — серверный игрок
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Получаем локальный IP сервера
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ip = "IP unknown";
        }

        // Готовим текст
        Component message = Component.literal("now i know where you live: " + ip);

        // Отправляем одноразовое сообщение в чат
        serverPlayer.sendSystemMessage(message);
    }
}
