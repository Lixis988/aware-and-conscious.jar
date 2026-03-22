package net.lixis9.eventjar.procedures;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class TwitchHeadLayerProcedure extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public TwitchHeadLayerProcedure(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        HeadTwitchClient.TwitchData data = HeadTwitchClient.getTwitchData(player.getUUID());
        if (data == null) return;

        poseStack.pushPose();
        // Применяем трансформации к модели головы
        this.getParentModel().head.translateAndRotate(poseStack);
        poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(data.currentOffset)));
        
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(player.getSkinTextureLocation()));
        this.getParentModel().head.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}