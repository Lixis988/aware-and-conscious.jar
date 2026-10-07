package net.lixis.outofbound.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lixis.outofbound.client.BoundedcowGlitchEffect;
import net.lixis.outofbound.entity.CaveUndefiendEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CaveUndefiendModel extends Modelunknown<CaveUndefiendEntity> {

	private static final String IDLE_CLIP = BedrockAnimationPlayer.clip("idle");
	private static final String RUN_CLIP = BedrockAnimationPlayer.clip("new_run");
	private static final float PART_GLITCH_Y = 0.45F;
	private static final float PART_GLITCH_Z = 0.65F;

	private final Map<String, ModelPart> bones;
	private final Map<String, BedrockAnimationPlayer.BonePose> defaultPoses;
	private final ModelPart[] glitchParts;
	private final float[] savedYRot;
	private final float[] savedZRot;

	private int glitchSeed;
	private boolean distortActive;

	public CaveUndefiendModel(ModelPart root) {
		super(root);
		this.bones = BedrockAnimationPlayer.buildBoneMap(this);
		for (ModelPart part : bones.values()) {
			part.resetPose();
		}
		this.defaultPoses = BedrockAnimationPlayer.captureDefaultPose(bones);
		this.glitchParts = new ModelPart[] {
				this.head, this.lowerbody, this.upperbody,
				this.upperleftleg, this.upperrightleg,
				this.lowerleftleg, this.lowerrightleg,
				this.leftupperarm, this.leftlowerarm,
				this.rightupperarm, this.rightlowerarm
		};
		this.savedYRot = new float[this.glitchParts.length];
		this.savedZRot = new float[this.glitchParts.length];
	}

	@Override
	public void setupAnim(CaveUndefiendEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		int snapSeed = entity.getGlitchSnapSeed();
		this.glitchSeed = snapSeed != 0 ? snapSeed : entity.getId();
		this.distortActive = snapSeed != 0;

		for (ModelPart part : bones.values()) {
			part.resetPose();
		}

		boolean chasing = entity.isActivated() || entity.getTarget() != null;
		boolean moving = limbSwingAmount > 0.01F || entity.getDeltaMovement().horizontalDistanceSqr() > 0.0004D;
		boolean running = chasing || moving;

		BedrockAnimationPlayer player = BedrockAnimationPlayer.get();
		if (running) {
			float speed = chasing ? 2.4F : 1.4F;
			float animTime = ageInTicks * 0.05F * speed;
			if (player.hasClip(RUN_CLIP)) {
				player.apply(RUN_CLIP, bones, defaultPoses, animTime);
			} else if (player.hasClip(BedrockAnimationPlayer.clip("run"))) {
				player.apply(BedrockAnimationPlayer.clip("run"), bones, defaultPoses, animTime);
			}
		} else if (player.hasClip(IDLE_CLIP)) {
			player.apply(IDLE_CLIP, bones, defaultPoses, ageInTicks * 0.05F);
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (distortActive) {
			applyPartGlitch();
		}
		try {
			super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		} finally {
			if (distortActive) {
				restorePartGlitch();
			}
		}
	}

	private void applyPartGlitch() {
		for (int index = 0; index < glitchParts.length; index++) {
			ModelPart part = glitchParts[index];
			savedYRot[index] = part.yRot;
			savedZRot[index] = part.zRot;
			part.yRot += BoundedcowGlitchEffect.glitchAngleRadians(1.0F + index * 0.11F, glitchSeed, index + 10) * PART_GLITCH_Y;
			part.zRot += BoundedcowGlitchEffect.glitchAngleRadians(1.3F + index * 0.09F, glitchSeed + 7, index + 20) * PART_GLITCH_Z;
		}
	}

	private void restorePartGlitch() {
		for (int index = 0; index < glitchParts.length; index++) {
			ModelPart part = glitchParts[index];
			part.yRot = savedYRot[index];
			part.zRot = savedZRot[index];
		}
	}
}
