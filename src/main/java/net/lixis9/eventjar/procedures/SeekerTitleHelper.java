package net.lixis9.eventjar.procedures;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

public final class SeekerTitleHelper {

	private SeekerTitleHelper() {
	}

	public static void warnAppeared(LevelAccessor world) {
		broadcast(world, 8, 50, 12,
				"§8it sees you",
				"§7don't make a sound");
	}

	public static void warnHuntStarts(LevelAccessor world) {
		broadcast(world, 3, 40, 10,
				"§4run",
				"§8too late to hide");
	}

	private static void broadcast(LevelAccessor world, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
		if (!(world instanceof ServerLevel level)) {
			return;
		}
		for (ServerPlayer player : level.players()) {
			player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
			player.connection.send(new ClientboundSetTitleTextPacket(Component.literal(title)));
			player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
		}
	}
}
