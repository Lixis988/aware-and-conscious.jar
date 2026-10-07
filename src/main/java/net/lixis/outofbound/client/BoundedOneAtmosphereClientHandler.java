package net.lixis.outofbound.client;

import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class BoundedOneAtmosphereClientHandler {

	private static final String FLICKER_TITLE = "outofbound.exe";
	private static final String ALT_TITLE = "winload.exe";

	private BoundedOneAtmosphereClientHandler() {
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		BoundedOneAtmosphereClientState.clientTick();

		if (BoundedOneAtmosphereClientState.isTitleFlickerActive() && minecraft.getWindow() != null) {
			if (!BoundedOneAtmosphereClientState.isTitleOverridden()) {
				BoundedOneAtmosphereClientState.setSavedTitle(FLICKER_TITLE);
				BoundedOneAtmosphereClientState.setTitleOverridden(true);
			}
			long time = minecraft.level == null ? System.currentTimeMillis() / 250L : minecraft.level.getGameTime() / 4L;
			minecraft.getWindow().setTitle((time & 1L) == 0L ? FLICKER_TITLE : ALT_TITLE);
		} else if (BoundedOneAtmosphereClientState.isTitleOverridden() && minecraft.getWindow() != null) {
			String saved = BoundedOneAtmosphereClientState.getSavedTitle();
			if (saved != null) {
				minecraft.getWindow().setTitle(saved);
			} else {
				minecraft.getWindow().setTitle(FLICKER_TITLE);
			}
			BoundedOneAtmosphereClientState.setTitleOverridden(false);
			BoundedOneAtmosphereClientState.setSavedTitle(null);
		}
	}

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (!BoundedOneAtmosphereClientState.isGlitching()) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) {
			return;
		}

		float time = minecraft.level.getGameTime() + event.getPartialTick();
		int seed = BoundedOneAtmosphereClientState.getGlitchSeed();

		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
			BoundedcowGlitchEffect.beginTextureGlitch(time, seed, BoundedOneAtmosphereClientState.getGlitchIntensity());
		}
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
			BoundedcowGlitchEffect.endTextureGlitch();
		}
	}
}
