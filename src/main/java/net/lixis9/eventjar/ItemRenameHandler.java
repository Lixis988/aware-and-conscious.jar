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

    /**
     * При подборе нового предмета сразу даём ему случайное имя.
     * После этого оно НЕ будет меняться автоматически.
     */
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        // Проверяем, включено ли переименование предметов
        if (!ConfigManager.isItemRenamerEnabled()) {
            return;
        }
        
        // Берем ItemEntity из события, затем получаем сам ItemStack
        ItemEntity itemEntity = event.getItem();
        ItemStack  stack      = itemEntity.getItem();

        // Присваиваем случайное имя — это затрагивает только компонент hoverName,
        // никаких других NBT-тегов (например, RepairCost) мы не трогаем.
        stack.setHoverName(Component.literal(randomText()));
    }

    /**
     * Генерирует случайный «хаотичный» текст длиной 4–12 символов.
     */
    private static String randomText() {
        int len = 4 + RANDOM.nextInt(9);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            // печатные ASCII-символы от '!' до '~'
            sb.append((char) (33 + RANDOM.nextInt(94)));
        }
        return sb.toString();
    }
}