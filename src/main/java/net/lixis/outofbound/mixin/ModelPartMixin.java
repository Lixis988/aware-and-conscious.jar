package net.lixis.outofbound.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lixis.outofbound.feature.corruption.render.MemoryCorruptionGate;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public class ModelPartMixin {

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
			at = @At("HEAD"))
	private void outofbound$corruptModelScale(PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
		if (!MemoryCorruptionGate.shouldCorruptMatrix()) {
			return;
		}
		float sx = MemoryCorruptionGate.corruptMatrixScale(1.0F);
		float sy = MemoryCorruptionGate.corruptMatrixScale(1.0F);
		float sz = MemoryCorruptionGate.corruptMatrixScale(1.0F);
		poseStack.scale(sx, sy, sz);
	}
}
