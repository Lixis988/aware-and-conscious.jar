package net.lixis9.eventjar;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

import net.lixis9.eventjar.init.EventjarModMobEffects;
import net.lixis9.eventjar.init.EventjarModEntities;
import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;
import java.util.Random;

@Mod.EventBusSubscriber
public class Overdosestart {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event, event.player);
        }
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable TickEvent.PlayerTickEvent event, Entity entity) {
        if (entity == null)
            return;

        if (entity instanceof ServerPlayer serverPlayer) {

            var playerVars = serverPlayer.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables());

            if (playerVars.patience <= -10000) {

                spawnRandomEntities(serverPlayer);

                removeRandomEntities(serverPlayer);

                if (!serverPlayer.hasEffect(EventjarModMobEffects.OVERDOSE.get())) {
                    serverPlayer.addEffect(new MobEffectInstance(
                        EventjarModMobEffects.OVERDOSE.get(),
                        600,
                        1,
                        false, true
                    ));
                }

                playerVars.patience = 0;
                playerVars.syncPlayerVariables(serverPlayer);
            }
        }
    }

    private static void spawnRandomEntities(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos playerPos = player.blockPosition();

        int entityCount = RANDOM.nextInt(3) + 3;
        for (int i = 0; i < entityCount; i++) {

            int offsetX = RANDOM.nextInt(21) - 10;
            int offsetZ = RANDOM.nextInt(21) - 10;
            BlockPos spawnPos = playerPos.offset(offsetX, 0, offsetZ);

            int spawnY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ());
            BlockPos finalSpawnPos = new BlockPos(spawnPos.getX(), spawnY, spawnPos.getZ());

            try {

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

            }
        }
    }

    private static void removeRandomEntities(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos playerPos = player.blockPosition();

        var entities = serverLevel.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,
            new net.minecraft.world.phys.AABB(playerPos).inflate(15));

        int removeCount = Math.min(RANDOM.nextInt(3) + 1, entities.size());
        for (int i = 0; i < removeCount && i < entities.size(); i++) {
            var entity = entities.get(RANDOM.nextInt(entities.size()));
            if (entity != player && !entity.isRemoved()) {
                entity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        }
    }
}
