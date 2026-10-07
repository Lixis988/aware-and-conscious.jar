package net.lixis9.eventjar.mixin;

import javax.annotation.Nullable;

import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenAccessor {

	@Accessor("logoRenderer")
	LogoRenderer eventjar$getLogoRenderer();

	@Nullable
	@Accessor("splash")
	SplashRenderer eventjar$getSplash();

	@Mutable
	@Accessor("splash")
	void eventjar$setSplash(SplashRenderer splash);
}
