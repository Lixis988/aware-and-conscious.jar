package net.lixis9.eventjar.procedures;

import net.lixis.outofbound.world.WorldInternalConfig;
import net.lixis9.eventjar.Authorship;
import net.lixis9.eventjar.network.ClientOsEffectPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class OsEffectDayWindows {

	@SuppressWarnings("unused")
	private static final String OWNERSHIP = Authorship.NOTICE;

	private static final String NBT_ROOT = "eventjar_os_windows";

	private static final int CHECK_INTERVAL = 1200;

	private static final float ROLL_CHANCE = 0.20F;

	private enum Window {
		CALC(5, 6, ClientOsEffectPacket.Effect.CALC, "calc"),
		MEAT_LIMINAL(6, 7, ClientOsEffectPacket.Effect.MEAT_LIMINAL, "meat_liminal"),
		TXT(8, 9, ClientOsEffectPacket.Effect.TXT_DESKTOP, "txt"),
		CMD(11, 12, ClientOsEffectPacket.Effect.CMD, "cmd"),
		BATCH(14, 15, ClientOsEffectPacket.Effect.BATCH1, "batch"),
		YARLIK(17, 18, ClientOsEffectPacket.Effect.YARLIK, "yarlik"),
		PAINT(20, 21, ClientOsEffectPacket.Effect.PAINT, "paint"),
		WINMSG(23, 24, ClientOsEffectPacket.Effect.WINMSG, "winmsg"),
		WALLPAPER(26, 27, ClientOsEffectPacket.Effect.WALLPAPER, "wallpaper");

		final long dayStart;
		final long dayEndInclusive;
		final ClientOsEffectPacket.Effect effect;
		final String flag;

		Window(long dayStart, long dayEndInclusive, ClientOsEffectPacket.Effect effect, String flag) {
			this.dayStart = dayStart;
			this.dayEndInclusive = dayEndInclusive;
			this.effect = effect;
			this.flag = flag;
		}

		boolean contains(long days) {
			return days >= dayStart && days <= dayEndInclusive;
		}
	}

	private OsEffectDayWindows() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player instanceof ServerPlayer player)) {
			return;
		}
		if (player.tickCount % CHECK_INTERVAL != 0) {
			return;
		}
		MinecraftServer server = player.getServer();
		if (server == null || !WorldInternalConfig.hasBoundedcowCollision(server)) {
			return;
		}
		long days = WorldInternalConfig.getDaysSinceBoundedcowCollision(server);
		if (days <= 0L) {
			return;
		}

		CompoundTag root = player.getPersistentData().getCompound(NBT_ROOT);
		boolean dirty = false;

		for (Window window : Window.values()) {
			if (!window.contains(days)) {
				continue;
			}
			if (root.getBoolean(window.flag)) {
				continue;
			}
			boolean fire;
			if (days == window.dayStart) {
				fire = true;
			} else {
				fire = player.getRandom().nextFloat() < ROLL_CHANCE;
			}
			if (fire) {

				ClientOsEffectPacket.send(player, window.effect);
				root.putBoolean(window.flag, true);
				dirty = true;
			}
		}

		if (dirty) {
			player.getPersistentData().put(NBT_ROOT, root);
		}
	}
}
