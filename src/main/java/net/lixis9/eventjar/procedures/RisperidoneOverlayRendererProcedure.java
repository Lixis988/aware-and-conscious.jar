package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.network.EventjarModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = EventjarMod.MODID, value = Dist.CLIENT)
public class RisperidoneOverlayRendererProcedure {

    private static final float START_THRESHOLD = 20000.0f;
    private static final float MAX_THRESHOLD = 24000.0f;
    private static final Random RANDOM = new Random();
    private static final ResourceLocation NOISE_SHADER = new ResourceLocation(EventjarMod.MODID, "shaders/post/noise.json");

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(variables -> {
            float patience = (float) variables.PatienceForRisperidone;

            if (patience < START_THRESHOLD) {
                if (isNoiseEffect(mc.gameRenderer.currentEffect())) {
                    mc.gameRenderer.shutdownEffect();
                }
                return;
            }

            float progress = (patience - START_THRESHOLD) / (MAX_THRESHOLD - START_THRESHOLD);
            progress = Mth.clamp(progress, 0.0f, 1.0f);

            PostChain currentEffect = mc.gameRenderer.currentEffect();
            if (currentEffect == null) {
                try {
                    mc.gameRenderer.loadEffect(NOISE_SHADER);
                } catch (Exception e) {
                    EventjarMod.LOGGER.error("Ошибка при загрузке шейдера: " + e.getMessage());
                }
            }

            float yawJitter = (RANDOM.nextFloat() - 0.5f) * 2.0f * progress;
            float pitchJitter = (RANDOM.nextFloat() - 0.5f) * 2.0f * progress;
            mc.player.setYRot(mc.player.getYRot() + yawJitter);
            mc.player.setXRot(mc.player.getXRot() + pitchJitter);
        });
    }

    private static boolean isNoiseEffect(PostChain effect) {
        return effect != null && NOISE_SHADER.toString().equals(effect.getName());
    }
}
