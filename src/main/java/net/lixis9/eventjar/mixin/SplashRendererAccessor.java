package net.lixis9.eventjar.mixin;

import net.minecraft.client.gui.components.SplashRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SplashRenderer.class)
public interface SplashRendererAccessor {

	@Mutable
	@Accessor("splash")
	void eventjar$setSplash(String splash);
}
