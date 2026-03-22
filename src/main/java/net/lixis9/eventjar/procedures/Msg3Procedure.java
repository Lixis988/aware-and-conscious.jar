package net.lixis9.eventjar.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.ServerChatEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import net.lixis9.eventjar.EventjarMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Msg3Procedure {
	@SubscribeEvent
	public static void onChat(ServerChatEvent event) {
		execute(event, event.getPlayer().level(), event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ(), event.getRawText());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, String text) {
		execute(null, world, x, y, z, text);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, String text) {
		if (text == null)
			return;
		if (text.contains("leave me alone")) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("fine"), false);
			if (world instanceof Level _level) {
				if (_level.isClientSide()) {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("eventjar:bs")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			EventjarMod.queueServerWork(100, () -> {
				if (!world.isClientSide() && world.getServer() != null)
					ServerLifecycleHooks.getCurrentServer().stopServer();
			});
		}
	}
}
