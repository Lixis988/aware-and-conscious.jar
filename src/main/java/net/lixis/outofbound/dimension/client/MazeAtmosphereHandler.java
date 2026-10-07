package net.lixis.outofbound.dimension.client;

import net.lixis.outofbound.OutofboundMod;
import net.lixis.outofbound.dimension.MazeDimensions;
import net.lixis.outofbound.dimension.theme.DimensionTheme;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID, value = Dist.CLIENT)
public final class MazeAtmosphereHandler {

	private MazeAtmosphereHandler() {
	}

	@SubscribeEvent
	public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
		DimensionTheme theme = currentTheme();
		if (theme == null) {
			return;
		}
		event.setRed(theme.fogRed());
		event.setGreen(theme.fogGreen());
		event.setBlue(theme.fogBlue());
	}

	@SubscribeEvent
	public static void onRenderFog(ViewportEvent.RenderFog event) {
		DimensionTheme theme = currentTheme();
		if (theme == null) {
			return;
		}
		event.setNearPlaneDistance(theme.fogStart);
		event.setFarPlaneDistance(theme.fogEnd);
		event.setCanceled(true);
	}

	private static DimensionTheme currentTheme() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return null;
		}
		long index = MazeDimensions.indexFromLocation(mc.level.dimension().location());
		if (index < 0L) {
			return null;
		}
		return DimensionTheme.forIndex(index);
	}
}
