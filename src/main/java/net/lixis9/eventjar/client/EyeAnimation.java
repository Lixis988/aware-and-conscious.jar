package net.lixis9.eventjar.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.lixis9.eventjar.EventjarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class EyeAnimation {

	private static final int FRAME_COUNT = 69;
	private static final ResourceLocation[] FRAMES = new ResourceLocation[FRAME_COUNT];
	private static final int MAX_MARKERS = 3;
	private static final double ACTIVATION_DISTANCE = 4.0;
	private static final int MAX_LIGHT_LEVEL = 4;
	private static final int FRAME_DURATION = 2;
	private static final int SEARCH_RANGE = 25;
	private static final int SEARCH_ATTEMPTS = 96;

	private static final List<BlockPos> trackedBlocks = new ArrayList<>();
	private static final Random random = new Random();
	private static int tickCounter;
	private static int animationFrame;

	static {
		for (int i = 0; i < FRAME_COUNT; i++) {
			FRAMES[i] = new ResourceLocation(EventjarMod.MODID, "textures/screens/eye_gif/" + (i + 1) + ".jpeg");
		}
	}

	private EyeAnimation() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		tickCounter++;
		if (tickCounter % FRAME_DURATION == 0) {
			animationFrame = (animationFrame + 1) % FRAME_COUNT;
		}
		if (tickCounter >= 40) {
			tickCounter = 0;
			updateTrackedBlocks();
		}
	}

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null || trackedBlocks.isEmpty()) {
			return;
		}

		List<BlockPos> snapshot = new ArrayList<>(trackedBlocks);
		PoseStack poseStack = event.getPoseStack();
		MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
		RenderType renderType = RenderType.eyes(FRAMES[animationFrame]);
		VertexConsumer consumer = buffer.getBuffer(renderType);
		for (BlockPos pos : snapshot) {
			renderEye(poseStack, mc, pos, consumer);
		}
		buffer.endBatch(renderType);
	}

	private static void updateTrackedBlocks() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null) {
			trackedBlocks.clear();
			return;
		}

		trackedBlocks.removeIf(pos -> mc.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < ACTIVATION_DISTANCE * ACTIVATION_DISTANCE
				|| !isDarkEnough(mc, pos)
				|| !isInViewFrustum(mc, pos));

		int attempts = 0;
		while (trackedBlocks.size() < MAX_MARKERS && attempts < SEARCH_ATTEMPTS) {
			attempts++;
			BlockPos pos = mc.player.blockPosition().offset(
					random.nextInt(SEARCH_RANGE * 2 + 1) - SEARCH_RANGE,
					random.nextInt(SEARCH_RANGE * 2 + 1) - SEARCH_RANGE,
					random.nextInt(SEARCH_RANGE * 2 + 1) - SEARCH_RANGE);
			if (!trackedBlocks.contains(pos) && isDarkEnough(mc, pos) && isInViewFrustum(mc, pos)) {
				trackedBlocks.add(pos.immutable());
			}
		}
	}

	private static boolean isDarkEnough(Minecraft mc, BlockPos pos) {
		return mc.level != null && mc.level.getMaxLocalRawBrightness(pos) <= MAX_LIGHT_LEVEL;
	}

	private static boolean isInViewFrustum(Minecraft mc, BlockPos pos) {
		if (mc.player == null) {
			return false;
		}
		Vec3 blockVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		Vec3 playerVec = mc.player.getEyePosition(1.0F);
		Vec3 direction = blockVec.subtract(playerVec);
		double distance = direction.length();
		if (distance < 3.0 || distance > 60.0) {
			return false;
		}
		return direction.normalize().dot(mc.player.getViewVector(1.0F)) >= 0.2;
	}

	private static void renderEye(PoseStack poseStack, Minecraft mc, BlockPos pos, VertexConsumer consumer) {
		Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
		poseStack.pushPose();
		poseStack.translate(pos.getX() + 0.5 - cameraPos.x, pos.getY() + 1.15 - cameraPos.y, pos.getZ() + 0.5 - cameraPos.z);
		poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());
		poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		poseStack.scale(1.35F, 1.35F, 1.35F);

		Matrix4f pose = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		float half = 0.5F;
		vertex(consumer, pose, normal, -half, -half, 0.0F, 1.0F);
		vertex(consumer, pose, normal, half, -half, 1.0F, 1.0F);
		vertex(consumer, pose, normal, half, half, 1.0F, 0.0F);
		vertex(consumer, pose, normal, -half, half, 0.0F, 0.0F);
		poseStack.popPose();
	}

	private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float u, float v) {
		consumer.vertex(pose, x, y, 0.0F)
				.color(1.0F, 1.0F, 1.0F, 1.0F)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(LightTexture.FULL_BRIGHT)
				.normal(normal, 0.0F, 0.0F, 1.0F)
				.endVertex();
	}
}
