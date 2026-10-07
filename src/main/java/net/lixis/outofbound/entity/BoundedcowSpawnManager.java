package net.lixis.outofbound.entity;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BoundedcowSpawnManager {

	private BoundedcowSpawnManager() {
	}

	public static boolean hasAny(MinecraftServer server) {
		return !collectAll(server).isEmpty();
	}

	public static void removeAll(MinecraftServer server) {
		for (BoundedcowEntity cow : collectAll(server)) {
			cow.discard();
		}
	}

	public static void keepAtMostOne(MinecraftServer server) {
		List<BoundedcowEntity> cows = collectAll(server);
		for (int index = 1; index < cows.size(); index++) {
			cows.get(index).discard();
		}
	}

	private static List<BoundedcowEntity> collectAll(MinecraftServer server) {
		List<BoundedcowEntity> cows = new ArrayList<>();
		for (ServerLevel level : server.getAllLevels()) {
			for (Entity entity : level.getAllEntities()) {
				if (entity instanceof BoundedcowEntity boundedcow && !boundedcow.isRemoved()) {
					cows.add(boundedcow);
				}
			}
		}
		cows.sort(Comparator.comparingInt(Entity::getId));
		return cows;
	}
}
