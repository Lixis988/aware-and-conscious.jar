package net.lixis9.eventjar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления переменными мира, включая отслеживание дней
 */
public class WorldVariables extends SavedData {
    
    private static final String DATA_NAME = "eventjar_world_data";
    
    // Переменные для отслеживания дней
    private long worldDay = 1;
    private Map<String, Object> customVariables = new HashMap<>();
    
    public WorldVariables() {
        super();
    }
    
    public WorldVariables(long worldDay) {
        super();
        this.worldDay = worldDay;
    }
    
    /**
     * Получает экземпляр WorldVariables для указанного мира
     */
    public static WorldVariables get(LevelAccessor world) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return new WorldVariables();
        }
        
        DimensionDataStorage storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent(WorldVariables::load, WorldVariables::new, DATA_NAME);
    }
    
    /**
     * Загружает данные из NBT
     */
    public static WorldVariables load(CompoundTag nbt) {
        WorldVariables data = new WorldVariables();
        data.worldDay = nbt.getLong("worldDay");
        
        // Загружаем кастомные переменные
        if (nbt.contains("customVariables")) {
            CompoundTag customVars = nbt.getCompound("customVariables");
            for (String key : customVars.getAllKeys()) {
                data.customVariables.put(key, customVars.get(key));
            }
        }
        
        return data;
    }
    
    /**
     * Сохраняет данные в NBT
     */
    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putLong("worldDay", worldDay);
        
        // Сохраняем кастомные переменные
        CompoundTag customVars = new CompoundTag();
        for (Map.Entry<String, Object> entry : customVariables.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                customVars.putString(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                customVars.putInt(entry.getKey(), (Integer) value);
            } else if (value instanceof Long) {
                customVars.putLong(entry.getKey(), (Long) value);
            } else if (value instanceof Boolean) {
                customVars.putBoolean(entry.getKey(), (Boolean) value);
            } else if (value instanceof Double) {
                customVars.putDouble(entry.getKey(), (Double) value);
            } else if (value instanceof Float) {
                customVars.putFloat(entry.getKey(), (Float) value);
            }
        }
        nbt.put("customVariables", customVars);
        
        return nbt;
    }
    
    /**
     * Получает текущий день мира
     */
    public long getWorldDay() {
        return worldDay;
    }
    
    /**
     * Устанавливает день мира
     */
    public void setWorldDay(long day) {
        this.worldDay = day;
        setDirty();
    }
    
    /**
     * Увеличивает день мира на 1
     */
    public void incrementWorldDay() {
        this.worldDay++;
        setDirty();
    }
    
    /**
     * Получает кастомную переменную
     */
    public Object getCustomVariable(String key) {
        return customVariables.get(key);
    }
    
    /**
     * Устанавливает кастомную переменную
     */
    public void setCustomVariable(String key, Object value) {
        this.customVariables.put(key, value);
        setDirty();
    }
    
    /**
     * Проверяет, существует ли кастомная переменная
     */
    public boolean hasCustomVariable(String key) {
        return customVariables.containsKey(key);
    }
    
    /**
     * Удаляет кастомную переменную
     */
    public void removeCustomVariable(String key) {
        this.customVariables.remove(key);
        setDirty();
    }
}
