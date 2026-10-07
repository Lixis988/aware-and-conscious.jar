package net.lixis.outofbound.client.model;

import net.lixis.outofbound.client.BoundedcowGlitchEffect;
import net.lixis.outofbound.entity.BoundedcowEntity;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlitchCowModel extends CowModel<BoundedcowEntity> {

	private float glitchTime;
	private int glitchSeed;

	private final float[] savedYRot = new float[6];
	private final float[] savedZRot = new float[6];

	public GlitchCowModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(BoundedcowEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.glitchTime = ageInTicks;
		this.glitchSeed = entity.getId();
	}

	@Override
	public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		applyPartGlitch();
		try {
			super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		} finally {
			restorePartGlitch();
		}
	}

	private void applyPartGlitch() {
		applyPartGlitch(this.head, 0);
		applyPartGlitch(this.body, 1);
		applyPartGlitch(this.rightFrontLeg, 2);
		applyPartGlitch(this.leftFrontLeg, 3);
		applyPartGlitch(this.rightHindLeg, 4);
		applyPartGlitch(this.leftHindLeg, 5);
	}

	private void applyPartGlitch(ModelPart part, int index) {
		savedYRot[index] = part.yRot;
		savedZRot[index] = part.zRot;
		part.yRot += BoundedcowGlitchEffect.glitchAngleRadians(glitchTime * (1.0F + index * 0.11F), glitchSeed, index + 10) * 0.45F;
		part.zRot += BoundedcowGlitchEffect.glitchAngleRadians(glitchTime * (1.3F + index * 0.09F), glitchSeed + 7, index + 20) * 0.65F;
	}

	private void restorePartGlitch() {
		restorePartGlitch(this.head, 0);
		restorePartGlitch(this.body, 1);
		restorePartGlitch(this.rightFrontLeg, 2);
		restorePartGlitch(this.leftFrontLeg, 3);
		restorePartGlitch(this.rightHindLeg, 4);
		restorePartGlitch(this.leftHindLeg, 5);
	}

	private void restorePartGlitch(ModelPart part, int index) {
		part.yRot = savedYRot[index];
		part.zRot = savedZRot[index];
	}
}
