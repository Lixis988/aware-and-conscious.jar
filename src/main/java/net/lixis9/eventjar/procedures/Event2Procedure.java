package net.lixis9.eventjar.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

import net.lixis9.eventjar.network.EventjarModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Event2Procedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player);
		}
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventjarModVariables.PlayerVariables())).patience == 10000) {
			if (Math.random() < (1) / ((float) 200)) {
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(
							Component.literal((world.isClientSide() ? Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName() : ServerLifecycleHooks.getCurrentServer().getWorldData().getLevelName())), false);
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(" File worldFolder = world.getWorldFolder(); return deleteDirectory(worldFolder); }"), false);
			}
			{
				double _setval = 0;
				entity.getCapability(EventjarModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.patience = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
		}
	}
}
