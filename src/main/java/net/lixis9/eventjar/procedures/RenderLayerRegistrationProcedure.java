package net.lixis9.eventjar.procedures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "eventjar", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class RenderLayerRegistrationProcedure {

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        @SuppressWarnings("unchecked")
        Map<String, PlayerRenderer> skinMap = (Map<String, PlayerRenderer>)(Object) Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
        for (Map.Entry<String, PlayerRenderer> entry : skinMap.entrySet()) {
            PlayerRenderer renderer = entry.getValue();
            renderer.addLayer(new TwitchHeadLayerProcedure(renderer));
            // TwitchHeadLayer added silently for skin
        }
    }
}
