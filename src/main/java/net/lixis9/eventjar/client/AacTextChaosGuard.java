package net.lixis9.eventjar.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class AacTextChaosGuard {

	private AacTextChaosGuard() {
	}

	public static boolean shouldSkip(Screen screen) {
		if (screen == null) {
			return true;
		}
		if (isModScreen(screen)) {
			return true;
		}

		return screen instanceof AbstractSignEditScreen
				|| screen instanceof BookEditScreen
				|| screen instanceof BookViewScreen;
	}

	private static boolean isModScreen(Screen screen) {
		Package pkg = screen.getClass().getPackage();
		if (pkg == null) {
			return false;
		}
		String name = pkg.getName();
		return name.startsWith("net.lixis9.eventjar")
				|| name.startsWith("net.lixis.outofbound");
	}
}
