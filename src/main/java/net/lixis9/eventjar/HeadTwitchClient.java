package net.lixis9.eventjar.procedures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "eventjar", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeadTwitchClient {

    private static final Map<UUID, TwitchData> twitchDataMap = new HashMap<>();

    public static void startTwitch(LocalPlayer player) {
        TwitchData data = new TwitchData(player.getYHeadRot());
        twitchDataMap.put(player.getUUID(), data);
    }

    public static void stopTwitch(UUID uuid) {
        twitchDataMap.remove(uuid);
    }

    public static TwitchData getTwitchData(UUID uuid) {
        return twitchDataMap.get(uuid);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        
        TwitchData data = twitchDataMap.get(player.getUUID());
        if (data == null) return;
        
        data.currentOffset = (float) ((Math.random() - 0.5) * 40);
        data.ticks++;
    }

    public static class TwitchData {
        public final float originalYaw;
        public float currentOffset;
        public int ticks;

        public TwitchData(float originalYaw) {
            this.originalYaw = originalYaw;
            this.ticks = 0;
            this.currentOffset = 0;
        }
    }
}