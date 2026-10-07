package net.lixis9.eventjar.client.model;

import net.lixis9.eventjar.entity.MimicTendrilEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class MimicTendrilModel extends HumanoidModel<MimicTendrilEntity> {

	public MimicTendrilModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(MimicTendrilEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		this.head.xScale = 0.55F;
		this.head.yScale = 1.4F;
		this.head.zScale = 0.55F;
		this.hat.visible = false;

		this.body.xScale = 0.35F;
		this.body.yScale = 1.8F;
		this.body.zScale = 0.35F;
		this.body.zRot = Mth.sin(ageInTicks * 0.15F) * 0.12F;

		this.leftArm.xScale = 0.25F;
		this.leftArm.yScale = 2.6F;
		this.leftArm.zScale = 0.25F;
		this.leftArm.zRot = 0.4F + Mth.sin(ageInTicks * 0.3F) * 0.2F;

		this.rightArm.xScale = 0.25F;
		this.rightArm.yScale = 2.8F;
		this.rightArm.zScale = 0.25F;
		this.rightArm.zRot = -0.45F + Mth.cos(ageInTicks * 0.28F) * 0.18F;

		this.leftLeg.xScale = 0.3F;
		this.leftLeg.yScale = 2.4F;
		this.leftLeg.zScale = 0.3F;

		this.rightLeg.xScale = 0.3F;
		this.rightLeg.yScale = 2.5F;
		this.rightLeg.zScale = 0.3F;
	}
}
