package net.lixis9.eventjar.client.model;

import net.lixis9.eventjar.entity.MeetboyGlitchEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class MeetboyGlitchModel extends HumanoidModel<MeetboyGlitchEntity> {

	public MeetboyGlitchModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(MeetboyGlitchEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		this.head.visible = false;
		this.hat.visible = false;

		int id = entity.getId();
		float t = ageInTicks;

		this.body.zRot = 0.22F + Mth.sin(t * 0.09F + id) * 0.14F;
		this.body.xRot = Mth.sin(t * 0.07F + id * 0.3F) * 0.08F;
		this.body.yRot = Mth.sin(t * 0.11F) * 0.06F;
		this.body.xScale = 0.92F;
		this.body.yScale = 1.12F;
		this.body.zScale = 0.85F;

		this.leftArm.xScale = 0.55F;
		this.leftArm.yScale = 1.45F;
		this.leftArm.zScale = 0.7F;
		this.leftArm.zRot = 0.55F + Mth.sin(t * 0.25F + id) * 0.2F;
		this.leftArm.xRot += Mth.sin(t * 0.4F) * 0.15F;

		this.rightArm.xScale = 1.55F;
		this.rightArm.yScale = 0.7F;
		this.rightArm.zScale = 1.2F;
		this.rightArm.zRot = -0.7F + Mth.cos(t * 0.18F) * 0.12F;
		this.rightArm.xRot -= 0.35F;

		this.leftLeg.yScale = 1.5F;
		this.leftLeg.xScale = 0.6F;
		this.leftLeg.zScale = 0.75F;
		this.leftLeg.zRot = 0.18F;

		this.rightLeg.yScale = 0.8F;
		this.rightLeg.xScale = 1.15F;
		this.rightLeg.zRot = -0.12F + Mth.sin(t * 0.15F) * 0.08F;

		if ((entity.tickCount + id) % 47 < 3) {
			this.body.yRot += (entity.getRandom().nextFloat() - 0.5F) * 1.2F;
			this.leftArm.xRot = entity.getRandom().nextFloat() * ((float) Math.PI * 2.0F);
			this.rightArm.zRot = (entity.getRandom().nextFloat() - 0.5F) * 2.0F;
			this.leftLeg.xRot = (entity.getRandom().nextFloat() - 0.5F) * 1.5F;
		}
	}
}
