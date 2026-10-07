package net.lixis9.eventjar.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.lixis9.eventjar.entity.ScaryDefaultVillagerEntity;

public class ScaryDefaultVillagerModel<T extends ScaryDefaultVillagerEntity> extends HumanoidModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("eventjar", "scary_default_villager"), "main");

	public ScaryDefaultVillagerModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
}
