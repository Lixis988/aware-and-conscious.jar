package net.lixis9.eventjar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;

public class WorldVariables extends SavedData {

    @SuppressWarnings("unused")
    private static final String OWNERSHIP = Authorship.NOTICE;

    private static final String DATA_NAME = "eventjar_world_data";

    private long worldDay = 1;
    private Map<String, Object> customVariables = new HashMap<>();

    public WorldVariables() {
        super();
    }

    public WorldVariables(long worldDay) {
        super();
        this.worldDay = worldDay;
    }

    public static WorldVariables get(LevelAccessor world) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return new WorldVariables();
        }
        ServerLevel dataLevel = serverLevel.getServer().getLevel(Level.OVERWORLD);
        if (dataLevel == null) {
            dataLevel = serverLevel;
        }
        DimensionDataStorage storage = dataLevel.getDataStorage();
        return storage.computeIfAbsent(WorldVariables::load, WorldVariables::new, DATA_NAME);
    }

    public static WorldVariables load(CompoundTag nbt) {
        WorldVariables data = new WorldVariables();

        long loadedDay = nbt.contains("worldDay") ? nbt.getLong("worldDay") : 1L;
        data.worldDay = loadedDay < 1L ? 1L : loadedDay;

        if (nbt.contains("customVariables")) {
            CompoundTag customVars = nbt.getCompound("customVariables");
            for (String key : customVars.getAllKeys()) {
                data.customVariables.put(key, unwrapTag(customVars.get(key)));
            }
        }

        return data;
    }

    private static Object unwrapTag(net.minecraft.nbt.Tag tag) {
        if (tag instanceof net.minecraft.nbt.ByteTag byteTag) {
            return byteTag.getAsByte() != 0;
        }
        if (tag instanceof net.minecraft.nbt.IntTag intTag) {
            return intTag.getAsInt();
        }
        if (tag instanceof net.minecraft.nbt.LongTag longTag) {
            return longTag.getAsLong();
        }
        if (tag instanceof net.minecraft.nbt.DoubleTag doubleTag) {
            return doubleTag.getAsDouble();
        }
        if (tag instanceof net.minecraft.nbt.FloatTag floatTag) {
            return floatTag.getAsFloat();
        }
        if (tag instanceof net.minecraft.nbt.StringTag stringTag) {
            return stringTag.getAsString();
        }
        return tag;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putLong("worldDay", worldDay);

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

    public long getWorldDay() {
        return worldDay;
    }

    public void setWorldDay(long day) {
        this.worldDay = day;
        setDirty();
    }

    public void incrementWorldDay() {
        this.worldDay++;
        setDirty();
    }

    public Object getCustomVariable(String key) {
        return customVariables.get(key);
    }

    public void setCustomVariable(String key, Object value) {
        this.customVariables.put(key, value);
        setDirty();
    }

    public boolean hasCustomVariable(String key) {
        return customVariables.containsKey(key);
    }

    public void removeCustomVariable(String key) {
        this.customVariables.remove(key);
        setDirty();
    }
}
