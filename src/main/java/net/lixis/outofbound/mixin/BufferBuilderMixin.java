package net.lixis.outofbound.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.lixis.outofbound.feature.corruption.render.MemoryCorruptionGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {

	@ModifyArg(
			method = "putFloat(IF)V",
			at = @At(value = "INVOKE", target = "Ljava/nio/ByteBuffer;putFloat(IF)Ljava/nio/ByteBuffer;"),
			index = 1)
	private float outofbound$corruptPutFloat(float value) {
		if (!MemoryCorruptionGate.isEffectivelyEnabled()) {
			return value;
		}
		VertexFormatElement currentElement = ((BufferBuilderAccessor) (Object) this).outofbound$getCurrentElement();
		if (currentElement == null) {
			return value;
		}
		if (currentElement.getUsage() == VertexFormatElement.Usage.UV && currentElement.getType() == VertexFormatElement.Type.FLOAT) {
			return maybeCorruptUv(value);
		}
		if (currentElement.isPosition()) {
			return maybeCorruptPosition(value);
		}
		return value;
	}

	@ModifyVariable(method = "vertex(FFFFFFFFFIIFFF)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float outofbound$corruptBulkX(float x) {
		return maybeCorruptPosition(x);
	}

	@ModifyVariable(method = "vertex(FFFFFFFFFIIFFF)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
	private float outofbound$corruptBulkY(float y) {
		return maybeCorruptPosition(y);
	}

	@ModifyVariable(method = "vertex(FFFFFFFFFIIFFF)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
	private float outofbound$corruptBulkZ(float z) {
		return maybeCorruptPosition(z);
	}

	@ModifyVariable(method = "vertex(FFFFFFFFFIIFFF)V", at = @At("HEAD"), ordinal = 7, argsOnly = true)
	private float outofbound$corruptBulkU(float u) {
		return maybeCorruptUv(u);
	}

	@ModifyVariable(method = "vertex(FFFFFFFFFIIFFF)V", at = @At("HEAD"), ordinal = 8, argsOnly = true)
	private float outofbound$corruptBulkV(float v) {
		return maybeCorruptUv(v);
	}

	@Inject(method = "endVertex", at = @At("RETURN"))
	private void outofbound$clearVertexRollEndVertex(CallbackInfo ci) {
		MemoryCorruptionGate.clearVertexRoll();
	}

	@Inject(method = "vertex(FFFFFFFFFIIFFF)V", at = @At("RETURN"))
	private void outofbound$clearVertexRollBulk(CallbackInfo ci) {
		MemoryCorruptionGate.clearVertexRoll();
	}

	private static float maybeCorruptUv(float value) {
		MemoryCorruptionGate.CorruptionType type = MemoryCorruptionGate.getOrRollType();
		if (type == MemoryCorruptionGate.CorruptionType.UV) {
			return MemoryCorruptionGate.corruptUv(value);
		}
		return value;
	}

	private static float maybeCorruptPosition(float value) {
		MemoryCorruptionGate.CorruptionType type = MemoryCorruptionGate.getOrRollType();
		if (type == MemoryCorruptionGate.CorruptionType.VERTEX) {
			return MemoryCorruptionGate.corruptVertexCoord(value);
		}
		return value;
	}
}
