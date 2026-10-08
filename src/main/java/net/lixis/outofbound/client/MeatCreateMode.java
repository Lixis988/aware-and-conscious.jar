package net.lixis.outofbound.client;

import net.lixis.outofbound.world.MeatGameMode;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;

public enum MeatCreateMode {
	SURVIVAL(WorldCreationUiState.SelectedGameMode.SURVIVAL),
	HARDCORE(WorldCreationUiState.SelectedGameMode.HARDCORE),
	CREATIVE(WorldCreationUiState.SelectedGameMode.CREATIVE),
	MEAT(WorldCreationUiState.SelectedGameMode.SURVIVAL);

	private final WorldCreationUiState.SelectedGameMode vanilla;

	MeatCreateMode(WorldCreationUiState.SelectedGameMode vanilla) {
		this.vanilla = vanilla;
	}

	public Component displayName() {
		if (this == MEAT) {
			return Component.translatable("selectWorld.gameMode.meat");
		}
		return vanilla.displayName;
	}

	public Component info() {
		if (this == MEAT) {
			return Component.translatable("selectWorld.gameMode.meat.info");
		}
		return vanilla.getInfo();
	}

	public void apply(WorldCreationUiState state) {
		MeatGameMode.setPendingCreate(this == MEAT);
		state.setGameMode(vanilla);
	}

	public static MeatCreateMode fromVanilla(WorldCreationUiState.SelectedGameMode mode) {
		if (mode == WorldCreationUiState.SelectedGameMode.HARDCORE) {
			return HARDCORE;
		}
		if (mode == WorldCreationUiState.SelectedGameMode.CREATIVE) {
			return CREATIVE;
		}
		return SURVIVAL;
	}
}
