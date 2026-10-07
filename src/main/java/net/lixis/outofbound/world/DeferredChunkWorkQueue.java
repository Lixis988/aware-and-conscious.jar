package net.lixis.outofbound.world;

import net.lixis.outofbound.OutofboundMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID)
public final class DeferredChunkWorkQueue {

	private static final int MAX_TASKS_PER_TICK = 1;
	private static final Queue<Runnable> PENDING = new ConcurrentLinkedQueue<>();

	private DeferredChunkWorkQueue() {
	}

	public static void enqueue(Runnable task) {
		PENDING.offer(task);
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		int budget = MAX_TASKS_PER_TICK;
		Runnable task;
		while (budget > 0 && (task = PENDING.poll()) != null) {
			try {
				task.run();
			} catch (Throwable throwable) {
				OutofboundMod.LOGGER.error("[outofbound] Deferred chunk task failed", throwable);
			}
			budget--;
		}
	}
}
