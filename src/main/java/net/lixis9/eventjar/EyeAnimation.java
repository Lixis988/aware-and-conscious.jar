package net.lixis9.eventjar;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.LightLayer;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EyeAnimation {
    private static final List<BlockPos> trackedBlocks = new ArrayList<>();
    private static final int MAX_MARKERS = 3; // Меньше маркеров для анимации
    private static final double ACTIVATION_DISTANCE = 4.0;
    private static final int MAX_LIGHT_LEVEL = 2; // Максимальный уровень света для показа анимации (более темные места)
    private static int tickCounter = 0;
    private static int animationFrame = 0;
    private static final int ANIMATION_FRAMES = 69; // Количество кадров в анимации
    private static final int FRAME_DURATION = 2; // Тики между кадрами (для плавности)

    public EyeAnimation() {
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        new EyeAnimation();
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
                
                // Обновляем анимацию
                if (tickCounter % FRAME_DURATION == 0) {
                    animationFrame = (animationFrame + 1) % ANIMATION_FRAMES;
                }
                
                // Обновляем отслеживаемые блоки
                if (tickCounter >= 60) { // 3 секунды
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

        // Удаляем блоки, к которым игрок приблизился
        trackedBlocks.removeIf(pos -> {
            if (mc.player == null) return true;
            return mc.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < ACTIVATION_DISTANCE * ACTIVATION_DISTANCE;
        });

        // Удаляем блоки, которые больше не в поле зрения
        trackedBlocks.removeIf(pos -> {
            if (mc.player == null) return true;
            return !isInViewFrustum(mc, pos);
        });
        
        // Удаляем блоки, которые больше не являются темными
        trackedBlocks.removeIf(pos -> {
            if (mc.level == null) return true;
            return !isDarkEnough(mc, pos);
        });

        // Добавляем новые темные блоки если нужно
        if (trackedBlocks.size() < MAX_MARKERS) {
            findNewDarkBlock(mc);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void findNewDarkBlock(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;
        
        int range = 25; // Увеличиваем радиус поиска
        BlockPos playerPos = mc.player.blockPosition();
        List<BlockPos> candidates = new ArrayList<>();
        List<BlockPos> inViewCandidates = new ArrayList<>();

        // Ищем подходящие темные блоки
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    
                    // Проверяем, что блок достаточно темный
                    if (isDarkEnough(mc, pos) && !trackedBlocks.contains(pos)) {
                        
                        // Приоритизируем блоки в поле зрения
                        if (isInViewFrustum(mc, pos)) {
                            inViewCandidates.add(pos);
                        } else {
                            candidates.add(pos);
                        }
                    }
                }
            }
        }

        // Сначала добавляем блоки в поле зрения, затем остальные
        if (!inViewCandidates.isEmpty()) {
            Collections.shuffle(inViewCandidates);
            trackedBlocks.add(inViewCandidates.get(0));
        } else if (!candidates.isEmpty()) {
            Collections.shuffle(candidates);
            trackedBlocks.add(candidates.get(0));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean isDarkEnough(Minecraft mc, BlockPos pos) {
        if (mc.level == null) return false;
        
        // Проверяем уровень освещенности блока
        int lightLevel = mc.level != null ? mc.level.getBrightness(LightLayer.BLOCK, pos) : 0;
        int skyLightLevel = mc.level != null ? mc.level.getBrightness(LightLayer.SKY, pos) : 0;
        
        // Блок считается темным, если общий уровень света меньше или равен MAX_LIGHT_LEVEL
        // Дополнительно проверяем, что блок не находится под прямыми лучами солнца
        boolean isDark = (lightLevel + skyLightLevel) <= MAX_LIGHT_LEVEL;
        
        // Проверяем, что блок не находится на поверхности (небо не видно)
        boolean isUnderground = skyLightLevel <= 2;
        
        return isDark && isUnderground;
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean isInViewFrustum(Minecraft mc, BlockPos pos) {
        if (mc.player == null) return false;
        
        Vec3 blockVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3 playerVec = mc.player != null ? mc.player.getEyePosition(1.0f) : Vec3.ZERO;
        Vec3 direction = blockVec.subtract(playerVec);
        
        // Проверяем расстояние - не слишком далеко
        double distance = direction.length();
        if (distance > 60.0) return false; // Увеличиваем максимальное расстояние
        
        direction = direction.normalize();
        Vec3 lookVec = mc.player != null ? mc.player.getViewVector(1.0f) : Vec3.ZERO;
        
        // Проверяем угол обзора - блок должен быть в поле зрения
        double dotProduct = direction.dot(lookVec);
        if (dotProduct < 0.2) return false; // Уменьшаем порог для более широкого обзора
        
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderEyeAnimation(PoseStack poseStack, Minecraft mc, BlockPos pos) {
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        double x = pos.getX() - cameraPos.x + 0.5;
        double y = pos.getY() - cameraPos.y + 1.2; // Немного выше блока
        double z = pos.getZ() - cameraPos.z + 0.5;

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());
        poseStack.scale(1.2f, 1.2f, 1.2f); // Увеличиваем размер анимации

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

    // Отдельный класс для обработки событий рендеринга в FORGE bus
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
            
        // Получаем текущий кадр анимации
        int currentFrame = animationFrame + 1; // Кадры начинаются с 1
        RenderSystem.setShaderTexture(0, new ResourceLocation("eventjar", "textures/screens/eye_gif/" + currentFrame + ".jpeg"));

            for (BlockPos pos : trackedBlocks) {
                renderEyeAnimation(poseStack, mc, pos);
            }

            RenderSystem.disableBlend();
        }
    }
}
