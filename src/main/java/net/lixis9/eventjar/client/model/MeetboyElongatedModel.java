package net.lixis9.eventjar.client.model;

import net.lixis9.eventjar.entity.MeetboyElongatedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class MeetboyElongatedModel extends HumanoidModel<MeetboyElongatedEntity> {

	public MeetboyElongatedModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(MeetboyElongatedEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		this.head.visible = false;
		this.hat.visible = false;

		int id = entity.getId();
		float t = ageInTicks;

		this.body.xScale = 0.78F;
		this.body.yScale = 1.35F;
		this.body.zScale = 0.72F;
		this.body.zRot = Mth.sin(t * 0.08F + id) * 0.1F;

		this.leftArm.xScale = 0.45F;
		this.leftArm.yScale = 2.15F;
		this.leftArm.zScale = 0.45F;
		this.leftArm.zRot = 0.25F + Mth.sin(t * 0.2F + id) * 0.12F;

		this.rightArm.xScale = 0.45F;
		this.rightArm.yScale = 2.25F;
		this.rightArm.zScale = 0.45F;
		this.rightArm.zRot = -0.28F + Mth.cos(t * 0.18F) * 0.1F;
		this.rightArm.xRot -= 0.2F;

		this.leftLeg.xScale = 0.5F;
		this.leftLeg.yScale = 1.95F;
		this.leftLeg.zScale = 0.55F;
		this.leftLeg.zRot = 0.08F;

		this.rightLeg.xScale = 0.5F;
		this.rightLeg.yScale = 2.05F;
		this.rightLeg.zScale = 0.55F;
		this.rightLeg.zRot = -0.1F + Mth.sin(t * 0.12F) * 0.06F;

		if ((entity.tickCount + id) % 53 < 2) {
			this.leftArm.xRot = entity.getRandom().nextFloat() * ((float) Math.PI);
			this.rightArm.xRot = -entity.getRandom().nextFloat() * ((float) Math.PI);
		}
	}
}
