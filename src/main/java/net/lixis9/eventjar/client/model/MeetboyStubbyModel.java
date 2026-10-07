package net.lixis9.eventjar.client.model;

import net.lixis9.eventjar.entity.MeetboyStubbyEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class MeetboyStubbyModel extends HumanoidModel<MeetboyStubbyEntity> {

	public MeetboyStubbyModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(MeetboyStubbyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		this.head.visible = false;
		this.hat.visible = false;

		int id = entity.getId();
		float t = ageInTicks;

		this.body.xScale = 1.25F;
		this.body.yScale = 0.72F;
		this.body.zScale = 1.15F;
		this.body.zRot = 0.15F + Mth.sin(t * 0.1F + id) * 0.08F;

		this.leftArm.xScale = 1.35F;
		this.leftArm.yScale = 0.42F;
		this.leftArm.zScale = 1.25F;
		this.leftArm.zRot = 0.65F + Mth.sin(t * 0.22F + id) * 0.15F;

		this.rightArm.xScale = 1.45F;
		this.rightArm.yScale = 0.38F;
		this.rightArm.zScale = 1.3F;
		this.rightArm.zRot = -0.7F + Mth.cos(t * 0.2F) * 0.12F;
		this.rightArm.xRot -= 0.15F;

		this.leftLeg.xScale = 1.4F;
		this.leftLeg.yScale = 0.48F;
		this.leftLeg.zScale = 1.2F;
		this.leftLeg.zRot = 0.2F;

		this.rightLeg.xScale = 1.5F;
		this.rightLeg.yScale = 0.44F;
		this.rightLeg.zScale = 1.25F;
		this.rightLeg.zRot = -0.18F + Mth.sin(t * 0.14F) * 0.05F;

		if ((entity.tickCount + id) % 43 < 2) {
			this.body.yRot += (entity.getRandom().nextFloat() - 0.5F) * 0.8F;
			this.leftLeg.xRot = (entity.getRandom().nextFloat() - 0.5F) * 1.2F;
		}
	}
}
