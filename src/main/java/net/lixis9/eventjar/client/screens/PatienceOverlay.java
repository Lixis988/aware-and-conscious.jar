package net.lixis9.eventjar.client.screens;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

import net.lixis9.eventjar.network.EventjarModVariables;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class PatienceOverlay {

	private static final int LABEL_X = 4;
	private static final int VALUE_X = 48;
	private static final int Y = 3;

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Player entity = Minecraft.getInstance().player;
		if (entity == null) {
			return;
		}

		double patience = entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new EventjarModVariables.PlayerVariables()).PatienceForRisperidone;
		int display = resolveDisplayLevel(patience);

		event.getGuiGraphics().drawString(Minecraft.getInstance().font,
				Component.translatable("gui.eventjar.patience.label_patience"), LABEL_X, Y, -1, false);
		event.getGuiGraphics().drawString(Minecraft.getInstance().font,
				Component.literal(Integer.toString(display)), VALUE_X, Y, -1, false);
	}

	static int resolveDisplayLevel(double patience) {
		if (patience >= 24000) {
			return 1;
		}
		if (patience >= 21600) {
			return 2;
		}
		if (patience >= 19200) {
			return 3;
		}
		if (patience >= 16800) {
			return 4;
		}
		if (patience >= 14400) {
			return 5;
		}
		if (patience >= 12000) {
			return 6;
		}
		if (patience >= 9600) {
			return 7;
		}
		if (patience >= 7200) {
			return 8;
		}
		if (patience >= 4800) {
			return 9;
		}
		return 10;
	}
}
