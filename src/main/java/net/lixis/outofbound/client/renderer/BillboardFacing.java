package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public final class BillboardFacing {

	private BillboardFacing() {
	}

	public static void yawOnly(PoseStack poseStack, EntityRenderDispatcher dispatcher) {
		if (dispatcher.camera == null) {
			return;
		}
		poseStack.mulPose(Axis.YP.rotationDegrees(-dispatcher.camera.getYRot()));
	}
}
