package net.lixis9.eventjar.procedures;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LookatyouProcedure {
    public static void execute(Level world, Player player) {

        double radius = 256.0D;

        for (Animal animal : world.getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(radius))) {

            Mob mob = (Mob) animal;

            mob.getLookControl().setLookAt(player, 360.0F, 360.0F);
        }
    }
}
