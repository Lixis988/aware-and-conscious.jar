// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelunknown<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "unknown"), "main");
	private final ModelPart base;
	private final ModelPart lowerbody;
	private final ModelPart upperbody;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart rightupperarm;
	private final ModelPart rightlowerarm;
	private final ModelPart righthand;
	private final ModelPart leftupperarm;
	private final ModelPart leftlowerarm;
	private final ModelPart lowerlowerLeftArm;
	private final ModelPart lefthand;
	private final ModelPart upperleftleg;
	private final ModelPart lowerleftleg;
	private final ModelPart leftfoot;
	private final ModelPart upperrightleg;
	private final ModelPart lowerrightleg;
	private final ModelPart rightfoot;

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

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition lowerbody = base.addOrReplaceChild("lowerbody", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, -15.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition lowerbody_r1 = lowerbody.addOrReplaceChild("lowerbody_r1",
				CubeListBuilder.create().texOffs(14, 27).addBox(-0.6938F, -5.2354F, -2.0F, 2.0F, 3.4F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.2555F, -4.3098F, 0.0F, 0.0F, 0.0F, 0.1047F));

		PartDefinition lowerbody_r2 = lowerbody.addOrReplaceChild("lowerbody_r2",
				CubeListBuilder.create().texOffs(4, 21)
						.addBox(-1.0F, -2.0F, -0.6F, 2.0F, 4.1F, 1.0F, new CubeDeformation(0.0F)).texOffs(18, 45)
						.addBox(-1.0F, 2.1F, -0.6F, 2.0F, 2.075F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -3.1776F, -1.9715F, -3.0194F, 0.0F, 3.1416F));

		PartDefinition lowerbody_r3 = lowerbody.addOrReplaceChild("lowerbody_r3",
				CubeListBuilder.create().texOffs(37, 43).addBox(-1.0F, -0.9875F, -0.6F, 2.0F, 2.075F, 1.3F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, 0.1097F, -1.2563F, -0.3491F, 0.0F, 0.0F));

		PartDefinition lowerbody_r4 = lowerbody.addOrReplaceChild("lowerbody_r4",
				CubeListBuilder.create().texOffs(10, 4).addBox(-1.0F, -0.9875F, -0.5F, 2.0F, 2.075F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, 0.1097F, 1.2563F, 0.3491F, 0.0F, 0.0F));

		PartDefinition lowerbody_r5 = lowerbody.addOrReplaceChild("lowerbody_r5",
				CubeListBuilder.create().texOffs(13, 27)
						.addBox(-1.0F, -2.5625F, -0.5F, 2.0F, 4.55F, 1.0F, new CubeDeformation(0.0F)).texOffs(44, 17)
						.addBox(-1.0F, 1.9875F, -0.5F, 2.0F, 2.2F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -3.1653F, 1.9736F, 0.0873F, 0.0F, 0.0F));

		PartDefinition lowerbody_r6 = lowerbody.addOrReplaceChild("lowerbody_r6",
				CubeListBuilder.create().texOffs(24, 31).addBox(-0.875F, -0.65F, -2.0F, 2.0F, 2.3F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.6868F, -1.8858F, 0.0F, 0.0F, 0.0F, 0.096F));

		PartDefinition lowerbody_r7 = lowerbody.addOrReplaceChild("lowerbody_r7",
				CubeListBuilder.create().texOffs(18, 0).addBox(0.3F, -0.25F, -2.0F, 2.0F, 3.3F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -5.0114F, 0.0F, 0.0F, 0.0F, 0.3665F));

		PartDefinition lowerbody_r8 = lowerbody.addOrReplaceChild("lowerbody_r8",
				CubeListBuilder.create().texOffs(2, 11).addBox(-0.5F, -1.75F, -2.0F, 2.0F, 3.3F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.706F, -5.0114F, 0.0F, 0.0F, 0.0F, -0.6109F));

		PartDefinition upperbody = lowerbody.addOrReplaceChild("upperbody", CubeListBuilder.create().texOffs(0, 16)
				.addBox(-2.1734F, -1.6692F, -2.2755F, 2.2F, 4.1F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 24)
				.addBox(-2.1734F, -5.6692F, -2.2755F, 2.2F, 4.1F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.8754F, -8.8134F, 0.2755F, 0.0F, 0.0F, -0.1309F));

		PartDefinition upperbody_r1 = upperbody.addOrReplaceChild("upperbody_r1",
				CubeListBuilder.create().texOffs(11, 10).mirror()
						.addBox(-1.0352F, 0.0298F, 0.3065F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-0.4419F, -6.6852F, 0.4877F, -1.3984F, 1.4389F, -3.055F));

		PartDefinition upperbody_r2 = upperbody.addOrReplaceChild("upperbody_r2",
				CubeListBuilder.create().texOffs(14, 11).addBox(-1.2811F, -1.4519F, 0.2919F, 2.8F, 0.3F, 0.8F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 0.0F, 1.4399F, 0.0F));

		PartDefinition upperbody_r3 = upperbody.addOrReplaceChild("upperbody_r3",
				CubeListBuilder.create().texOffs(10, 8).mirror()
						.addBox(1.0833F, -1.3384F, -0.0944F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 0.259F, -0.5628F, 1.4936F));

		PartDefinition upperbody_r4 = upperbody.addOrReplaceChild("upperbody_r4",
				CubeListBuilder.create().texOffs(9, 6).mirror()
						.addBox(0.9761F, -1.8766F, -0.0694F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, -0.0479F, -0.612F, 2.0377F));

		PartDefinition upperbody_r5 = upperbody.addOrReplaceChild("upperbody_r5",
				CubeListBuilder.create().texOffs(8, 4).mirror()
						.addBox(1.3548F, -1.1232F, 0.7803F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 2.5779F, -1.3041F, -0.886F));

		PartDefinition upperbody_r6 = upperbody.addOrReplaceChild("upperbody_r6",
				CubeListBuilder.create().texOffs(7, 2).mirror()
						.addBox(1.6229F, -1.1483F, 0.8054F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.354F, -7.023F, -0.0174F, 2.8589F, -1.3367F, -1.1759F));

		PartDefinition upperbody_r7 = upperbody.addOrReplaceChild("upperbody_r7",
				CubeListBuilder.create().texOffs(21, 21).addBox(-3.0229F, -13.9772F, -1.75F, 3.0F, 2.9F, 3.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, 0.0F, 0.0F, 0.1309F));

		PartDefinition upperbody_r8 = upperbody.addOrReplaceChild("upperbody_r8",
				CubeListBuilder.create().texOffs(1, 13).mirror()
						.addBox(-3.1639F, -13.3945F, -0.4804F, 2.7F, 4.425F, 1.575F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, 0.1309F, -0.48F, 0.1309F));

		PartDefinition upperbody_r9 = upperbody.addOrReplaceChild("upperbody_r9",
				CubeListBuilder.create().texOffs(2, 38).addBox(-3.1639F, -13.3945F, -1.0946F, 2.7F, 4.425F, 1.575F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, -0.1309F, 0.48F, 0.1309F));

		PartDefinition upperbody_r10 = upperbody.addOrReplaceChild("upperbody_r10", CubeListBuilder.create()
				.texOffs(27, 28).mirror()
				.addBox(-0.9383F, -11.2327F, -6.0333F, 2.2F, 3.8F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(32, 22).addBox(-0.9383F, -11.2327F, -5.0333F, 2.2F, 3.8F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(9, 38).addBox(-0.9383F, -10.2327F, -3.5333F, 2.2F, 3.8F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, -0.4363F, 0.0F, 0.0F));

		PartDefinition upperbody_r11 = upperbody.addOrReplaceChild("upperbody_r11",
				CubeListBuilder.create().texOffs(32, 26).addBox(-0.6883F, -10.2327F, 1.7833F, 1.7F, 3.8F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.2351F, 2.6751F, -0.2755F, 0.4363F, 0.0F, 0.0F));

		PartDefinition upperbody_r12 = upperbody.addOrReplaceChild("upperbody_r12",
				CubeListBuilder.create().texOffs(8, 0).addBox(-0.6F, -2.05F, -2.0F, 2.2F, 4.1F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0734F, -1.8692F, -0.2755F, 0.0F, 0.0F, -0.2618F));

		PartDefinition neck = upperbody.addOrReplaceChild("neck", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-1.8604F, -11.1337F, -0.2755F, 0.0F, 0.0F, -0.2182F));

		PartDefinition neck_r1 = neck.addOrReplaceChild("neck_r1",
				CubeListBuilder.create().texOffs(7, 30).addBox(2.3488F, -6.5603F, -1.0F, 1.75F, 3.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.844F, 2.6303F, 0.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition neck_r2 = neck.addOrReplaceChild("neck_r2",
				CubeListBuilder.create().texOffs(35, 40).addBox(1.3021F, -6.6382F, -1.25F, 1.5F, 2.5F, 2.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.3942F, 4.5902F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(34, 0)
				.addBox(-2.8079F, -2.6076F, -2.0007F, 2.35F, 2.5F, 4.0F, new CubeDeformation(0.0F)).texOffs(38, 0)
				.addBox(-1.8079F, -2.1076F, 1.2493F, 0.85F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.536F, -3.8816F, 0.0007F, -0.3491F, 0.0F, 0.0F));

		PartDefinition head_r1 = head.addOrReplaceChild("head_r1",
				CubeListBuilder.create().texOffs(2, 24).addBox(-0.7659F, -6.0824F, -3.2319F, 0.25F, 0.75F, 1.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0298F, 5.8383F, -1.6873F, -0.6315F, -1.0496F, 0.393F));

		PartDefinition head_r2 = head.addOrReplaceChild("head_r2",
				CubeListBuilder.create().texOffs(24, 54).addBox(-0.7659F, -5.0255F, -3.6638F, 0.25F, 0.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0298F, 5.8383F, -1.6873F, -0.7624F, -1.0496F, 0.393F));

		PartDefinition head_r3 = head.addOrReplaceChild("head_r3",
				CubeListBuilder.create().texOffs(23, 54).addBox(-0.2836F, -5.7768F, -2.9793F, 0.25F, 0.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-5.7578F, 6.834F, -0.3616F, -0.4641F, -1.1227F, 0.2266F));

		PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(0, 23).mirror()
				.addBox(-0.2836F, -6.594F, -2.1378F, 0.25F, 0.75F, 1.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-5.7512F, 6.5581F, -0.3349F, -0.2896F, -1.1227F, 0.2266F));

		PartDefinition head_r5 = head.addOrReplaceChild("head_r5",
				CubeListBuilder.create().texOffs(44, 22).addBox(-1.1581F, -6.2083F, -2.9517F, 0.25F, 0.75F, 1.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.8213F, 6.3326F, -0.9676F, -0.9282F, -1.2773F, 0.7298F));

		PartDefinition head_r6 = head.addOrReplaceChild("head_r6",
				CubeListBuilder.create().texOffs(22, 54).addBox(-1.1581F, -5.0824F, -3.4935F, 0.25F, 0.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.8213F, 6.3326F, -0.9676F, -1.0591F, -1.2773F, 0.7298F));

		PartDefinition head_r7 = head.addOrReplaceChild("head_r7",
				CubeListBuilder.create().texOffs(21, 54).addBox(-1.6822F, -5.0803F, -3.3317F, 0.25F, 12.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.1113F, 6.0353F, -1.4971F, -0.9427F, -1.0019F, 0.6782F));

		PartDefinition head_r8 = head.addOrReplaceChild("head_r8",
				CubeListBuilder.create().texOffs(41, 21).addBox(-1.6822F, -6.185F, -2.7915F, 0.25F, 0.75F, 1.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.1113F, 6.0353F, -1.4971F, -0.8118F, -1.0019F, 0.6782F));

		PartDefinition head_r9 = head.addOrReplaceChild("head_r9",
				CubeListBuilder.create().texOffs(37, 20).addBox(-0.7659F, -6.0824F, 2.7819F, 0.25F, 0.75F, 1.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0298F, 5.8383F, 1.686F, 0.6315F, 1.0496F, 0.393F));

		PartDefinition head_r10 = head.addOrReplaceChild("head_r10",
				CubeListBuilder.create().texOffs(20, 54).addBox(-0.7659F, -5.0255F, 3.4138F, 0.25F, 0.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0298F, 5.8383F, 1.686F, 0.7624F, 1.0496F, 0.393F));

		PartDefinition head_r11 = head.addOrReplaceChild("head_r11",
				CubeListBuilder.create().texOffs(19, 54).addBox(-0.2836F, -5.9922F, 1.5134F, 0.25F, 11.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-5.7512F, 6.5581F, 0.3336F, 0.246F, 1.1227F, 0.2266F));

		PartDefinition head_r12 = head.addOrReplaceChild("head_r12",
				CubeListBuilder.create().texOffs(33, 19).addBox(-0.2836F, -6.594F, 1.6878F, 0.25F, 0.75F, 1.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-5.7512F, 6.5581F, 0.3336F, 0.2896F, 1.1227F, 0.2266F));

		PartDefinition head_r13 = head.addOrReplaceChild("head_r13",
				CubeListBuilder.create().texOffs(28, 18).addBox(-1.1581F, -6.2083F, 2.5017F, 0.25F, 0.75F, 1.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.8213F, 6.3326F, 0.9662F, 0.9282F, 1.2773F, 0.7298F));

		PartDefinition head_r14 = head.addOrReplaceChild("head_r14",
				CubeListBuilder.create().texOffs(18, 54).addBox(-1.1581F, -5.0824F, 3.2435F, 0.25F, 11.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.8213F, 6.3326F, 0.9662F, 1.0591F, 1.2773F, 0.7298F));

		PartDefinition head_r15 = head.addOrReplaceChild("head_r15",
				CubeListBuilder.create().texOffs(17, 54).addBox(-1.6822F, -5.0803F, 3.0817F, 0.25F, 0.5F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.1113F, 6.0353F, 1.4957F, 0.9427F, 1.0019F, 0.6782F));

		PartDefinition head_r16 = head.addOrReplaceChild("head_r16",
				CubeListBuilder.create().texOffs(24, 17).addBox(-1.6822F, -6.185F, 2.3415F, 0.25F, 0.75F, 1.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.1113F, 6.0353F, 1.4957F, 0.8118F, 1.0019F, 0.6782F));

		PartDefinition head_r17 = head.addOrReplaceChild("head_r17", CubeListBuilder.create().texOffs(37, 45)
				.addBox(2.8104F, -6.7738F, -0.6525F, 1.8F, 1.55F, 1.3F, new CubeDeformation(0.0F)).texOffs(35, 6)
				.addBox(3.8104F, -6.7738F, -0.6525F, 0.8F, 1.55F, 1.3F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.7042F, 5.7713F, 0.0018F, 0.0F, 0.0F, -0.7418F));

		PartDefinition head_r18 = head.addOrReplaceChild("head_r18",
				CubeListBuilder.create().texOffs(38, 32).addBox(-2.1532F, -7.136F, -1.6473F, 1.5F, 1.25F, 3.75F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.7042F, 5.7713F, 0.0018F, -0.0932F, -1.1353F, -0.0404F));

		PartDefinition head_r19 = head.addOrReplaceChild("head_r19",
				CubeListBuilder.create().texOffs(36, 35).addBox(-1.9032F, -7.136F, -2.1076F, 1.5F, 1.25F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.7042F, 5.7713F, 0.0018F, 0.0932F, 1.1353F, -0.0404F));

		PartDefinition head_r20 = head.addOrReplaceChild("head_r20", CubeListBuilder.create().texOffs(32, 12).mirror()
				.addBox(-1.8979F, -7.3715F, -0.9275F, 1.95F, 1.4F, 1.875F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.7042F, 5.7713F, 0.0018F, 0.0F, 0.0F, -0.0873F));

		PartDefinition head_r21 = head.addOrReplaceChild("head_r21",
				CubeListBuilder.create().texOffs(30, 24).mirror()
						.addBox(-5.9415F, -5.1399F, -1.5F, 2.35F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.0608F, 5.813F, -0.0007F, 0.0F, 0.0F, 0.8727F));

		PartDefinition head_r22 = head.addOrReplaceChild("head_r22",
				CubeListBuilder.create().texOffs(45, 46).addBox(-3.5236F, 7.3746F, -58.75F, 0.35F, 0.5F, 18.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.9657F, 38.7679F, -0.0007F, -1.5708F, 0.0F, 0.0F));

		PartDefinition head_r23 = head.addOrReplaceChild("head_r23",
				CubeListBuilder.create().texOffs(23, 45).addBox(-3.5236F, -39.6254F, -41.0F, 0.35F, 0.5F, 21.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.9657F, 38.7679F, -0.0007F, -0.6109F, 0.0F, 0.0F));

		PartDefinition head_r24 = head.addOrReplaceChild("head_r24",
				CubeListBuilder.create().texOffs(40, 42).addBox(-3.7736F, 12.1246F, -42.5F, 0.85F, 1.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.9657F, 38.7679F, -0.0007F, -2.0071F, 0.0F, 0.0F));

		PartDefinition head_r25 = head.addOrReplaceChild("head_r25",
				CubeListBuilder.create().texOffs(40, 0).addBox(-3.7736F, -33.3754F, -25.75F, 0.85F, 1.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.9657F, 38.7679F, -0.0007F, -0.48F, 0.0F, 0.0F));

		PartDefinition head_r26 = head.addOrReplaceChild("head_r26",
				CubeListBuilder.create().texOffs(39, 34).addBox(-3.7736F, -37.3754F, 13.75F, 0.85F, 1.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.9657F, 38.7679F, -0.0007F, 0.48F, 0.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw",
				CubeListBuilder.create().texOffs(41, 0).addBox(-3.9206F, -0.5678F, -1.8033F, 3.85F, 1.0F, 3.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.0461F, 0.3501F, 0.0527F, 0.0F, 0.0F, -1.0472F));

		PartDefinition jaw_r1 = jaw.addOrReplaceChild("jaw_r1", CubeListBuilder.create().texOffs(6, 0).mirror()
				.addBox(-1.1612F, -1.6846F, 0.7185F, 0.7F, 0.25F, 0.45F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 0.9704F, -0.4181F, -0.7445F));

		PartDefinition jaw_r2 = jaw.addOrReplaceChild("jaw_r2", CubeListBuilder.create().texOffs(12, 45).mirror()
				.addBox(-0.7309F, -5.9471F, 0.2705F, 1.25F, 1.0F, 1.5F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-9.0426F, 2.4276F, -0.0343F, 0.0F, 1.5708F, 0.6109F));

		PartDefinition jaw_r3 = jaw.addOrReplaceChild("jaw_r3",
				CubeListBuilder.create().texOffs(42, 8).addBox(-0.9103F, -5.9471F, 0.7667F, 0.95F, 1.0F, 3.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0426F, 2.4276F, -0.0343F, 0.0F, 1.2654F, 0.6109F));

		PartDefinition jaw_r4 = jaw.addOrReplaceChild("jaw_r4",
				CubeListBuilder.create().texOffs(42, 42).addBox(-0.6567F, -5.9471F, -3.7782F, 0.95F, 1.0F, 3.25F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0426F, 2.4276F, -0.0343F, 0.0F, -1.2654F, 0.6109F));

		PartDefinition jaw_r5 = jaw.addOrReplaceChild("jaw_r5",
				CubeListBuilder.create().texOffs(42, 14).addBox(1.2705F, -5.9471F, -1.2191F, 2.25F, 1.0F, 2.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0426F, 2.4276F, -0.0343F, 0.0F, 0.0F, 0.6109F));

		PartDefinition jaw_r6 = jaw.addOrReplaceChild("jaw_r6",
				CubeListBuilder.create().texOffs(5, 52).mirror()
						.addBox(0.3228F, -1.8479F, 0.6988F, 0.7F, 0.3F, 0.5F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.1353F, -0.2649F, 2.2902F));

		PartDefinition jaw_r7 = jaw.addOrReplaceChild("jaw_r7",
				CubeListBuilder.create().texOffs(12, 52).addBox(-0.0806F, -2.151F, 0.6488F, 0.675F, 0.375F, 0.6F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.1168F, 0.1075F, 2.0608F));

		PartDefinition jaw_r8 = jaw.addOrReplaceChild("jaw_r8", CubeListBuilder.create().texOffs(4, 52).mirror()
				.addBox(0.2071F, -1.6769F, 0.7238F, 0.7F, 0.25F, 0.45F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.1712F, -0.4181F, 2.3971F));

		PartDefinition jaw_r9 = jaw.addOrReplaceChild("jaw_r9", CubeListBuilder.create().texOffs(3, 52).mirror()
				.addBox(0.336F, -1.3363F, 0.598F, 0.7F, 0.5F, 0.2F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(-9, 52).mirror()
				.addBox(-9.964F, -1.3363F, 0.098F, 11.0F, 0.25F, 0.45F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.044F, -0.3832F, 2.3474F));

		PartDefinition jaw_r10 = jaw.addOrReplaceChild("jaw_r10",
				CubeListBuilder.create().texOffs(1, 52).mirror()
						.addBox(0.5117F, -1.5366F, 0.073F, 0.7F, 0.3F, 0.5F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.0187F, -0.2189F, 2.2622F));

		PartDefinition jaw_r11 = jaw.addOrReplaceChild("jaw_r11",
				CubeListBuilder.create().texOffs(10, 52).addBox(0.2222F, -1.9487F, 0.523F, 0.675F, 0.625F, 0.35F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.0146F, 0.1759F, 2.0753F));

		PartDefinition jaw_r12 = jaw.addOrReplaceChild("jaw_r12", CubeListBuilder.create().texOffs(0, 52).mirror()
				.addBox(0.4659F, -2.0406F, -0.7075F, 0.7F, 0.25F, 0.45F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 2.2427F, 0.3677F, -1.1614F));

		PartDefinition jaw_r13 = jaw.addOrReplaceChild("jaw_r13",
				CubeListBuilder.create().texOffs(53, 52).addBox(-0.3482F, -2.0465F, -0.2325F, 0.7F, 0.55F, 0.25F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 2.2981F, 0.5082F, -1.0311F));

		PartDefinition jaw_r14 = jaw.addOrReplaceChild("jaw_r14",
				CubeListBuilder.create().texOffs(8, 52).addBox(-0.3568F, -1.4844F, -0.9453F, 0.675F, 0.625F, 0.35F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.1455F, 0.8443F, -1.5002F));

		PartDefinition jaw_r15 = jaw.addOrReplaceChild("jaw_r15",
				CubeListBuilder.create().texOffs(52, 52).addBox(-0.3729F, -1.4174F, -1.3703F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.2833F, 0.2601F, -1.2497F));

		PartDefinition jaw_r16 = jaw.addOrReplaceChild("jaw_r16",
				CubeListBuilder.create().texOffs(51, 52).addBox(-0.5491F, -1.4498F, -1.3953F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.2637F, 0.4353F, -1.3067F));

		PartDefinition jaw_r17 = jaw.addOrReplaceChild("jaw_r17",
				CubeListBuilder.create().texOffs(6, 52).addBox(-0.7641F, -2.2774F, -1.1902F, 0.675F, 0.625F, 0.35F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 2.0041F, -0.346F, -0.8655F));

		PartDefinition jaw_r18 = jaw.addOrReplaceChild("jaw_r18",
				CubeListBuilder.create().texOffs(50, 52).addBox(-1.2534F, -1.9964F, -1.1402F, 0.7F, 0.55F, 0.25F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 2.1304F, -0.7318F, -1.1063F));

		PartDefinition jaw_r19 = jaw.addOrReplaceChild("jaw_r19",
				CubeListBuilder.create().texOffs(49, 52).addBox(-1.1651F, -1.8264F, -1.1152F, 0.7F, 0.5F, 0.2F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 2.2412F, -0.8819F, -1.2591F));

		PartDefinition jaw_r20 = jaw.addOrReplaceChild("jaw_r20",
				CubeListBuilder.create().texOffs(48, 52).addBox(-0.9664F, -1.3981F, -0.6047F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.6655F, -0.1502F, -0.9487F));

		PartDefinition jaw_r21 = jaw.addOrReplaceChild("jaw_r21",
				CubeListBuilder.create().texOffs(47, 52).addBox(-1.1361F, -1.539F, -0.6297F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.6645F, 0.0322F, -0.9315F));

		PartDefinition jaw_r22 = jaw.addOrReplaceChild("jaw_r22",
				CubeListBuilder.create().texOffs(4, 52).addBox(-0.8511F, -1.8133F, -0.1797F, 0.675F, 0.625F, 0.35F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.6757F, 0.4665F, -0.8872F));

		PartDefinition jaw_r23 = jaw.addOrReplaceChild("jaw_r23",
				CubeListBuilder.create().texOffs(46, 52).addBox(-0.5634F, -0.6073F, -2.6697F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.0804F, 0.2729F, 1.6651F));

		PartDefinition jaw_r24 = jaw.addOrReplaceChild("jaw_r24",
				CubeListBuilder.create().texOffs(2, 52).addBox(-0.0867F, -0.8329F, -2.2447F, 0.675F, 0.625F, 0.35F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.2981F, 0.7861F, 1.2525F));

		PartDefinition jaw_r25 = jaw.addOrReplaceChild("jaw_r25",
				CubeListBuilder.create().texOffs(45, 52).addBox(-0.2398F, -0.6558F, -2.6947F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -2.1144F, 0.4315F, 1.567F));

		PartDefinition jaw_r26 = jaw.addOrReplaceChild("jaw_r26",
				CubeListBuilder.create().texOffs(44, 52).addBox(0.2016F, -1.5946F, -1.0413F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -0.9586F, -0.4506F, 1.546F));

		PartDefinition jaw_r27 = jaw.addOrReplaceChild("jaw_r27",
				CubeListBuilder.create().texOffs(0, 52).addBox(-0.0372F, -2.0808F, -0.6163F, 0.675F, 0.625F, 0.35F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.0254F, 0.0733F, 1.8873F));

		PartDefinition jaw_r28 = jaw.addOrReplaceChild("jaw_r28",
				CubeListBuilder.create().texOffs(43, 52).addBox(0.3325F, -1.766F, -1.0663F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -0.9989F, -0.2984F, 1.6558F));

		PartDefinition jaw_r29 = jaw.addOrReplaceChild("jaw_r29",
				CubeListBuilder.create().texOffs(42, 52).addBox(-0.1152F, -1.4404F, -0.9485F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.4761F, -0.1502F, 1.7042F));

		PartDefinition jaw_r30 = jaw.addOrReplaceChild("jaw_r30", CubeListBuilder.create().texOffs(50, 50).mirror()
				.addBox(-0.2056F, -1.7713F, -0.5235F, 0.675F, 0.625F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.4659F, 0.4665F, 1.7657F));

		PartDefinition jaw_r31 = jaw.addOrReplaceChild("jaw_r31",
				CubeListBuilder.create().texOffs(41, 52).addBox(0.049F, -1.5566F, -0.9735F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.4771F, 0.0322F, 1.7214F));

		PartDefinition jaw_r32 = jaw.addOrReplaceChild("jaw_r32",
				CubeListBuilder.create().texOffs(40, 52).addBox(0.0148F, -1.388F, -0.6056F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.4761F, -0.1502F, 2.1928F));

		PartDefinition jaw_r33 = jaw.addOrReplaceChild("jaw_r33",
				CubeListBuilder.create().texOffs(39, 52).addBox(0.1865F, -1.5288F, -0.6306F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.4771F, 0.0322F, 2.2101F));

		PartDefinition jaw_r34 = jaw.addOrReplaceChild("jaw_r34", CubeListBuilder.create().texOffs(48, 48).mirror()
				.addBox(-0.0693F, -1.8042F, -0.1806F, 0.675F, 0.625F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.4659F, 0.4665F, 2.2544F));

		PartDefinition jaw_r35 = jaw.addOrReplaceChild("jaw_r35",
				CubeListBuilder.create().texOffs(38, 52).addBox(0.1454F, -1.3368F, -0.4321F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.9206F, -0.3428F, 2.3036F));

		PartDefinition jaw_r36 = jaw.addOrReplaceChild("jaw_r36",
				CubeListBuilder.create().texOffs(37, 52).addBox(0.3242F, -1.5023F, -0.4571F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.9045F, -0.1701F, 2.2401F));

		PartDefinition jaw_r37 = jaw.addOrReplaceChild("jaw_r37", CubeListBuilder.create().texOffs(46, 46).mirror()
				.addBox(0.0667F, -1.8384F, -0.0071F, 0.675F, 0.625F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, -1.9098F, 0.2425F, 2.0971F));

		PartDefinition jaw_r38 = jaw.addOrReplaceChild("jaw_r38",
				CubeListBuilder.create().texOffs(36, 52).addBox(-1.0988F, -1.3459F, -0.4354F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.2209F, -0.3428F, -0.838F));

		PartDefinition jaw_r39 = jaw.addOrReplaceChild("jaw_r39",
				CubeListBuilder.create().texOffs(35, 52).addBox(-1.2759F, -1.5118F, -0.4604F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.2371F, -0.1701F, -0.9014F));

		PartDefinition jaw_r40 = jaw.addOrReplaceChild("jaw_r40", CubeListBuilder.create().texOffs(44, 44).mirror()
				.addBox(-0.9892F, -1.8477F, -0.0104F, 0.675F, 0.625F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.2318F, 0.2425F, -1.0445F));

		PartDefinition jaw_r41 = jaw.addOrReplaceChild("jaw_r41",
				CubeListBuilder.create().texOffs(34, 52).addBox(-1.2378F, -1.3231F, 0.1901F, 0.7F, 0.25F, 0.45F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.1861F, -0.3549F, -0.8261F));

		PartDefinition jaw_r42 = jaw.addOrReplaceChild("jaw_r42",
				CubeListBuilder.create().texOffs(33, 52).addBox(-1.4166F, -1.5147F, 0.1651F, 0.7F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.2047F, -0.1843F, -0.8957F));

		PartDefinition jaw_r43 = jaw.addOrReplaceChild("jaw_r43", CubeListBuilder.create().texOffs(42, 42).mirror()
				.addBox(-1.1156F, -1.9098F, 0.6151F, 0.675F, 0.625F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.2016F, 0.2237F, -1.0515F));

		PartDefinition jaw_r44 = jaw.addOrReplaceChild("jaw_r44", CubeListBuilder.create().texOffs(40, 40).mirror()
				.addBox(-0.8433F, -2.1597F, 1.1435F, 0.675F, 0.625F, 0.35F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.0248F, 0.1075F, -1.0808F));

		PartDefinition jaw_r45 = jaw.addOrReplaceChild("jaw_r45",
				CubeListBuilder.create().texOffs(32, 52).addBox(-1.2755F, -1.8562F, 0.6935F, 11.0F, 0.3F, 0.5F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.6977F, -1.0668F, 0.0079F, 1.0063F, -0.2649F, -0.8514F));

		PartDefinition rightupperarm = upperbody.addOrReplaceChild("rightupperarm", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-1.2351F, -10.3249F, 2.7245F, 0.0873F, 0.0F, 0.0F));

		PartDefinition rightupperarm_r1 = rightupperarm.addOrReplaceChild("rightupperarm_r1",
				CubeListBuilder.create().texOffs(2, 7).addBox(0.0052F, -11.9353F, 0.5151F, 1.0F, 12.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0019F, 11.281F, 0.0642F, 0.0828F, -0.0036F, 0.0435F));

		PartDefinition rightupperarm_r2 = rightupperarm.addOrReplaceChild("rightupperarm_r2",
				CubeListBuilder.create().texOffs(16, 30).addBox(-0.7401F, -11.9078F, 0.5151F, 1.0F, 12.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0019F, 11.281F, 0.0642F, 0.0829F, 0.0F, 0.0F));

		PartDefinition rightlowerarm = rightupperarm.addOrReplaceChild("rightlowerarm", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 11.181F, 1.1642F, -0.0873F, 0.0F, 0.0F));

		PartDefinition rightlowerarm_r1 = rightlowerarm.addOrReplaceChild("rightlowerarm_r1",
				CubeListBuilder.create().texOffs(31, 13).addBox(-0.5101F, -20.6202F, 10.1838F, 1.5F, 5.7F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 22.8302F, -4.0922F, 0.3054F, 0.0F, 0.0F));

		PartDefinition righthand = rightlowerarm.addOrReplaceChild("righthand", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-0.0291F, 8.005F, 1.742F, 0.0F, 1.6581F, 0.0F));

		PartDefinition leftupperarm = upperbody.addOrReplaceChild("leftupperarm", CubeListBuilder.create(),
				PartPose.offsetAndRotation(-0.9351F, -9.9249F, -3.2755F, -0.0873F, 0.0F, 0.0F));

		PartDefinition leftupperarm_r1 = leftupperarm.addOrReplaceChild("leftupperarm_r1",
				CubeListBuilder.create().texOffs(4, 47).addBox(-0.4F, -1.5F, -0.4F, 0.8F, 3.0F, 0.8F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.4836F, 10.3741F, -0.9198F, -0.2614F, -0.3672F, -0.0787F));

		PartDefinition leftupperarm_r2 = leftupperarm.addOrReplaceChild("leftupperarm_r2",
				CubeListBuilder.create().texOffs(9, 34).addBox(-0.4493F, -15.1798F, -1.5831F, 1.0F, 9.9F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.0518F, 14.8547F, -0.0842F, -0.0644F, -0.4833F, -0.0058F));

		PartDefinition leftlowerarm = leftupperarm.addOrReplaceChild("leftlowerarm", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.8543F, 11.7342F, -1.0041F, 0.0868F, -0.0091F, 0.1043F));

		PartDefinition leftlowerarm_r1 = leftlowerarm.addOrReplaceChild("leftlowerarm_r1",
				CubeListBuilder.create().texOffs(41, 48).mirror()
						.addBox(0.1984F, -4.1989F, 0.5161F, 0.7F, 3.2F, 0.7F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.5435F, 3.8358F, -1.3369F, -0.0653F, -0.4066F, -0.1708F));

		PartDefinition lowerlowerLeftArm = leftlowerarm.addOrReplaceChild("lowerlowerLeftArm", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.719F, -5.204F, 1.1698F, 0.0F, 0.8727F, 0.0F));

		PartDefinition lefthand = lowerlowerLeftArm.addOrReplaceChild("lefthand", CubeListBuilder.create(),
				PartPose.offset(0.4056F, 8.418F, -1.1037F));

		PartDefinition lefthand_r1 = lefthand.addOrReplaceChild("lefthand_r1",
				CubeListBuilder.create().texOffs(33, 47).mirror()
						.addBox(-1.3498F, -1.2119F, -0.5144F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 2.126F, -0.0163F, 2.29F));

		PartDefinition lefthand_r2 = lefthand.addOrReplaceChild("lefthand_r2",
				CubeListBuilder.create().texOffs(25, 38).mirror()
						.addBox(0.5358F, -1.1404F, -0.4144F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 2.144F, -0.2379F, 2.1491F));

		PartDefinition lefthand_r3 = lefthand.addOrReplaceChild("lefthand_r3",
				CubeListBuilder.create().texOffs(17, 33).addBox(-0.3723F, -1.1149F, -0.4644F, 1.125F, 0.375F, 0.35F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 2.1344F, -0.1643F, 2.197F));

		PartDefinition lefthand_r4 = lefthand.addOrReplaceChild("lefthand_r4", CubeListBuilder.create().texOffs(45, 43)
				.addBox(-2.4537F, -0.9774F, -0.5517F, 1.525F, 0.85F, 1.75F, new CubeDeformation(0.0F)).texOffs(32, 42)
				.mirror().addBox(-1.4115F, -0.8431F, 0.6733F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
				.mirror(false).texOffs(31, 39).mirror()
				.addBox(-1.4115F, -0.8431F, 0.0733F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(30, 36).mirror()
				.addBox(-1.4115F, -0.8431F, -0.5017F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.7174F, -0.1079F, 1.6969F));

		PartDefinition lefthand_r5 = lefthand.addOrReplaceChild("lefthand_r5", CubeListBuilder.create().texOffs(16, 49)
				.addBox(-0.369F, -0.7409F, 0.7233F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).texOffs(15, 28)
				.addBox(-0.369F, -0.7409F, 0.1233F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).texOffs(14, 25)
				.addBox(-0.369F, -0.7409F, -0.4517F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.6953F, -0.2214F, 1.8315F));

		PartDefinition lefthand_r6 = lefthand.addOrReplaceChild("lefthand_r6", CubeListBuilder.create().texOffs(24, 36)
				.mirror().addBox(0.5717F, -0.7682F, 0.7733F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F))
				.mirror(false).texOffs(21, 30).mirror()
				.addBox(0.5717F, -0.7682F, 0.1733F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(20, 28).mirror()
				.addBox(0.5717F, -0.7682F, -0.4017F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.6781F, -0.2767F, 1.9011F));

		PartDefinition lefthand_r7 = lefthand.addOrReplaceChild("lefthand_r7", CubeListBuilder.create().texOffs(23, 34)
				.mirror().addBox(1.0709F, -0.7843F, 0.7983F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F))
				.mirror(false).texOffs(22, 32).mirror()
				.addBox(1.0709F, -0.7843F, 0.1983F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(19, 26).mirror()
				.addBox(1.0709F, -0.7843F, -0.3767F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.2587F, 1.9086F, 0.5196F, 0.6679F, -0.3039F, 1.9367F));

		PartDefinition upperleftleg = lowerbody.addOrReplaceChild("upperleftleg", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 1.2F, -2.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition upperleftleg_r1 = upperleftleg.addOrReplaceChild("upperleftleg_r1",
				CubeListBuilder.create().texOffs(0, 47).mirror()
						.addBox(-0.3F, -3.3002F, 0.0311F, 2.0F, 6.9F, 0.65F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(34, 27)
						.addBox(-0.3F, -3.3002F, -0.2689F, 2.0F, 6.9F, 0.95F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.1046F, 2.2076F, 0.029F, -0.1396F, 0.0F, 0.0F));

		PartDefinition upperleftleg_r2 = upperleftleg.addOrReplaceChild("upperleftleg_r2",
				CubeListBuilder.create().texOffs(18, 26).mirror()
						.addBox(-0.3F, -3.4F, -0.2483F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.1046F, 2.3046F, -0.5F, -0.0175F, 0.0F, 0.0F));

		PartDefinition lowerleftleg = upperleftleg.addOrReplaceChild("lowerleftleg",
				CubeListBuilder.create().texOffs(42, 2).addBox(-0.028F, 4.57F, -0.6F, 1.2F, 2.425F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.1046F, 5.8046F, 0.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition lowerleftleg_r1 = lowerleftleg.addOrReplaceChild("lowerleftleg_r1",
				CubeListBuilder.create().texOffs(27, 47).addBox(0.8142F, -4.1223F, -0.694F, 0.8F, 4.9F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.3915F, 3.6442F, 0.0997F, 0.0436F, 0.0F, 0.1484F));

		PartDefinition lowerleftleg_r2 = lowerleftleg.addOrReplaceChild("lowerleftleg_r2",
				CubeListBuilder.create().texOffs(21, 12).mirror()
						.addBox(0.4009F, -3.8382F, -0.694F, 0.7F, 4.9F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-0.3915F, 3.6442F, 0.0997F, 0.0426F, 0.0094F, -0.0696F));

		PartDefinition leftfoot = lowerleftleg.addOrReplaceChild("leftfoot", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 8.0F, 0.3F, 0.0F, 0.0F, 0.1745F));

		PartDefinition leftfoot_r1 = leftfoot.addOrReplaceChild("leftfoot_r1", CubeListBuilder.create().texOffs(0, 43)
				.addBox(-2.3729F, -0.5866F, -0.862F, 1.525F, 0.85F, 1.75F, new CubeDeformation(0.0F)).texOffs(36, 11)
				.mirror().addBox(-1.3306F, -0.4522F, 0.363F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
				.mirror(false).texOffs(35, 48).mirror()
				.addBox(-1.3306F, -0.4522F, -0.237F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(34, 1).mirror()
				.addBox(-1.3306F, -0.4522F, -0.812F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.716F, -1.1488F, -0.4411F, -3.1344F, 0.0428F, 3.1067F));

		PartDefinition leftfoot_r2 = leftfoot.addOrReplaceChild("leftfoot_r2", CubeListBuilder.create().texOffs(20, 41)
				.addBox(-0.2214F, -0.3701F, -0.762F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).texOffs(19, 49)
				.addBox(-0.2214F, -0.3701F, -0.187F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)).texOffs(18, 36)
				.addBox(-0.2214F, -0.3701F, 0.413F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.716F, -1.1488F, -0.4411F, 3.1412F, 0.0434F, 2.932F));

		PartDefinition leftfoot_r3 = leftfoot.addOrReplaceChild("leftfoot_r3", CubeListBuilder.create().texOffs(31, 50)
				.mirror().addBox(1.2656F, -0.4359F, 0.488F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(28, 44).mirror()
				.addBox(1.2656F, -0.4359F, -0.112F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(27, 42).mirror()
				.addBox(1.2656F, -0.4359F, -0.687F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.716F, -1.1488F, -0.4411F, 3.1355F, 0.043F, 2.8009F));

		PartDefinition leftfoot_r4 = leftfoot.addOrReplaceChild("leftfoot_r4",
				CubeListBuilder.create().texOffs(30, 48).mirror()
						.addBox(0.751F, -0.4116F, 0.463F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(29, 46).mirror()
						.addBox(0.751F, -0.4116F, -0.137F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(26, 40).mirror()
						.addBox(0.751F, -0.4116F, -0.712F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-1.716F, -1.1488F, -0.4411F, 3.1374F, 0.0432F, 2.8446F));

		PartDefinition upperrightleg = lowerbody.addOrReplaceChild("upperrightleg",
				CubeListBuilder.create().texOffs(5, 29).mirror()
						.addBox(-0.2216F, -0.7966F, -0.3F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0F, 0.9F, 2.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition upperrightleg_r1 = upperrightleg.addOrReplaceChild("upperrightleg_r1",
				CubeListBuilder.create().texOffs(1, 4).addBox(-0.3F, -3.2177F, -0.2322F, 2.0F, 6.8F, 0.65F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0784F, 2.513F, -0.3788F, 0.1745F, 0.0F, 0.0F));

		PartDefinition lowerrightleg = upperrightleg.addOrReplaceChild("lowerrightleg", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0784F, 6.1034F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition lowerrightleg_r1 = lowerrightleg.addOrReplaceChild("lowerrightleg_r1",
				CubeListBuilder.create().texOffs(33, 17).addBox(0.117F, -4.0205F, -0.4602F, 0.7F, 4.9F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0272F, 3.5592F, 0.2843F, 0.0237F, 0.0197F, -0.0618F));

		PartDefinition lowerrightleg_r2 = lowerrightleg.addOrReplaceChild("lowerrightleg_r2",
				CubeListBuilder.create().texOffs(29, 6).addBox(0.1448F, 0.4608F, -0.1593F, 1.2F, 2.525F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0272F, 3.5592F, 0.0343F, -0.0182F, 0.0123F, 0.008F));

		PartDefinition lowerrightleg_r3 = lowerrightleg.addOrReplaceChild("lowerrightleg_r3",
				CubeListBuilder.create().texOffs(30, 47).mirror()
						.addBox(0.4956F, -4.2267F, -0.4602F, 0.8F, 4.9F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0272F, 3.5592F, 0.2843F, 0.0272F, 0.0145F, 0.1389F));

		PartDefinition rightfoot = lowerrightleg.addOrReplaceChild("rightfoot", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.1F, 8.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition rightfoot_r1 = rightfoot.addOrReplaceChild("rightfoot_r1",
				CubeListBuilder.create().texOffs(39, 19).mirror()
						.addBox(-1.1325F, -0.4702F, -0.8091F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.mirror(false).texOffs(38, 48).mirror()
						.addBox(-1.1325F, -0.4702F, -0.2341F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.mirror(false).texOffs(37, 14).mirror()
						.addBox(-1.1325F, -0.4702F, 0.3659F, 1.325F, 0.475F, 0.45F, new CubeDeformation(0.0F))
						.mirror(false).texOffs(3, 26).mirror()
						.addBox(-2.6723F, -0.565F, -0.8633F, 1.525F, 0.85F, 1.75F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.64F, -1.1599F, 0.4589F, -3.1314F, -0.0444F, 3.1066F));

		PartDefinition rightfoot_r2 = rightfoot.addOrReplaceChild("rightfoot_r2", CubeListBuilder.create()
				.texOffs(23, 50).addBox(0.0743F, -0.4216F, -0.7627F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F))
				.texOffs(22, 49).addBox(0.0743F, -0.4216F, -0.1877F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F))
				.texOffs(21, 44).addBox(0.0743F, -0.4216F, 0.4123F, 1.125F, 0.375F, 0.35F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.64F, -1.1599F, 0.4589F, -3.1238F, -0.0419F, 2.932F));

		PartDefinition rightfoot_r3 = rightfoot.addOrReplaceChild("rightfoot_r3",
				CubeListBuilder.create().texOffs(37, 54).mirror()
						.addBox(1.5521F, -0.5255F, -0.6877F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F))
						.mirror(false).texOffs(33, 54).mirror()
						.addBox(1.5521F, -0.5255F, 0.4873F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(32, 52).mirror()
						.addBox(1.5521F, -0.5255F, -0.1127F, 0.65F, 0.25F, 0.2F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.64F, -1.1599F, 0.4589F, -3.1185F, -0.0392F, 2.801F));

		PartDefinition rightfoot_r4 = rightfoot.addOrReplaceChild("rightfoot_r4",
				CubeListBuilder.create().texOffs(36, 54).mirror()
						.addBox(1.0412F, -0.4887F, -0.7127F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F))
						.mirror(false).texOffs(35, 54).mirror()
						.addBox(1.0412F, -0.4887F, -0.1377F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F))
						.mirror(false).texOffs(34, 54).mirror()
						.addBox(1.0412F, -0.4887F, 0.4623F, 0.65F, 0.3F, 0.25F, new CubeDeformation(0.0F))
						.mirror(false),
				PartPose.offsetAndRotation(-1.64F, -1.1599F, 0.4589F, -3.1202F, -0.0402F, 2.8446F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.lefthand.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.lefthand.xRot = headPitch / (180F / (float) Math.PI);
		this.leftfoot.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
		this.jaw.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.jaw.xRot = headPitch / (180F / (float) Math.PI);
		this.rightfoot.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
		this.neck.yRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
		this.rightupperarm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		this.rightlowerarm.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
		this.head.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.head.xRot = headPitch / (180F / (float) Math.PI);
		this.upperleftleg.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
		this.lowerleftleg.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
		this.leftupperarm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
		this.leftlowerarm.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
		this.lowerlowerLeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
	}
}