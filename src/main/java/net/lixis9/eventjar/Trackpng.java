package net.lixis9.eventjar;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Trackpng {
    private static final List<BlockPos> trackedBlocks = new ArrayList<>();
    private static final int MAX_MARKERS = 5;
    private static final double ACTIVATION_DISTANCE = 3.0;
    private static int tickCounter = 0;

    public Trackpng() {
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        new Trackpng();
    }

    @Mod.EventBusSubscriber
    private static class ForgeBusEvents {
        @SubscribeEvent
        public static void serverLoad(ServerStartingEvent event) {
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tickCounter++;
                if (tickCounter >= 40) {
                    tickCounter = 0;
                    updateTrackedBlocks();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void updateTrackedBlocks() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        trackedBlocks.removeIf(pos ->
            mc.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < ACTIVATION_DISTANCE * ACTIVATION_DISTANCE
        );

        trackedBlocks.removeIf(pos -> !isInViewFrustum(mc, pos));

        trackedBlocks.removeIf(pos -> mc.level.getBlockState(pos).getBlock() != Blocks.GRASS_BLOCK);

        if (trackedBlocks.size() < MAX_MARKERS) {
            findNewGrassBlock(mc);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void findNewGrassBlock(Minecraft mc) {
        int range = 20;
        BlockPos playerPos = mc.player.blockPosition();
        List<BlockPos> candidates = new ArrayList<>();
        List<BlockPos> inViewCandidates = new ArrayList<>();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    if (mc.level.getBlockState(pos).getBlock() == Blocks.GRASS_BLOCK &&
                        !trackedBlocks.contains(pos)) {

                        if (isInViewFrustum(mc, pos)) {
                            inViewCandidates.add(pos);
                        } else {
                            candidates.add(pos);
                        }
                    }
                }
            }
        }

        if (!inViewCandidates.isEmpty()) {
            Collections.shuffle(inViewCandidates);
            trackedBlocks.add(inViewCandidates.get(0));
        } else if (!candidates.isEmpty()) {
            Collections.shuffle(candidates);
            trackedBlocks.add(candidates.get(0));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean isInViewFrustum(Minecraft mc, BlockPos pos) {
        Vec3 blockVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3 playerVec = mc.player.getEyePosition(1.0f);
        Vec3 direction = blockVec.subtract(playerVec);

        double distance = direction.length();
        if (distance > 50.0) return false;

        direction = direction.normalize();
        Vec3 lookVec = mc.player.getViewVector(1.0f);

        double dotProduct = direction.dot(lookVec);
        if (dotProduct < 0.3) return false;

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderTrackingMarker(PoseStack poseStack, Minecraft mc, BlockPos pos) {
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        double x = pos.getX() - cameraPos.x + 0.5;
        double y = pos.getY() - cameraPos.y + 1.1;
        double z = pos.getZ() - cameraPos.z + 0.5;

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());
        poseStack.scale(0.8f, 0.8f, 0.8f);

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -0.5f, -0.5f, 0).uv(0, 1).endVertex();
        buffer.vertex(matrix, -0.5f, 0.5f, 0).uv(0, 0).endVertex();
        buffer.vertex(matrix, 0.5f, 0.5f, 0).uv(1, 0).endVertex();
        buffer.vertex(matrix, 0.5f, -0.5f, 0).uv(1, 1).endVertex();

        Tesselator.getInstance().end();
        poseStack.popPose();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class RenderEventHandler {
        @SubscribeEvent
        public static void renderWorldLast(net.minecraftforge.client.event.RenderLevelStageEvent event) {
            if (event.getStage() != net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
                return;
            }

            PoseStack poseStack = event.getPoseStack();
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null || trackedBlocks.isEmpty()) return;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, new ResourceLocation("eventjar", "textures/screens/tracking.png"));

            for (BlockPos pos : trackedBlocks) {
                renderTrackingMarker(poseStack, mc, pos);
            }

            RenderSystem.disableBlend();
        }
    }
}
