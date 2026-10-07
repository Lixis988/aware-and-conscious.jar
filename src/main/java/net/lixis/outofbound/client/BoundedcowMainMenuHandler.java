package net.lixis.outofbound.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class BoundedcowMainMenuHandler {

	private BoundedcowMainMenuHandler() {
	}

	public static void returnToMainMenu() {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(() -> {
			BoundedcowWindowShaker.reset();

			if (minecraft.getConnection() != null) {
				minecraft.getConnection().getConnection().disconnect(Component.literal("Unable to access the file winload.exe"));
				return;
			}

			minecraft.clearLevel();
			minecraft.setScreen(new TitleScreen());
		});
	}
}
