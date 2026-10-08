package net.lixis9.eventjar.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicManagerMixin {

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void eventjar$musicTick(CallbackInfo ci) {
		if (Minecraft.getInstance().screen instanceof TitleScreen) {
			ci.cancel();
		}
	}

	@Inject(method = "startPlaying", at = @At("HEAD"), cancellable = true)
	private void eventjar$blockVanillaMusic(Music music, CallbackInfo ci) {
		ci.cancel();
	}
}
