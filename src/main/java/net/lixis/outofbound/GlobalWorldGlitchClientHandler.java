package net.lixis.outofbound;

import net.lixis.outofbound.client.BoundedcowGlitchEffect;
import net.lixis.outofbound.client.WorldProgressClientState;
import net.lixis.outofbound.client.glitch.GlobalWorldGlitchScheduler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class GlobalWorldGlitchClientHandler {

	private GlobalWorldGlitchClientHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}

		boolean sinking = WorldProgressClientState.isSinking();
		long overworldTime = WorldProgressClientState.getOverworldGameTime(minecraft);
		float progress = sinking ? WorldProgressClientState.getSinkProgress(overworldTime) : 0.0F;

		GlobalWorldGlitchScheduler.tick(sinking, sinking, progress);
	}

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (!GlobalWorldGlitchScheduler.isGlitching()) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}

		float time = minecraft.level.getGameTime() + event.getPartialTick();
		int seed = GlobalWorldGlitchScheduler.getGlitchSeed();

		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
			BoundedcowGlitchEffect.beginTextureGlitch(time, seed, GlobalWorldGlitchScheduler.getGlitchIntensity());
		}
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
			BoundedcowGlitchEffect.endTextureGlitch();
		}
	}
}
