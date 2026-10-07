package net.lixis.outofbound.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	private static final String WINDOW_TITLE = "Event.jar";

	@Inject(method = "updateTitle", at = @At("HEAD"), cancellable = true)
	private void outofbound$customWindowTitle(CallbackInfo ci) {
		Minecraft minecraft = (Minecraft) (Object) this;
		minecraft.getWindow().setTitle(WINDOW_TITLE);
		ci.cancel();
	}
}
