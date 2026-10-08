package net.lixis.outofbound.mixin;

import net.lixis.outofbound.client.MeatCreateWorldScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

	protected CreateWorldScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void outofbound$installMeatGameMode(CallbackInfo ci) {
		outofbound$installMeat();
	}

	@Inject(method = "repositionElements", at = @At("RETURN"))
	private void outofbound$repositionMeatGameMode(CallbackInfo ci) {
		outofbound$installMeat();
	}

	@Inject(method = "onCreate", at = @At("HEAD"))
	private void outofbound$stampMeatGameMode(CallbackInfo ci) {
		MeatCreateWorldScreen.confirmPending();
	}

	@Unique
	private void outofbound$installMeat() {
		MeatCreateWorldScreen.install((CreateWorldScreen) (Object) this, new MeatCreateWorldScreen.Host() {
			@Override
			public void remove(GuiEventListener listener) {
				removeWidget(listener);
			}

			@Override
			public <T extends AbstractWidget> T add(T widget) {
				return addRenderableWidget(widget);
			}
		});
	}
}
