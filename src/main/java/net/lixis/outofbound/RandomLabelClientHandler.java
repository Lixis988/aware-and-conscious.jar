package net.lixis.outofbound;

import net.lixis9.eventjar.DarknessConfig;
import net.lixis9.eventjar.client.AacTextChaosGuard;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class RandomLabelClientHandler {

	@SubscribeEvent
	public static void onScreenInit(ScreenEvent.Init.Post event) {
		tryRandomize(event.getScreen());
	}

	@SubscribeEvent
	public static void onScreenOpen(ScreenEvent.Opening event) {
		tryRandomize(event.getNewScreen());
	}

	public static void tryRandomize(Screen screen) {
		if (!DarknessConfig.RANDOM_LABELS) {
			return;
		}
		if (AacTextChaosGuard.shouldSkip(screen)) {
			return;
		}
		randomizeWidgetLabels(screen);
	}

	public static void randomizeWidgetLabels(Screen screen) {
		for (Renderable renderable : screen.renderables) {
			if (renderable instanceof EditBox || renderable instanceof PlainTextButton) {
				continue;
			}
			if (renderable instanceof AbstractWidget widget) {
				widget.setMessage(Component.literal(MenuRandomLabels.next()));
			}
		}
	}

	public static void refreshCurrentScreen(net.minecraft.client.Minecraft minecraft) {
		Screen screen = minecraft.screen;
		if (AacTextChaosGuard.shouldSkip(screen)) {
			return;
		}
		if (DarknessConfig.RANDOM_LABELS) {
			randomizeWidgetLabels(screen);
		} else {
			screen.init(minecraft, screen.width, screen.height);
		}
	}
}
