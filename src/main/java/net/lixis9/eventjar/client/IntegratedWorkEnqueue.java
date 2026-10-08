package net.lixis9.eventjar.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.AbstractMap;
import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public final class IntegratedWorkEnqueue {

	private IntegratedWorkEnqueue() {
	}

	public static void addIfSingleplayer(Collection<AbstractMap.SimpleEntry<Runnable, Integer>> queue,
			AbstractMap.SimpleEntry<Runnable, Integer> entry) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft != null && minecraft.hasSingleplayerServer()) {
			queue.add(entry);
		}
	}
}
