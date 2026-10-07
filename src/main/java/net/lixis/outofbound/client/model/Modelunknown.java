package net.lixis.outofbound.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class Modelunknown<T extends Entity> extends EntityModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("outofbound", "modelunknown"), "main");
	public final ModelPart base;
	public final ModelPart lowerbody;
	public final ModelPart upperbody;
	public final ModelPart neck;
	public final ModelPart head;
	public final ModelPart jaw;
	public final ModelPart rightupperarm;
	public final ModelPart rightlowerarm;
	public final ModelPart righthand;
	public final ModelPart leftupperarm;
	public final ModelPart leftlowerarm;
	public final ModelPart lowerlowerLeftArm;
	public final ModelPart lefthand;
	public final ModelPart upperleftleg;
	public final ModelPart lowerleftleg;
	public final ModelPart leftfoot;
	public final ModelPart upperrightleg;
	public final ModelPart lowerrightleg;
	public final ModelPart rightfoot;

	public Modelunknown(ModelPart root) {
		this.base = root.getChild("base");
		this.lowerbody = this.base.getChild("lowerbody");
		this.upperbody = this.lowerbody.getChild("upperbody");
		this.neck = this.upperbody.getChild("neck");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.rightupperarm = this.upperbody.getChild("rightupperarm");
		this.rightlowerarm = this.rightupperarm.getChild("rightlowerarm");
		this.righthand = this.rightlowerarm.getChild("righthand");
		this.leftupperarm = this.upperbody.getChild("leftupperarm");
		this.leftlowerarm = this.leftupperarm.getChild("leftlowerarm");
		this.lowerlowerLeftArm = this.leftlowerarm.getChild("lowerlowerLeftArm");
		this.lefthand = this.lowerlowerLeftArm.getChild("lefthand");
		this.upperleftleg = this.lowerbody.getChild("upperleftleg");
		this.lowerleftleg = this.upperleftleg.getChild("lowerleftleg");
		this.leftfoot = this.lowerleftleg.getChild("leftfoot");
		this.upperrightleg = this.lowerbody.getChild("upperrightleg");
		this.lowerrightleg = this.upperrightleg.getChild("lowerrightleg");
		this.rightfoot = this.lowerrightleg.getChild("rightfoot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition lowerbody = base.addOrReplaceChild("lowerbody", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -15.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
		PartDefinition lowerbody_r1 = lowerbody.addOrReplaceChild("lowerbody_r1", CubeListBuilder.create().texOffs(6, 16).mirror().addBox(-0.6938F, -5.2354F, -2.0F, 2.0F, 3.4F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2555F, -4.3098F, 0.0F, 0.0F, 0.0F, 0.1047F));
		PartDefinition lowerbody_r2 = lowerbody.addOrReplaceChild("lowerbody_r2",
				CubeListBuilder.create().texOffs(1, 8).mirror().addBox(-1.0F, -2.0F, -0.6F, 2.0F, 4.1F, 1.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(36, 41).addBox(-1.0F, 2.1F, -0.6F, 2.0F, 2.075F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -3.1776F, -1.9715F, -3.0194F, 0.0F, 3.1416F));
		PartDefinition lowerbody_r3 = lowerbody.addOrReplaceChild("lowerbody_r3", CubeListBuilder.create().texOffs(41, 4).addBox(-1.0F, -0.9875F, -0.6F, 2.0F, 2.075F, 1.3F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, 0.1097F, -1.2563F, -0.3491F, 0.0F, 0.0F));
		PartDefinition lowerbody_r4 = lowerbody.addOrReplaceChild("lowerbody_r4", CubeListBuilder.create().texOffs(34, 39).addBox(-1.0F, -0.9875F, -0.5F, 2.0F, 2.075F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, 0.1097F, 1.2563F, 0.3491F, 0.0F, 0.0F));
		PartDefinition lowerbody_r5 = lowerbody.addOrReplaceChild("lowerbody_r5",
				CubeListBuilder.create().texOffs(22, 11).addBox(-1.0F, -2.5625F, -0.5F, 2.0F, 4.55F, 1.0F, new CubeDeformation(0.0F)).texOffs(32, 17).addBox(-1.0F, 1.9875F, -0.5F, 2.0F, 2.2F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -3.1653F, 1.9736F, 0.0873F, 0.0F, 0.0F));
		PartDefinition lowerbody_r6 = lowerbody.addOrReplaceChild("lowerbody_r6", CubeListBuilder.create().texOffs(26, 8).addBox(-0.875F, -0.65F, -2.0F, 2.0F, 2.3F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.6868F, -1.8858F, 0.0F, 0.0F, 0.0F, 0.096F));
		PartDefinition lowerbody_r7 = lowerbody.addOrReplaceChild("lowerbody_r7", CubeListBuilder.create().texOffs(20, 33).addBox(0.3F, -0.25F, -2.0F, 2.0F, 3.3F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -5.0114F, 0.0F, 0.0F, 0.0F, 0.3665F));
		PartDefinition lowerbody_r8 = lowerbody.addOrReplaceChild("lowerbody_r8", CubeListBuilder.create().texOffs(20, 0).addBox(-0.5F, -1.75F, -2.0F, 2.0F, 3.3F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -5.0114F, 0.0F, 0.0F, 0.0F, -0.6109F));
		PartDefinition upperbody = lowerbody.addOrReplaceChild("upperbody",
				CubeListBuilder.create().texOffs(10, 28).addBox(-2.1734F, -1.6692F, -2.2755F, 2.2F, 4.1F, 4.0F, new CubeDeformation(0.0F)).texOffs(10, 29).addBox(-2.1734F, -5.6692F, -2.2755F, 2.2F, 4.1F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.8754F, -8.8134F, 0.2755F, 0.0F, 0.0F, -0.1309F));
		PartDefinition upperbody_r1 = upperbody.addOrReplaceChild("upperbody_r1", CubeListBuilder.create().texOffs(39, 18).mirror().addBox(-1.0352F, 0.0298F, 0.3065F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.4419F, -6.6852F, 0.4877F, -1.3984F, 1.4389F, -3.055F));
		PartDefinition upperbody_r2 = upperbody.addOrReplaceChild("upperbody_r2", CubeListBuilder.create().texOffs(4, 3).addBox(-1.2811F, -1.4519F, 0.2919F, 2.8F, 0.3F, 0.8F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 0.0F, 1.4399F, 0.0F));
		PartDefinition upperbody_r3 = upperbody.addOrReplaceChild("upperbody_r3", CubeListBuilder.create().texOffs(22, 48).mirror().addBox(1.0833F, -1.3384F, -0.0944F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 0.259F, -0.5628F, 1.4936F));
		PartDefinition upperbody_r4 = upperbody.addOrReplaceChild("upperbody_r4", CubeListBuilder.create().texOffs(38, 17).mirror().addBox(0.9761F, -1.8766F, -0.0694F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, -0.0479F, -0.612F, 2.0377F));
		PartDefinition upperbody_r5 = upperbody.addOrReplaceChild("upperbody_r5", CubeListBuilder.create().texOffs(21, 48).mirror().addBox(1.3548F, -1.1232F, 0.7803F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 2.5779F, -1.3041F, -0.886F));
		PartDefinition upperbody_r6 = upperbody.addOrReplaceChild("upperbody_r6", CubeListBuilder.create().texOffs(37, 16).mirror().addBox(1.6229F, -1.1483F, 0.8054F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 2.8589F, -1.3367F, -1.1759F));
		PartDefinition upperbody_r7 = upperbody.addOrReplaceChild("upperbody_r7", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0229F, -13.9772F, -1.75F, 3.0F, 2.9F, 3.5F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, 0.0F, 0.0F, 0.1309F));
		PartDefinition upperbody_r8 = upperbody.addOrReplaceChild("upperbody_r8", CubeListBuilder.create().texOffs(16, 37).mirror().addBox(-3.1639F, -13.3945F, -0.4804F, 2.7F, 4.425F, 1.575F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, 0.1309F, -0.48F, 0.1309F));
		PartDefinition upperbody_r9 = upperbody.addOrReplaceChild("upperbody_r9", CubeListBuilder.create().texOffs(34, 30).addBox(-3.1639F, -13.3945F, -1.0946F, 2.7F, 4.425F, 1.575F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, -0.1309F, 0.48F, 0.1309F));
		PartDefinition upperbody_r10 = upperbody.addOrReplaceChild(
				"upperbody_r10", CubeListBuilder.create().texOffs(35, 26).addBox(-0.9383F, -11.2327F, -6.0333F, 2.2F, 3.8F, 2.0F, new CubeDeformation(0.0F)).texOffs(12, 22)
						.addBox(-0.9383F, -11.2327F, -5.0333F, 2.2F, 3.8F, 2.0F, new CubeDeformation(0.0F)).texOffs(12, 18).mirror().addBox(-0.9383F, -10.2327F, -3.5333F, 2.2F, 3.8F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, -0.4363F, 0.0F, 0.0F));
		PartDefinition upperbody_r11 = upperbody.addOrReplaceChild("upperbody_r11", CubeListBuilder.create().texOffs(19, 40).addBox(-0.6883F, -10.2327F, 1.7833F, 1.7F, 3.8F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, 0.4363F, 0.0F, 0.0F));
		PartDefinition upperbody_r12 = upperbody.addOrReplaceChild("upperbody_r12", CubeListBuilder.create().texOffs(14, 3).addBox(-0.6F, -2.05F, -2.0F, 2.2F, 4.1F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0734F, -1.8692F, -0.2755F, 0.0F, 0.0F, -0.2618F));
		PartDefinition neck = upperbody.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.8604F, -11.1337F, -0.2755F, 0.0F, 0.0F, -0.2182F));
		PartDefinition neck_r1 = neck.addOrReplaceChild("neck_r1", CubeListBuilder.create().texOffs(0, 0).addBox(2.0988F, -13.5603F, -5.0F, 0.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.844F, 0.6303F, 0.0F, 0.0F, 0.0F, -0.3054F));
		PartDefinition neck_r2 = neck.addOrReplaceChild("neck_r2", CubeListBuilder.create().texOffs(22, 40).addBox(2.3488F, -6.5603F, -1.0F, 1.75F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.844F, 2.6303F, 0.0F, 0.0F, 0.0F, -0.3054F));
		PartDefinition neck_r3 = neck.addOrReplaceChild("neck_r3", CubeListBuilder.create().texOffs(3, 40).mirror().addBox(1.3021F, -6.6382F, -1.25F, 1.5F, 2.5F, 2.5F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.3942F, 4.5902F, 0.0F, 0.0F, 0.0F, -0.0873F));
		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.536F, -3.8816F, 0.0007F, -0.3491F, 0.0F, 0.0F));
		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.0461F, 0.3501F, 0.0527F, 0.0F, 0.0F, -1.0472F));
		PartDefinition rightupperarm = upperbody.addOrReplaceChild("rightupperarm", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.2351F, -10.3249F, 2.7245F, 0.0873F, 0.0F, 0.0F));
		PartDefinition rightupperarm_r1 = rightupperarm.addOrReplaceChild("rightupperarm_r1", CubeListBuilder.create().texOffs(11, 28).addBox(0.0052F, -11.9353F, 0.5151F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0019F, 11.281F, 0.0642F, 0.0828F, -0.0036F, 0.0435F));
		PartDefinition rightupperarm_r2 = rightupperarm.addOrReplaceChild("rightupperarm_r2", CubeListBuilder.create().texOffs(9, 7).addBox(-0.7401F, -11.9078F, 0.5151F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0019F, 11.281F, 0.0642F, 0.0829F, 0.0F, 0.0F));
		PartDefinition rightlowerarm = rightupperarm.addOrReplaceChild("rightlowerarm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 11.181F, 1.1642F, -0.0873F, 0.0F, 0.0F));
		PartDefinition rightlowerarm_r1 = rightlowerarm.addOrReplaceChild("rightlowerarm_r1", CubeListBuilder.create().texOffs(32, 17).addBox(-0.5101F, -20.6202F, 10.1838F, 1.5F, 5.7F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 22.8302F, -4.0922F, 0.3054F, 0.0F, 0.0F));
		PartDefinition righthand = rightlowerarm.addOrReplaceChild("righthand", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.0291F, 8.005F, 1.742F, 0.0F, 1.6581F, 0.0F));
		PartDefinition righthand_r1 = righthand.addOrReplaceChild("righthand_r1", CubeListBuilder.create().texOffs(42, 31).addBox(-0.1349F, -1.0004F, -0.7923F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0125F, -0.3374F, -0.1993F, 1.551F, -0.4333F, -1.8591F));
		PartDefinition righthand_r2 = righthand.addOrReplaceChild("righthand_r2", CubeListBuilder.create().texOffs(6, 47).mirror().addBox(-0.9468F, -0.9343F, -0.7423F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0125F, -0.3374F, -0.1993F, 1.5522F, -0.2588F, -1.8627F));
		PartDefinition righthand_r3 = righthand.addOrReplaceChild("righthand_r3", CubeListBuilder.create().texOffs(26, 44).mirror().addBox(-6.3949F, -0.9775F, -0.6923F, 5.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0125F, -0.3374F, -0.1993F, 1.5525F, -0.1715F, -1.8643F));
		PartDefinition righthand_r4 = righthand.addOrReplaceChild("righthand_r4",
				CubeListBuilder.create().texOffs(41, 29).addBox(-0.007F, -0.4894F, -0.3848F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).texOffs(39, 27).addBox(-0.007F, -0.4894F, 0.1902F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.texOffs(37, 26).addBox(-0.007F, -0.4894F, 0.7902F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).texOffs(43, 12).addBox(0.8353F, -0.6237F, -0.4348F, 1.525F, 0.85F, 1.75F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0125F, -0.3374F, -0.1993F, 0.0087F, -0.0008F, -1.4312F));
		PartDefinition righthand_r5 = righthand.addOrReplaceChild("righthand_r5",
				CubeListBuilder.create().texOffs(5, 18).mirror().addBox(-0.9095F, -0.4089F, -0.3348F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(4, 17).mirror()
						.addBox(-0.9095F, -0.4089F, 0.2402F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(3, 47).mirror().addBox(-0.9095F, -0.4089F, 0.8402F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0125F, -0.3374F, -0.1993F, 0.0087F, 0.0008F, -1.6057F));
		PartDefinition righthand_r6 = righthand.addOrReplaceChild("righthand_r6",
				CubeListBuilder.create().texOffs(25, 48).mirror().addBox(-1.4036F, -0.4508F, -0.2848F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(24, 48).mirror()
						.addBox(-1.4036F, -0.4508F, 0.2902F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(23, 48).mirror().addBox(-1.4036F, -0.4508F, 0.8902F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0125F, -0.3374F, -0.1993F, 0.0086F, 0.0015F, -1.693F));
		PartDefinition righthand_r7 = righthand.addOrReplaceChild("righthand_r7",
				CubeListBuilder.create().texOffs(42, 46).mirror().addBox(-6.9164F, -0.4751F, -0.2598F, 5.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false).texOffs(41, 46).mirror()
						.addBox(-6.9164F, -0.4751F, 0.3152F, 5.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false).texOffs(40, 46).mirror().addBox(-6.9164F, -0.4751F, 0.9152F, 5.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0125F, -0.3374F, -0.1993F, 0.0085F, 0.0019F, -1.7366F));
		PartDefinition leftupperarm = upperbody.addOrReplaceChild("leftupperarm", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.9351F, -9.9249F, -3.2755F, -0.0873F, 0.0F, 0.0F));
		PartDefinition leftupperarm_r1 = leftupperarm.addOrReplaceChild("leftupperarm_r1", CubeListBuilder.create().texOffs(42, 44).addBox(-0.4F, -1.5F, -0.4F, 0.8F, 3.0F, 0.8F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.4836F, 10.3741F, -0.9198F, -0.2614F, -0.3672F, -0.0787F));
		PartDefinition leftupperarm_r2 = leftupperarm.addOrReplaceChild("leftupperarm_r2", CubeListBuilder.create().texOffs(1, 32).mirror().addBox(-0.4493F, -15.1798F, -1.5831F, 1.0F, 9.9F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.0518F, 14.8547F, -0.0842F, -0.0644F, -0.4833F, -0.0058F));
		PartDefinition leftlowerarm = leftupperarm.addOrReplaceChild("leftlowerarm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.8543F, 11.7342F, -1.0041F, 0.0868F, -0.0091F, 0.1043F));
		PartDefinition leftlowerarm_r1 = leftlowerarm.addOrReplaceChild("leftlowerarm_r1", CubeListBuilder.create().texOffs(22, 44).addBox(0.1984F, -4.1989F, 0.5161F, 0.7F, 3.2F, 0.7F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.5435F, 3.8358F, -1.3369F, -0.0653F, -0.4066F, -0.1708F));
		PartDefinition lowerlowerLeftArm = leftlowerarm.addOrReplaceChild("lowerlowerLeftArm", CubeListBuilder.create(), PartPose.offsetAndRotation(0.719F, -5.204F, 1.1698F, 0.0F, 0.8727F, 0.0F));
		PartDefinition lefthand = lowerlowerLeftArm.addOrReplaceChild("lefthand", CubeListBuilder.create(), PartPose.offset(0.4056F, 8.418F, -1.1037F));
		PartDefinition lefthand_r1 = lefthand.addOrReplaceChild("lefthand_r1", CubeListBuilder.create().texOffs(6, 36).addBox(-1.3498F, -1.2119F, -0.5144F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 2.126F, -0.0163F, 2.29F));
		PartDefinition lefthand_r2 = lefthand.addOrReplaceChild("lefthand_r2", CubeListBuilder.create().texOffs(30, 41).mirror().addBox(0.5358F, -1.1404F, -0.4144F, 4.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 2.144F, -0.2379F, 2.1491F));
		PartDefinition lefthand_r3 = lefthand.addOrReplaceChild("lefthand_r3", CubeListBuilder.create().texOffs(10, 25).mirror().addBox(-0.3723F, -1.1149F, -0.4644F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 2.1344F, -0.1643F, 2.197F));
		PartDefinition lefthand_r4 = lefthand.addOrReplaceChild("lefthand_r4",
				CubeListBuilder.create().texOffs(43, 34).addBox(-2.4537F, -0.9774F, -0.5517F, 1.525F, 0.85F, 1.75F, new CubeDeformation(0.0F)).texOffs(4, 35).addBox(-1.4115F, -0.8431F, 0.6733F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.texOffs(46, 45).addBox(-1.4115F, -0.8431F, 0.0733F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).texOffs(44, 32).addBox(-1.4115F, -0.8431F, -0.5017F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.7174F, -0.1079F, 1.6969F));
		PartDefinition lefthand_r5 = lefthand.addOrReplaceChild("lefthand_r5",
				CubeListBuilder.create().texOffs(9, 47).mirror().addBox(-0.369F, -0.7409F, 0.7233F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(8, 22).mirror()
						.addBox(-0.369F, -0.7409F, 0.1233F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(7, 21).mirror().addBox(-0.369F, -0.7409F, -0.4517F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.6953F, -0.2214F, 1.8315F));
		PartDefinition lefthand_r6 = lefthand.addOrReplaceChild("lefthand_r6",
				CubeListBuilder.create().texOffs(29, 48).mirror().addBox(0.5717F, -0.7682F, 0.7733F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(28, 48).mirror()
						.addBox(0.5717F, -0.7682F, 0.1733F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(27, 48).mirror().addBox(0.5717F, -0.7682F, -0.4017F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.6781F, -0.2767F, 1.9011F));
		PartDefinition lefthand_r7 = lefthand.addOrReplaceChild("lefthand_r7",
				CubeListBuilder.create().texOffs(45, 46).mirror().addBox(1.0709F, -0.7843F, 0.7983F, 5.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false).texOffs(44, 19).mirror()
						.addBox(1.0709F, -0.7843F, 0.1983F, 4.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false).texOffs(43, 46).mirror().addBox(1.0709F, -0.7843F, -0.3767F, 5.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.6679F, -0.3039F, 1.9367F));
		PartDefinition upperleftleg = lowerbody.addOrReplaceChild("upperleftleg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.2F, -2.0F, 0.0F, 0.0F, -0.0873F));
		PartDefinition upperleftleg_r1 = upperleftleg.addOrReplaceChild("upperleftleg_r1",
				CubeListBuilder.create().texOffs(6, 25).addBox(-0.3F, -3.3002F, 0.0311F, 2.0F, 6.9F, 0.65F, new CubeDeformation(0.0F)).texOffs(16, 15).addBox(-0.3F, -3.3002F, -0.2689F, 2.0F, 6.9F, 0.95F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.1046F, 2.2076F, 0.029F, -0.1396F, 0.0F, 0.0F));
		PartDefinition upperleftleg_r2 = upperleftleg.addOrReplaceChild("upperleftleg_r2", CubeListBuilder.create().texOffs(5, 10).addBox(-0.3F, -3.4F, -0.2483F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.1046F, 2.3046F, -0.5F, -0.0175F, 0.0F, 0.0F));
		PartDefinition lowerleftleg = upperleftleg.addOrReplaceChild("lowerleftleg", CubeListBuilder.create().texOffs(17, 36).addBox(-0.028F, 4.57F, -0.6F, 1.2F, 2.425F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.1046F, 5.8046F, 0.0F, 0.0F, 0.0F, -0.1745F));
		PartDefinition lowerleftleg_r1 = lowerleftleg.addOrReplaceChild("lowerleftleg_r1", CubeListBuilder.create().texOffs(4, 29).addBox(0.8142F, -4.1223F, -0.694F, 0.8F, 4.9F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.3915F, 3.6442F, 0.0997F, 0.0436F, 0.0F, 0.1484F));
		PartDefinition lowerleftleg_r2 = lowerleftleg.addOrReplaceChild("lowerleftleg_r2", CubeListBuilder.create().texOffs(3, 43).addBox(0.4009F, -3.8382F, -0.694F, 0.7F, 4.9F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.3915F, 3.6442F, 0.0997F, 0.0426F, 0.0094F, -0.0696F));
		PartDefinition leftfoot = lowerleftleg.addOrReplaceChild("leftfoot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 8.0F, 0.3F, 0.0F, 0.0F, 0.1745F));
		PartDefinition leftfoot_r1 = leftfoot.addOrReplaceChild("leftfoot_r1",
				CubeListBuilder.create().texOffs(23, 41).addBox(-2.3729F, -0.5866F, -0.862F, 1.525F, 0.85F, 1.75F, new CubeDeformation(0.0F)).texOffs(18, 45).addBox(-1.3306F, -0.4522F, 0.363F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.texOffs(16, 45).addBox(-1.3306F, -0.4522F, -0.237F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).texOffs(14, 45).addBox(-1.3306F, -0.4522F, -0.812F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.716F, -1.1488F, -0.4411F, -3.1344F, 0.0428F, 3.1067F));
		PartDefinition leftfoot_r2 = leftfoot.addOrReplaceChild("leftfoot_r2",
				CubeListBuilder.create().texOffs(13, 29).mirror().addBox(-0.2214F, -0.3701F, -0.762F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(12, 47).mirror()
						.addBox(-0.2214F, -0.3701F, -0.187F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(11, 26).mirror().addBox(-0.2214F, -0.3701F, 0.413F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.716F, -1.1488F, -0.4411F, 3.1412F, 0.0434F, 2.932F));
		PartDefinition leftfoot_r3 = leftfoot.addOrReplaceChild("leftfoot_r3",
				CubeListBuilder.create().texOffs(33, 48).mirror().addBox(0.751F, -0.4116F, 0.463F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(32, 48).mirror()
						.addBox(0.751F, -0.4116F, -0.137F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(31, 48).mirror().addBox(0.751F, -0.4116F, -0.712F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.716F, -1.1488F, -0.4411F, 3.1374F, 0.0432F, 2.8446F));
		PartDefinition upperrightleg = lowerbody.addOrReplaceChild("upperrightleg", CubeListBuilder.create().texOffs(35, 13).addBox(-0.2216F, -0.7966F, -0.3F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.9F, 2.0F, 0.0F, 0.0F, -0.0873F));
		PartDefinition upperrightleg_r1 = upperrightleg.addOrReplaceChild("upperrightleg_r1", CubeListBuilder.create().texOffs(11, 43).mirror().addBox(-0.3F, -3.2177F, -0.2322F, 2.0F, 6.8F, 0.65F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0784F, 2.513F, -0.3788F, 0.1745F, 0.0F, 0.0F));
		PartDefinition lowerrightleg = upperrightleg.addOrReplaceChild("lowerrightleg", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0784F, 6.1034F, 0.0F, 0.0F, 0.0F, -0.1309F));
		PartDefinition lowerrightleg_r1 = lowerrightleg.addOrReplaceChild("lowerrightleg_r1", CubeListBuilder.create().texOffs(18, 43).addBox(0.117F, -4.0205F, -0.4602F, 0.7F, 4.9F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0272F, 3.5592F, 0.2843F, 0.0237F, 0.0197F, -0.0618F));
		PartDefinition lowerrightleg_r2 = lowerrightleg.addOrReplaceChild("lowerrightleg_r2", CubeListBuilder.create().texOffs(4, 43).addBox(0.1448F, 0.4608F, -0.1593F, 1.2F, 2.525F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0272F, 3.5592F, 0.0343F, -0.0182F, 0.0123F, 0.008F));
		PartDefinition lowerrightleg_r3 = lowerrightleg.addOrReplaceChild("lowerrightleg_r3", CubeListBuilder.create().texOffs(9, 14).addBox(0.4956F, -4.2267F, -0.4602F, 0.8F, 4.9F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0272F, 3.5592F, 0.2843F, 0.0272F, 0.0145F, 0.1389F));
		PartDefinition rightfoot = lowerrightleg.addOrReplaceChild("rightfoot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.1F, 8.0F, 0.0F, 0.0F, 0.0F, 0.1309F));
		PartDefinition rightfoot_r1 = rightfoot.addOrReplaceChild("rightfoot_r1",
				CubeListBuilder.create().texOffs(30, 46).addBox(-1.1325F, -0.4702F, -0.8091F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).texOffs(28, 46).addBox(-1.1325F, -0.4702F, -0.2341F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.texOffs(26, 46).addBox(-1.1325F, -0.4702F, 0.3659F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).texOffs(27, 2).mirror().addBox(-2.6723F, -0.565F, -0.8633F, 1.525F, 0.85F, 1.75F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.64F, -1.1599F, 0.4589F, -3.1314F, -0.0444F, 3.1066F));
		PartDefinition rightfoot_r2 = rightfoot.addOrReplaceChild("rightfoot_r2",
				CubeListBuilder.create().texOffs(16, 33).mirror().addBox(0.0743F, -0.4216F, -0.7627F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(15, 47).mirror()
						.addBox(0.0743F, -0.4216F, -0.1877F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false).texOffs(14, 30).mirror().addBox(0.0743F, -0.4216F, 0.4123F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.64F, -1.1599F, 0.4589F, -3.1238F, -0.0419F, 2.932F));
		PartDefinition rightfoot_r3 = rightfoot.addOrReplaceChild("rightfoot_r3",
				CubeListBuilder.create().texOffs(36, 1).mirror().addBox(1.0412F, -0.4887F, -0.7127F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(35, 0).mirror()
						.addBox(1.0412F, -0.4887F, -0.1377F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false).texOffs(34, 48).mirror().addBox(1.0412F, -0.4887F, 0.4623F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.64F, -1.1599F, 0.4589F, -3.1202F, -0.0402F, 2.8446F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.lefthand.xRot = ageInTicks / 20.f;
		this.leftfoot.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.jaw.zRot = ageInTicks;
		this.lowerrightleg.zRot = ageInTicks;
		this.righthand.zRot = headPitch / (180F / (float) Math.PI);
		this.rightfoot.zRot = ageInTicks / 20.f;
		this.neck.xRot = ageInTicks / 20.f;
		this.rightupperarm.xRot = headPitch / (180F / (float) Math.PI);
		this.rightlowerarm.yRot = ageInTicks / 20.f;
		this.head.xRot = headPitch / (180F / (float) Math.PI);
		this.upperleftleg.yRot = headPitch / (180F / (float) Math.PI);
		this.lowerleftleg.zRot = ageInTicks / 20.f;
		this.upperbody.yRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
		this.upperrightleg.zRot = headPitch / (180F / (float) Math.PI);
		this.lowerbody.zRot = ageInTicks;
		this.leftupperarm.yRot = ageInTicks / 20.f;
		this.leftlowerarm.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.lowerlowerLeftArm.xRot = headPitch / (180F / (float) Math.PI);
	}
}
