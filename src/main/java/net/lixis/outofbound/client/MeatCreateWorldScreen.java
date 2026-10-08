package net.lixis.outofbound.client;

import net.lixis.outofbound.world.MeatGameMode;
import net.lixis9.eventjar.EventjarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EventjarMod.MODID, value = Dist.CLIENT)
public final class MeatCreateWorldScreen {

	private static CreateWorldScreen bound;
	private static CycleButton<MeatCreateMode> meatButton;
	private static Host host;
	private static boolean selectedMeat;

	private MeatCreateWorldScreen() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof CreateWorldScreen create && host != null) {
			install(create, host);
			return;
		}
		if (!(screen instanceof CreateWorldScreen)) {
			bound = null;
			meatButton = null;
			host = null;
		}
	}

	public static void confirmPending() {
		boolean meat = selectedMeat || (meatButton != null && meatButton.getValue() == MeatCreateMode.MEAT);
		MeatGameMode.setPendingCreate(meat);
		if (meat) {
			WorldProgressClientState.markMeatGameModeLocal();
		}
	}

	public static void install(CreateWorldScreen screen, Host widgetHost) {
		host = widgetHost;
		if (bound != screen) {
			bound = screen;
			meatButton = null;
			selectedMeat = false;
			MeatGameMode.setPendingCreate(false);
			screen.getUiState().addListener(MeatCreateWorldScreen::syncButton);
		}

		CycleButton<?> vanilla = findVanillaGameMode(screen);
		if (vanilla != null) {
			replaceVanilla(screen, vanilla, widgetHost);
			return;
		}

		if (meatButton != null && screen.children().contains(meatButton)) {
			meatButton.visible = true;
			meatButton.active = !screen.getUiState().isDebug();
		}
	}

	private static void replaceVanilla(CreateWorldScreen screen, CycleButton<?> vanilla, Host widgetHost) {
		if (meatButton != null && screen.children().contains(meatButton)) {
			meatButton.setX(vanilla.getX());
			meatButton.setY(vanilla.getY());
			meatButton.setWidth(vanilla.getWidth());
			meatButton.setHeight(vanilla.getHeight());
			widgetHost.remove(vanilla);
			return;
		}
		WorldCreationUiState uiState = screen.getUiState();
		MeatCreateMode initial = selectedMeat || MeatGameMode.isPendingCreate()
				? MeatCreateMode.MEAT
				: MeatCreateMode.fromVanilla(uiState.getGameMode());
		CycleButton<MeatCreateMode> replacement = CycleButton.builder(MeatCreateMode::displayName)
				.withValues(MeatCreateMode.values())
				.withTooltip(mode -> Tooltip.create(mode.info()))
				.withInitialValue(initial)
				.create(vanilla.getX(), vanilla.getY(), vanilla.getWidth(), vanilla.getHeight(),
						Component.translatable("selectWorld.gameMode"),
						(ignored, mode) -> {
							selectedMeat = mode == MeatCreateMode.MEAT;
							mode.apply(screen.getUiState());
						});
		replacement.active = vanilla.active;
		replacement.visible = vanilla.visible;
		widgetHost.remove(vanilla);
		widgetHost.add(replacement);
		meatButton = replacement;
		selectedMeat = initial == MeatCreateMode.MEAT;
	}

	private static void syncButton(WorldCreationUiState ui) {
		if (meatButton == null) {
			return;
		}
		if (!selectedMeat && !MeatGameMode.isPendingCreate()) {
			meatButton.setValue(MeatCreateMode.fromVanilla(ui.getGameMode()));
		} else if (selectedMeat) {
			meatButton.setValue(MeatCreateMode.MEAT);
			MeatGameMode.setPendingCreate(true);
		}
		meatButton.active = !ui.isDebug();
		meatButton.visible = !ui.isDebug();
	}

	private static CycleButton<?> findVanillaGameMode(CreateWorldScreen screen) {
		for (GuiEventListener child : List.copyOf(screen.children())) {
			if (child instanceof CycleButton<?> cycle && cycle.getValue() instanceof WorldCreationUiState.SelectedGameMode) {
				return cycle;
			}
		}
		return null;
	}

	public interface Host {
		void remove(GuiEventListener listener);

		<T extends AbstractWidget> T add(T widget);
	}
}
