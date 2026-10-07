package net.lixis.outofbound.mixin;

import net.lixis.outofbound.client.MeatTextureSwap;
import net.lixis.outofbound.client.renderer.VanillaBillboardSprites;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@Inject(method = "onResourceManagerReload", at = @At("HEAD"))
	private void outofbound$clearBillboardCache(ResourceManager resourceManager, CallbackInfo ci) {
		VanillaBillboardSprites.clearCaches();
		MeatTextureSwap.clearCache();
	}

	@Inject(method = "onResourceManagerReload", at = @At("RETURN"))
	private void outofbound$wrapVanillaMobs(ResourceManager resourceManager, CallbackInfo ci) {
		EntityRenderDispatcher self = (EntityRenderDispatcher) (Object) this;
		VanillaBillboardSprites.wrapRemainingVanillaMobs(self,
				((EntityRenderDispatcherAccessor) (Object) this).outofbound$getRenderers());
	}

	@Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
	private void outofbound$billboardRenderer(Entity entity, CallbackInfoReturnable<EntityRenderer<?>> cir) {
		EntityRenderer<?> billboard = VanillaBillboardSprites.rendererOf(entity.getType());
		if (billboard != null) {
			cir.setReturnValue(billboard);
		}
	}
}
