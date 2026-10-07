package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RndRenameProcedure {
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

	@SubscribeEvent
	public static void onEntitySpawned(EntityJoinLevelEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null || entity.level().isClientSide())
			return;
		if (entity instanceof Animal)
			return;
		if (entity.getCustomName() != null)
			return;
		if (Math.random() < (1) / ((float) 5)) {
			entity.setCustomName(Component.literal(randomName(entity.level().getRandom())));
		}
	}

	private static String randomName(RandomSource random) {
		int len = 6 + random.nextInt(5);
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}
}
