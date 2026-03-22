package net.lixis9.eventjar;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.MenuProvider;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.lixis9.eventjar.world.inventory.Test1Menu;
import net.lixis9.eventjar.world.inventory.Test2Menu;
import net.lixis9.eventjar.procedures.CalculatorFindProcedure;
import net.lixis9.eventjar.procedures.WorkspacereplaceProcedure;
import net.lixis9.eventjar.procedures.ChunkDellProcedure;
import net.lixis9.eventjar.procedures.WrongProcedure;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import io.netty.buffer.Unpooled;
import java.util.Random;

/**
 * Обработчик процедур дня, который управляет выполнением событий в зависимости от дня мира.
 * Использует систему переменных мира для отслеживания текущего дня.
 */
public class DayProcedureHandler {
    
    private static final Random RANDOM = new Random();
    
    /**
     * Выполняет процедуру для указанного дня
     * @param world Мир
     * @param x Координата X
     * @param y Координата Y  
     * @param z Координата Z
     * @param entity Сущность (обычно игрок)
     * @param day Номер дня (1-20)
     */
    public static void executeDayProcedure(LevelAccessor world, double x, double y, double z, Entity entity, int day) {
        if (entity == null || !(entity instanceof ServerPlayer)) {
            return;
        }
        
        ServerPlayer player = (ServerPlayer) entity;
        BlockPos pos = BlockPos.containing(x, y, z);
        
        // Получаем текущий день мира из переменных
        long worldDay = getWorldDay(world);
        
        // Проверяем, что это правильный день для выполнения процедуры
        if (worldDay != day) {
            return;
        }
        
        // Проверяем, что день еще не был выполнен
        if (isDayExecuted(world, day)) {
            return;
        }
        
        // Отмечаем день как выполненный
        markDayExecuted(world, day);
        
        // Выполняем процедуру в зависимости от дня
        switch (day) {
            case 1:
                executeDay1(world, player, pos);
                break;
            case 2:
                executeDay2(world, player, pos);
                break;
            case 3:
                executeDay3(world, player, pos);
                break;
            case 4:
                executeDay4(world, player, pos);
                break;
            case 5:
                executeDay5(world, player, pos);
                break;
            case 6:
                executeDay6(world, player, pos);
                break;
            case 7:
                executeDay7(world, player, pos);
                break;
            case 8:
                executeDay8(world, player, pos);
                break;
            case 9:
                executeDay9(world, player, pos);
                break;
            case 10:
                executeDay10(world, player, pos);
                break;
            case 11:
                executeDay11(world, player, pos);
                break;
            case 12:
                executeDay12(world, player, pos);
                break;
            case 13:
                executeDay13(world, player, pos);
                break;
            case 14:
                executeDay14(world, player, pos);
                break;
            case 15:
                executeDay15(world, player, pos);
                break;
            case 16:
                executeDay16(world, player, pos);
                break;
            case 17:
                executeDay17(world, player, pos);
                break;
            case 18:
                executeDay18(world, player, pos);
                break;
            case 19:
                executeDay19(world, player, pos);
                break;
            case 20:
                executeDay20(world, player, pos);
                break;
        }
    }
    
    /**
     * Получает текущий день мира из переменных
     */
    private static long getWorldDay(LevelAccessor world) {
        WorldVariables variables = WorldVariables.get(world);
        return variables.getWorldDay();
    }
    
    /**
     * Увеличивает день мира на 1
     */
    public static void incrementWorldDay(LevelAccessor world) {
        WorldVariables variables = WorldVariables.get(world);
        variables.incrementWorldDay();
    }
    
    /**
     * Устанавливает день мира
     */
    public static void setWorldDay(LevelAccessor world, long day) {
        WorldVariables variables = WorldVariables.get(world);
        variables.setWorldDay(day);
    }
    
    /**
     * Проверяет, был ли день уже выполнен
     */
    public static boolean isDayExecuted(LevelAccessor world, int day) {
        WorldVariables variables = WorldVariables.get(world);
        String key = "day_" + day + "_executed";
        if (!variables.hasCustomVariable(key)) {
            return false;
        }
        Object value = variables.getCustomVariable(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof net.minecraft.nbt.ByteTag) {
            return ((net.minecraft.nbt.ByteTag) value).getAsByte() != 0;
        }
        return false;
    }
    
    /**
     * Отмечает день как выполненный
     */
    public static void markDayExecuted(LevelAccessor world, int day) {
        WorldVariables variables = WorldVariables.get(world);
        variables.setCustomVariable("day_" + day + "_executed", true);
    }
    
    // Процедуры для каждого дня
    
