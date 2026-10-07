package net.lixis9.eventjar.procedures;

import net.lixis9.eventjar.EventjarMod;
import net.lixis9.eventjar.network.ClientOsEffectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber
public class TxtondesktopProcedure {

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			ClientOsEffectPacket.send(player, ClientOsEffectPacket.Effect.TXT_DESKTOP);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void execute() {
		try {
			Path desktop = WindowsDesktopPaths.resolveDesktop();
			Files.createDirectories(desktop);
			Path file = desktop.resolve("hello.txt");
			String gameDir = Minecraft.getInstance().gameDirectory.getAbsolutePath();
			String content = String.join("\r\n",
					"Hi, I'm meetboy :3333, did you miss me? Anyway, your minecraft is here:",
					gameDir,
					"sorry i accidentally deleted a couple of files there :33333",
					""
			);
			Files.writeString(file, content, StandardCharsets.UTF_8);
		} catch (Exception e) {
			EventjarMod.LOGGER.warn("TxtondesktopProcedure failed: {}", e.toString());
		}
	}
}
