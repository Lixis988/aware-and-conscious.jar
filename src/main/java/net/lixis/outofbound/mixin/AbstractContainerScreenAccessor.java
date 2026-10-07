package net.lixis.outofbound.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

	@Accessor("leftPos")
	int outofbound$getLeftPos();

	@Accessor("topPos")
	int outofbound$getTopPos();

	@Accessor("menu")
	AbstractContainerMenu outofbound$getMenu();
}