    private static void executeDay1(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        EventjarMod.queueServerWork(12000, () -> {
            if (!world.isClientSide() && world.getServer() != null)
                world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 1: Welcome to the nightmare"), false);
            EventjarMod.queueServerWork(20, () -> {
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, pos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambient.cave")), SoundSource.NEUTRAL, 1, 1);
                    } else {
                        _level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambient.cave")), SoundSource.NEUTRAL, 1, 1, false);
                    }
                }
            });
            if (player != null) {
                NetworkHooks.openScreen(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Test1");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new Test1Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                    }
                }, pos);
            }
        });
    }
    
    private static void executeDay2(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        EventjarMod.queueServerWork(35000, () -> {
            if (!world.isClientSide() && world.getServer() != null)
                world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 2: The voices begin"), false);
            if (player != null) {
                NetworkHooks.openScreen(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Test2");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new Test2Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                    }
                }, pos);
            }
        });
    }
    
    private static void executeDay3(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 3 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 3: Something is watching..."), false);
        }
    }
    
    private static void executeDay4(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 4 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 4: The whispers grow louder"), false);
        }
    }
    
    private static void executeDay5(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 5 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 5: Calculator appears"), false);
            CalculatorFindProcedure.execute();
        }
    }
    
    private static void executeDay6(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 6 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 6: Something is wrong"), false);
        }
    }
    
    private static void executeDay7(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 7 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 7: The walls are changing"), false);
        }
    }
    
    private static void executeDay8(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 8 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 8: Meetboy approaches"), false);
            spawnMeetboy(world, player, pos);
        }
    }
    
    private static void executeDay9(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 9 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 9: Desktop changes"), false);
            WorkspacereplaceProcedure.execute();
        }
    }
    
    private static void executeDay10(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 10 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 10: Chunks disappear"), false);
            ChunkDellProcedure.execute(world);
        }
    }
    
    private static void executeDay11(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 11 дня
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 11: Wrong answers"), false);
            WrongProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
        }
    }
    
    private static void executeDay12(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 12 дня - спавним больше сущностей
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 12: More entities spawn"), false);
            
            // Спавним несколько случайных сущностей вокруг игрока
            for (int i = 0; i < 3; i++) {
                spawnRandomEntity(world, player, pos);
            }
        }
    }
    
    private static void executeDay13(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 13 дня - несчастливый день
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 13: Unlucky day"), false);
            
            // Спавним опасных сущностей
            spawnMeetboy(world, player, pos);
            spawnMeetboy(world, player, pos);
        }
    }
    
    private static void executeDay14(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 14 дня - дела идут хуже
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 14: Things get worse"), false);
            
            // Спавним больше опасных сущностей
            spawnRandomEntity(world, player, pos);
            spawnRandomEntity(world, player, pos);
            spawnRandomEntity(world, player, pos);
        }
    }
    
    private static void executeDay15(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 15 дня - середина пути
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 15: Halfway through"), false);
            
            // Спавним особую сущность
            spawnMeetboy(world, player, pos);
            spawnMeetboy(world, player, pos);
            spawnMeetboy(world, player, pos);
        }
    }
    
    private static void executeDay16(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 16 дня - конец приближается
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 16: The end approaches"), false);
            
            // Спавним много сущностей
            for (int i = 0; i < 5; i++) {
                spawnRandomEntity(world, player, pos);
            }
        }
    }
    
    private static void executeDay17(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 17 дня - почти там
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 17: Almost there"), false);
            
            // Спавним много сущностей
            for (int i = 0; i < 6; i++) {
                spawnRandomEntity(world, player, pos);
            }
        }
    }
    
    private static void executeDay18(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 18 дня - финальные предупреждения
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 18: Final warnings"), false);
            
            // Спавним много сущностей
            for (int i = 0; i < 7; i++) {
                spawnRandomEntity(world, player, pos);
            }
        }
    }
    
    private static void executeDay19(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 19 дня - последний день
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 19: Last day"), false);
            
            // Спавним много сущностей
            for (int i = 0; i < 8; i++) {
                spawnRandomEntity(world, player, pos);
            }
        }
    }
    
    private static void executeDay20(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        // Процедура для 20 дня - конец
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Day 20: The end"), false);
            
            // Спавним максимальное количество сущностей
            for (int i = 0; i < 10; i++) {
                spawnRandomEntity(world, player, pos);
            }
        }
    }
    
    /**
     * Спавнит Meetboy рядом с игроком
     */
    private static void spawnMeetboy(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Спавним Meetboy в радиусе 10 блоков от игрока
        int offsetX = RANDOM.nextInt(21) - 10; // -10 до +10
        int offsetZ = RANDOM.nextInt(21) - 10;
        BlockPos spawnPos = pos.offset(offsetX, 0, offsetZ);
        
        // Находим безопасную высоту для спавна
        int spawnY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ());
        BlockPos finalSpawnPos = new BlockPos(spawnPos.getX(), spawnY, spawnPos.getZ());
        
        try {
            EventjarModEntities.MEETBOY.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
        } catch (Exception e) {
            // Silent failure
        }
    }
    
    /**
     * Спавнит случайную сущность рядом с игроком
     */
    private static void spawnRandomEntity(LevelAccessor world, ServerPlayer player, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Спавним в радиусе 15 блоков от игрока
        int offsetX = RANDOM.nextInt(31) - 15; // -15 до +15
        int offsetZ = RANDOM.nextInt(31) - 15;
        BlockPos spawnPos = pos.offset(offsetX, 0, offsetZ);
        
        // Находим безопасную высоту для спавна
        int spawnY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ());
        BlockPos finalSpawnPos = new BlockPos(spawnPos.getX(), spawnY, spawnPos.getZ());
        
        try {
            // Выбираем случайную сущность для спавна
            int entityChoice = RANDOM.nextInt(6);
            switch (entityChoice) {
                case 0:
                    EventjarModEntities.MEETBOY.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                    break;
                case 1:
                    EventjarModEntities.WHOAMI.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                    break;
                case 2:
                    EventjarModEntities.EYES.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                    break;
                case 3:
                    EventjarModEntities.EYE.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                    break;
                case 4:
                    EventjarModEntities.WATCHER.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                    break;
                case 5:
                    EventjarModEntities.SEEKER.get().spawn(serverLevel, finalSpawnPos, MobSpawnType.EVENT);
                    break;
            }
        } catch (Exception e) {
            // Silent failure
        }
    }
}