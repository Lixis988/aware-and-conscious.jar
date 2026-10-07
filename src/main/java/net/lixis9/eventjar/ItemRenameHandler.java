package net.lixis9.eventjar;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.lixis9.eventjar.ConfigManager;

import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemRenameHandler {
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {

        if (!ConfigManager.isItemRenamerEnabled()) {
            return;
        }

        ItemEntity itemEntity = event.getItem();
        ItemStack  stack      = itemEntity.getItem();

        stack.setHoverName(Component.literal(randomText()));
    }

    private static String randomText() {
        int len = 4 + RANDOM.nextInt(9);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {

            sb.append((char) (33 + RANDOM.nextInt(94)));
        }
        return sb.toString();
    }
}
