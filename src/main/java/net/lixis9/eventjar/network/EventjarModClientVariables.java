package net.lixis9.eventjar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class EventjarModClientVariables {
	private EventjarModClientVariables() {
	}

	public static void applyPlayerSync(EventjarModVariables.PlayerVariables data) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		EventjarModVariables.PlayerVariables variables = player.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables());
		variables.patience = data.patience;
		variables.PatienceForRisperidone = data.PatienceForRisperidone;
		variables.scissorsnumber = data.scissorsnumber;
		variables.reputation = data.reputation;
	}
}
