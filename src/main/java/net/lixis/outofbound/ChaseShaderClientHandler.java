package net.lixis.outofbound;

import net.lixis.outofbound.entity.ChaserEntity;
import net.lixis.outofbound.entity.UnknownEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public class ChaseShaderClientHandler {

	private static final double CHASE_DISTANCE = 28.0D;
	private static final double CHASE_CHANCE = 0.50D;
	private static final int GRACE_TICKS = 40;

	private static int chaseGrace = 0;
	private static boolean episodeActive = false;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.level == null) {
			reset();
			return;
		}
		if (mc.isPaused()) {
			return;
		}

		boolean unknownNear = isUnknownNear(mc, player);
		boolean chaserNear = isChaserNear(mc, player);

		if (unknownNear || chaserNear) {
			chaseGrace = GRACE_TICKS;
		} else if (chaseGrace > 0) {
			chaseGrace--;
		}

		boolean chasing = chaseGrace > 0;

		if (chasing && !episodeActive) {
			episodeActive = true;
			if (unknownNear || ThreadLocalRandom.current().nextDouble() < CHASE_CHANCE) {
				ResourceLocation pick = ThreadLocalRandom.current().nextBoolean()
						? TestShaderHandler.GRAPHICAL_BUG
						: TestShaderHandler.TEXTURE_SWAP;
				TestShaderHandler.setChase(pick);
			} else {
				TestShaderHandler.setChase(null);
			}
		} else if (!chasing && episodeActive) {
			episodeActive = false;
			TestShaderHandler.setChase(null);
		}
	}

	private static boolean isUnknownNear(Minecraft mc, LocalPlayer player) {
		AABB box = player.getBoundingBox().inflate(CHASE_DISTANCE);
		List<Entity> nearby = mc.level.getEntities(player, box,
				entity -> entity instanceof UnknownEntity mob
						&& mob.isAlive() && !mob.isRemoved());
		for (Entity entity : nearby) {
			if (player.distanceTo(entity) <= CHASE_DISTANCE) {
				return true;
			}
		}
		return false;
	}

	private static boolean isChaserNear(Minecraft mc, LocalPlayer player) {
		AABB box = player.getBoundingBox().inflate(CHASE_DISTANCE);
		List<Entity> nearby = mc.level.getEntities(player, box,
				entity -> entity instanceof ChaserEntity && entity instanceof Mob mob
						&& mob.isAlive() && !mob.isRemoved());
		for (Entity entity : nearby) {
			if (player.distanceTo(entity) <= CHASE_DISTANCE) {
				return true;
			}
		}
		return false;
	}

	private static void reset() {
		chaseGrace = 0;
		if (episodeActive) {
			episodeActive = false;
			TestShaderHandler.setChase(null);
		}
	}
}
