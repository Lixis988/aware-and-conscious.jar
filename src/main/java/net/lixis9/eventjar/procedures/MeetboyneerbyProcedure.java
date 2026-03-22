package net.lixis9.eventjar.procedures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import java.lang.reflect.Field;

public class MeetboyneerbyProcedure {
    public static void execute() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof TitleScreen) {
            TitleScreen titleScreen = (TitleScreen) mc.screen;
            try {
                Field splashField;
                // Пробуем получить поле splash в режиме разработки
                try {
                    splashField = TitleScreen.class.getDeclaredField("splash");
                } catch (NoSuchFieldException e) {
                    // Если поле не найдено – пытаемся альтернативное имя для production
                    splashField = TitleScreen.class.getDeclaredField("field_92048_a");
                }
                splashField.setAccessible(true);
                Object splashRenderer = splashField.get(titleScreen);

                // Ищем первое строковое поле внутри splashRenderer с непустым значением
                Field targetField = null;
                for (Field field : splashRenderer.getClass().getDeclaredFields()) {
                    if (field.getType().equals(String.class)) {
                        field.setAccessible(true);
                        String value = (String) field.get(splashRenderer);
                        if (value != null && !value.isEmpty()) {
                            targetField = field;
                            break;
                        }
                    }
                }
                if (targetField != null) {
                    targetField.set(splashRenderer, "meetboy nearby");
                    // Можно добавить логирование при необходимости, но помните, что вывод каждую итерацию может быть избыточным
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Silent failure
            }
        }
    }
}
