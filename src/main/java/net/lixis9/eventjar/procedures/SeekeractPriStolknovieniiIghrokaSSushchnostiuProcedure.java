package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.entity.SeekeractEntity;
import net.minecraft.world.entity.player.Player;

@Deprecated
public class SeekeractPriStolknovieniiIghrokaSSushchnostiuProcedure {
	public static void execute(SeekeractEntity seeker, Player player) {
		SeekerCatchProcedure.execute(seeker, player);
	}

	public static void execute(net.minecraft.world.level.LevelAccessor world, double x, double y, double z) {

	}
}
