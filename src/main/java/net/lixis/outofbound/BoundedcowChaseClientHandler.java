package net.lixis.outofbound;

import net.lixis.outofbound.client.BoundedcowWindowShaker;
import net.lixis.outofbound.entity.BoundedcowEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BoundedcowChaseClientHandler {

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null || minecraft.level == null) {
			BoundedcowWindowShaker.reset();
			return;
		}

		AABB searchBox = player.getBoundingBox().inflate(64.0D);
		List<BoundedcowEntity> cows = minecraft.level.getEntitiesOfClass(BoundedcowEntity.class, searchBox, cow -> !cow.isRemoved());

		boolean chased = false;
		for (BoundedcowEntity cow : cows) {
			if (cow.getTarget() == player) {
				chased = true;
				break;
			}
		}

		if (chased) {
			BoundedcowWindowShaker.tick();
		} else {
			BoundedcowWindowShaker.reset();
		}
	}
}
