package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LookatyouProcedure {
    public static void execute(Level world, Player player) {
        // Радиус поиска животных (например, 256 блоков)
        double radius = 256.0D;
        // Получаем всех животных в области вокруг игрока
        for (Animal animal : world.getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(radius))) {
            // Animal уже наследует Mob, поэтому просто приводим тип
            Mob mob = (Mob) animal;
            // Устанавливаем направление взгляда на игрока,
            // используя максимально большие углы, чтобы животное мгновенно ориентировалось
            mob.getLookControl().setLookAt(player, 360.0F, 360.0F);
        }
    }
}
