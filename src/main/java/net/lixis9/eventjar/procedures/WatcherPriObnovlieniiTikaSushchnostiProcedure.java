package net.lixis9.eventjar.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.lixis9.eventjar.network.EventjarModVariables;

public class WatcherPriObnovlieniiTikaSushchnostiProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null || !(entity instanceof net.minecraft.world.entity.Mob)) return;
        // Ищем ближайшего игрока в радиусе 64 блоков
        Player player = world.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 64, false);
        if (player == null) return;

        // Вектор взгляда игрока из переменных мира
        Vec3 look = new Vec3(
            EventjarModVariables.WorldVariables.get(world).X,
            EventjarModVariables.WorldVariables.get(world).Y,
            EventjarModVariables.WorldVariables.get(world).Z
        );

        // Направление от игрока к сущности
        Vec3 toWatcher = entity.position().subtract(player.position()).normalize();

        // Если сущность в пределах прямого взгляда (dot ~ cos θ; >0.995 ≈ 5° отклонения)
        double dot = look.dot(toWatcher); // dot-произведение Vec3 :contentReference[oaicite:0]{index=0}
        if (dot > 0.995 && player.hasLineOfSight(entity)) {  // проверяем наличие прямой видимости :contentReference[oaicite:1]{index=1}
            if (!entity.level().isClientSide()) {
                entity.discard();
            }
        }
    }
}
